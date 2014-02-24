/*
* Copyright 2010-2013 JetBrains s.r.o.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.jetbrains.jet.codegen.asm;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.asm4.ClassVisitor;
import org.jetbrains.asm4.Opcodes;
import org.jetbrains.asm4.Type;
import org.jetbrains.asm4.commons.Method;
import org.jetbrains.asm4.tree.MethodNode;
import org.jetbrains.asm4.util.Textifier;
import org.jetbrains.asm4.util.TraceMethodVisitor;
import org.jetbrains.jet.codegen.*;
import org.jetbrains.jet.codegen.context.CodegenContext;
import org.jetbrains.jet.codegen.context.MethodContext;
import org.jetbrains.jet.codegen.context.PackageContext;
import org.jetbrains.jet.codegen.signature.JvmMethodParameterKind;
import org.jetbrains.jet.codegen.signature.JvmMethodParameterSignature;
import org.jetbrains.jet.codegen.signature.JvmMethodSignature;
import org.jetbrains.jet.codegen.state.GenerationState;
import org.jetbrains.jet.codegen.state.JetTypeMapper;
import org.jetbrains.jet.descriptors.serialization.descriptors.DeserializedSimpleFunctionDescriptor;
import org.jetbrains.jet.lang.descriptors.*;
import org.jetbrains.jet.lang.descriptors.impl.AnonymousFunctionDescriptor;
import org.jetbrains.jet.lang.psi.*;
import org.jetbrains.jet.lang.resolve.BindingContext;
import org.jetbrains.jet.lang.resolve.BindingContextUtils;
import org.jetbrains.jet.lang.resolve.java.AsmTypeConstants;
import org.jetbrains.jet.lang.types.lang.InlineUtil;
import org.jetbrains.jet.renderer.DescriptorRenderer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static org.jetbrains.jet.codegen.AsmUtil.getMethodAsmFlags;
import static org.jetbrains.jet.codegen.AsmUtil.isPrimitive;
import static org.jetbrains.jet.codegen.binding.CodegenBinding.asmTypeForAnonymousClass;

public class InlineCodegen implements ParentCodegenAware, Inliner {

    private final JetTypeMapper typeMapper;

    private final ExpressionCodegen codegen;

    private final boolean notSeparateInline;

    private final GenerationState state;

    private final boolean disabled;

    private final Call call;

    private final SimpleFunctionDescriptor functionDescriptor;

    private final BindingContext bindingContext;

    private final MethodContext context;

    private final FrameMap originalFunctionFrame;

    private final int initialFrameSize;

    private final JvmMethodSignature jvmSignature;

    private LambdaInfo activeLambda;

    protected final List<ParameterInfo> tempTypes = new ArrayList<ParameterInfo>();

    protected final Map<Integer, LambdaInfo> expressionMap = new HashMap<Integer, LambdaInfo>();

    public InlineCodegen(
            @NotNull ExpressionCodegen codegen,
            boolean notSeparateInline,
            @NotNull GenerationState state,
            boolean disabled,
            @NotNull SimpleFunctionDescriptor functionDescriptor,
            @NotNull Call call
    ) {
        this.state = state;
        this.typeMapper = state.getTypeMapper();

        this.codegen = codegen;
        this.notSeparateInline = notSeparateInline;
        this.disabled = disabled;
        this.call = call;
        this.functionDescriptor = functionDescriptor.getOriginal();
        bindingContext = codegen.getBindingContext();
        initialFrameSize = codegen.getFrameMap().getCurrentSize();

        context = (MethodContext) getContext(functionDescriptor, state);
        originalFunctionFrame = context.prepareFrame(typeMapper);
        jvmSignature = typeMapper.mapSignature(functionDescriptor, context.getContextKind());
    }


    @Override
    public void inlineCall(CallableMethod callableMethod, ClassVisitor visitor) {
        MethodNode node = null;

        try {
            node = createMethodNode(callableMethod);
            inlineCall(node);
        }
        catch (CompilationException e) {
            throw e;
        }
        catch (Exception e) {
            String text = getNodeText(node);
            PsiElement element = BindingContextUtils.descriptorToDeclaration(bindingContext, codegen.getContext().getContextDescriptor());
            throw new CompilationException("Couldn't inline method call '" +
                                       functionDescriptor.getName() +
                                       "' into \n" + (element != null ? element.getText() : "null psi element " + codegen.getContext().getContextDescriptor()) +
                                       "\ncause: " +
                                       text, e, call.getCallElement());
        }
    }

    @NotNull
    private MethodNode createMethodNode(CallableMethod callableMethod)
            throws ClassNotFoundException, IOException {
        MethodNode node;
        if (functionDescriptor instanceof DeserializedSimpleFunctionDescriptor) {
            VirtualFile file = InlineCodegenUtil.getVirtualFileForCallable((DeserializedSimpleFunctionDescriptor) functionDescriptor, state);
            node = InlineCodegenUtil.getMethodNode(file.getInputStream(), functionDescriptor.getName().asString(),
                                                   callableMethod.getAsmMethod().getDescriptor());

            if (node == null) {
                throw new RuntimeException("Couldn't obtain compiled function body for " + descriptorName(functionDescriptor));
            }
        }
        else {
            PsiElement element = BindingContextUtils.descriptorToDeclaration(bindingContext, functionDescriptor);

            if (element == null) {
                throw new RuntimeException("Couldn't find declaration for function " + descriptorName(functionDescriptor));
            }

            JvmMethodSignature jvmSignature = typeMapper.mapSignature(functionDescriptor, context.getContextKind());
            Method asmMethod = jvmSignature.getAsmMethod();
            node = new MethodNode(Opcodes.ASM4,
                                           getMethodAsmFlags(functionDescriptor, context.getContextKind()),
                                           asmMethod.getName(),
                                           asmMethod.getDescriptor(),
                                           jvmSignature.getGenericsSignature(),
                                           null);

            FunctionCodegen.generateMethodBody(node, functionDescriptor, context.getParentContext().intoFunction(functionDescriptor),
                                               jvmSignature,
                                               new FunctionGenerationStrategy.FunctionDefault(state,
                                                                                              functionDescriptor,
                                                                                              (JetDeclarationWithBody) element),
                                               getParentCodegen());
            //TODO
            node.visitMaxs(30, 30);
            node.visitEnd();
        }
        return node;
    }

    private void inlineCall(MethodNode node) {
        generateClosuresBodies();

        List<ParameterInfo> realParams = new ArrayList<ParameterInfo>(tempTypes);

        putClosureParametersOnStack();

        List<CapturedParamInfo> captured = getAllCaptured();

        Parameters parameters = new Parameters(realParams, Parameters.addStubs(captured, realParams.size()));

        InliningInfo info =
                new InliningInfo(expressionMap, null, null, null, state,
                                 codegen.getInlineNameGenerator().subGenerator(functionDescriptor.getName().asString()),
                                 codegen.getContext(), call);
        MethodInliner inliner = new MethodInliner(node, parameters, info, null, new LambdaFieldRemapper()); //with captured

        VarRemapper.ParamRemapper remapper = new VarRemapper.ParamRemapper(parameters, initialFrameSize);

        inliner.doTransformAndMerge(codegen.getInstructionAdapter(), remapper);
        generateClosuresBodies();
    }


    private void generateClosuresBodies() {
        for (LambdaInfo info : expressionMap.values()) {
            info.setNode(generateClosureBody(info));
        }
    }

    private MethodNode generateClosureBody(LambdaInfo info) {
        JetFunctionLiteral declaration = info.getFunctionLiteral();
        FunctionDescriptor descriptor = info.getFunctionDescriptor();

        MethodContext parentContext = codegen.getContext();

        MethodContext context = parentContext.intoClosure(descriptor, codegen, typeMapper).intoFunction(descriptor);
        context.setInlineClosure(true);

        JvmMethodSignature jvmMethodSignature = typeMapper.mapSignature(descriptor);
        Method asmMethod = jvmMethodSignature.getAsmMethod();
        MethodNode methodNode = new MethodNode(Opcodes.ASM4, getMethodAsmFlags(descriptor, context.getContextKind()), asmMethod.getName(), asmMethod.getDescriptor(), jvmMethodSignature.getGenericsSignature(), null);


        //AnalyzerAdapter adapter = new AnalyzerAdapter("fake", methodNode.access, methodNode.name, methodNode.desc, methodNode);
        MethodNode adapter = methodNode;

        FunctionCodegen.generateMethodBody(adapter, descriptor, context, jvmMethodSignature, new FunctionGenerationStrategy.FunctionDefault(state, descriptor, declaration) {
            @Override
            public boolean generateLocalVarTable() {
                return false;
            }
        }, codegen.getParentCodegen());
        adapter.visitMaxs(30, 30);

        return methodNode;
    }



    @Override
    public void putInLocal(@NotNull Type type, @Nullable StackValue stackValue, ValueParameterDescriptor valueParameterDescriptor) {
        putCapturedInLocal(type, stackValue, valueParameterDescriptor, -1);
    }

    @Override
    public void putCapturedInLocal(
            @NotNull Type type, @Nullable StackValue stackValue, @Nullable ValueParameterDescriptor valueParameterDescriptor, int index
    ) {
        if (!disabled && notSeparateInline && Type.VOID_TYPE != type) {
            //TODO remap only inlinable closure => otherwise we could get a lot of problem
            boolean couldBeRemapped = !shouldPutValue(type, stackValue, codegen.getContext(), valueParameterDescriptor);
            StackValue remappedIndex = couldBeRemapped ? stackValue : null;

            ParameterInfo info = new ParameterInfo(type, false, couldBeRemapped ? -1 : codegen.getFrameMap().enterTemp(type), remappedIndex);

            if (index >= 0 && couldBeRemapped) {
                CapturedParamInfo capturedParamInfo = activeLambda.getCapturedVars().get(index);
                capturedParamInfo.setRemapIndex(remappedIndex != null ? remappedIndex : StackValue.local(info.getIndex(), info.getType()));
            }

            doWithParameter(info);
        }
    }

    @Override
    public boolean shouldPutValue(@NotNull Type type, @Nullable StackValue stackValue, MethodContext context, ValueParameterDescriptor descriptor) {
        //boolean isInline = true/*context.isInlineFunction() || context.getParentContext() instanceof ClosureContext*/;
        //if (stackValue != null && isInline && stackValue instanceof StackValue.Local) {
        //    if (isInvokeOnInlinable(type.getClassName(), "invoke") && (descriptor == null || !InlineUtil.hasNoinlineAnnotation(descriptor))) {
        //        //TODO remap only inlinable closure => otherwise we could get a lot of problem
        //        return false;
        //    }
        //}

        //remap only inline functions (and maybe non primitives)
        //TODO - clean asserion and remapping logic
        if (stackValue == null || isPrimitive(type) ^ isPrimitive(stackValue.type)) {
            //don't remap boxing/unboxing primitives - lost identity and perfomance
            return true;
        }

        boolean shouldPut = !(stackValue != null && stackValue instanceof StackValue.Local);
        if (shouldPut) {
            //we could recapture field of anonymous objects cause they couldn't change
            boolean isInlineClosure = codegen.getContext().isInlineClosure();
            if (isInlineClosure && codegen.getContext().getContextDescriptor() instanceof AnonymousFunctionDescriptor) {
                Type internalName = asmTypeForAnonymousClass(bindingContext, (FunctionDescriptor) codegen.getContext().getContextDescriptor());

                String owner = null;
                if (stackValue instanceof StackValue.Field) {
                    owner = ((StackValue.Field) stackValue).owner.getInternalName();
                }

                if (stackValue instanceof StackValue.Composed) {
                    //go through aload 0
                    owner = internalName.getInternalName();
                }

                if (descriptor != null && !InlineUtil.hasNoinlineAnnotation(descriptor) && internalName.getInternalName().equals(owner)) {
                    //check type of context
                    return false;
                }
            }
        }
        return shouldPut;
    }

    private void doWithParameter(ParameterInfo info) {
        recordParamInfo(info, true);
        putParameterOnStack(info);
    }

    private int recordParamInfo(ParameterInfo info, boolean addToFrame) {
        Type type = info.type;
        tempTypes.add(info);
        if (info.getType().getSize() == 2) {
            tempTypes.add(ParameterInfo.STUB);
        }
        if (addToFrame) {
            return originalFunctionFrame.enterTemp(type);
        }
        return -1;
    }

    private void putParameterOnStack(ParameterInfo info) {
        if (!info.isSkippedOrRemapped()) {
            int index = info.getIndex();
            Type type = info.type;
            StackValue.local(index, type).store(type, codegen.getInstructionAdapter());
        }
    }

    @Override
    public void putHiddenParams() {
        List<JvmMethodParameterSignature> types = jvmSignature.getValueParameters();

        if (!isStaticMethod(functionDescriptor, context)) {
            Type type = AsmTypeConstants.OBJECT_TYPE;
            ParameterInfo info = new ParameterInfo(type, false, codegen.getFrameMap().enterTemp(type), -1);
            recordParamInfo(info, false);
        }

        for (JvmMethodParameterSignature param : types) {
            if (param.getKind() == JvmMethodParameterKind.VALUE) {
                break;
            }
            Type type = param.getAsmType();
            ParameterInfo info = new ParameterInfo(type, false, codegen.getFrameMap().enterTemp(type), -1);
            recordParamInfo(info, false);
        }

        for (ListIterator<? extends ParameterInfo> iterator = tempTypes.listIterator(tempTypes.size()); iterator.hasPrevious(); ) {
            ParameterInfo param = iterator.previous();
            putParameterOnStack(param);
        }
    }

    @Override
    public void leaveTemps() {
        FrameMap frameMap = codegen.getFrameMap();
        for (ListIterator<? extends ParameterInfo> iterator = tempTypes.listIterator(tempTypes.size()); iterator.hasPrevious(); ) {
            ParameterInfo param = iterator.previous();
            if (!param.isSkippedOrRemapped()) {
                frameMap.leaveTemp(param.type);
            }
        }
    }

    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public boolean isInliningClosure(JetExpression expression, ValueParameterDescriptor valueParameterDescriptora) {
        return !disabled &&
               expression instanceof JetFunctionLiteralExpression &&
               !InlineUtil.hasNoinlineAnnotation(valueParameterDescriptora);
    }

    @Override
    public void rememberClosure(JetFunctionLiteralExpression expression, Type type) {
        ParameterInfo closureInfo = new ParameterInfo(type, true, -1, -1);
        int index = recordParamInfo(closureInfo, true);

        LambdaInfo info = new LambdaInfo(expression, typeMapper);
        expressionMap.put(index, info);

        closureInfo.setLambda(info);
    }

    private void putClosureParametersOnStack() {
        //TODO: SORT
        int currentSize = tempTypes.size();
        for (LambdaInfo next : expressionMap.values()) {
            if (next.closure != null) {
                activeLambda = next;
                next.setParamOffset(currentSize);
                codegen.pushClosureOnStack(next.closure, false, this);
                currentSize += next.getCapturedVarsSize();
            }
        }
        activeLambda = null;
    }

    private List<CapturedParamInfo> getAllCaptured() {
        //TODO: SORT
        List<CapturedParamInfo> result = new ArrayList<CapturedParamInfo>();
        for (LambdaInfo next : expressionMap.values()) {
            if (next.closure != null) {
                result.addAll(next.getCapturedVars());
            }
        }
        return result;
    }

    @Nullable
    @Override
    public MemberCodegen getParentCodegen() {
        return codegen.getParentCodegen();
    }

    public static CodegenContext getContext(DeclarationDescriptor descriptor, GenerationState state) {
        if (descriptor instanceof PackageFragmentDescriptor) {
            return new PackageContext((PackageFragmentDescriptor) descriptor, null, null);
        }

        CodegenContext parent = getContext(descriptor.getContainingDeclaration(), state);

        if (descriptor instanceof ClassDescriptor) {
            return parent.intoClass((ClassDescriptor) descriptor, OwnerKind.IMPLEMENTATION, state);
        }
        else if (descriptor instanceof FunctionDescriptor) {
            return parent.intoFunction((FunctionDescriptor) descriptor);
        }

        throw new IllegalStateException("Coudn't build context for " + descriptorName(descriptor));
    }

    private static boolean isStaticMethod(FunctionDescriptor functionDescriptor, MethodContext context) {
        return (getMethodAsmFlags(functionDescriptor, context.getContextKind()) & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
    }

    @NotNull
    public static String getNodeText(@Nullable MethodNode node) {
        if (node == null) {
            return "Not generated";
        }
        Textifier p = new Textifier();
        node.accept(new TraceMethodVisitor(p));
        StringWriter sw = new StringWriter();
        p.print(new PrintWriter(sw));
        sw.flush();
        return node.name + ": \n " + sw.getBuffer().toString();
    }

    private static String descriptorName(DeclarationDescriptor descriptor) {
        return DescriptorRenderer.SHORT_NAMES_IN_TYPES.render(descriptor);
    }
}
