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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.asm4.Type;
import org.jetbrains.asm4.tree.MethodNode;
import org.jetbrains.jet.codegen.AsmUtil;
import org.jetbrains.jet.codegen.binding.CalculatedClosure;
import org.jetbrains.jet.codegen.context.EnclosedValueDescriptor;
import org.jetbrains.jet.codegen.state.JetTypeMapper;
import org.jetbrains.jet.lang.descriptors.ClassDescriptor;
import org.jetbrains.jet.lang.descriptors.FunctionDescriptor;
import org.jetbrains.jet.lang.psi.JetFunctionLiteral;
import org.jetbrains.jet.lang.psi.JetFunctionLiteralExpression;
import org.jetbrains.jet.lang.resolve.BindingContext;

import java.util.*;

import static org.jetbrains.jet.codegen.binding.CodegenBinding.CLOSURE;
import static org.jetbrains.jet.codegen.binding.CodegenBinding.anonymousClassForFunction;
import static org.jetbrains.jet.codegen.binding.CodegenBinding.asmTypeForAnonymousClass;

public class LambdaInfo {

    public final JetFunctionLiteralExpression expression;

    @NotNull private final JetTypeMapper typeMapper;

    public final CalculatedClosure closure;

    private MethodNode node;

    private List<CapturedParamInfo> capturedVars;

    private final FunctionDescriptor functionDescriptor;

    private final ClassDescriptor classDescriptor;

    private final Type closureClassType;

    private int paramOffset;

    LambdaInfo(@NotNull JetFunctionLiteralExpression expression, @NotNull JetTypeMapper typeMapper) {
        this.expression = expression;
        this.typeMapper = typeMapper;
        BindingContext bindingContext = typeMapper.getBindingContext();
        functionDescriptor = bindingContext.get(BindingContext.FUNCTION, expression.getFunctionLiteral());
        assert functionDescriptor != null : "Function is not resolved to descriptor: " + expression.getText();

        classDescriptor = anonymousClassForFunction(bindingContext, functionDescriptor);
        closureClassType = asmTypeForAnonymousClass(bindingContext, functionDescriptor);

        closure = bindingContext.get(CLOSURE, classDescriptor);

    }

    public MethodNode getNode() {
        return node;
    }

    public void setNode(MethodNode node) {
        this.node = node;
    }

    public FunctionDescriptor getFunctionDescriptor() {
        return functionDescriptor;
    }

    public JetFunctionLiteral getFunctionLiteral() {
        return expression.getFunctionLiteral();
    }

    public ClassDescriptor getClassDescriptor() {
        return classDescriptor;
    }

    public Type getLambdaClassType() {
        return closureClassType;
    }

    public List<CapturedParamInfo> getCapturedVars() {
        //lazy initialization cause it would be calculated after object creation
        int index = 0;
        if (capturedVars == null) {
            capturedVars = new ArrayList<CapturedParamInfo>();

            if (closure.getCaptureThis() != null) {
                EnclosedValueDescriptor descriptor = new EnclosedValueDescriptor(AsmUtil.CAPTURED_THIS_FIELD, null, null, typeMapper.mapType(closure.getCaptureThis()));
                capturedVars.add(getCapturedParamInfo(descriptor, index));
                index += descriptor.getType().getSize();
            }

            if (closure.getCaptureReceiverType() != null) {
                EnclosedValueDescriptor descriptor = new EnclosedValueDescriptor(AsmUtil.CAPTURED_RECEIVER_FIELD, null, null, typeMapper.mapType(closure.getCaptureReceiverType()));
                capturedVars.add(getCapturedParamInfo(descriptor, index));
                index += descriptor.getType().getSize();
            }

            if (closure != null) {
                for (EnclosedValueDescriptor descriptor : closure.getCaptureVariables().values()) {
                    capturedVars.add(getCapturedParamInfo(descriptor, index));
                    index += descriptor.getType().getSize();
                }
            }
        }
        return capturedVars;
    }

    @NotNull
    public static CapturedParamInfo getCapturedParamInfo(@NotNull EnclosedValueDescriptor descriptor, int index) {
        return new CapturedParamInfo(descriptor.getFieldName(), descriptor.getType(), false, index, -1);
    }

    private void shiftParams(int shift) {
        for (CapturedParamInfo var : getCapturedVars()) {
            var.setShift(shift);
        }
    }

    public int getParamOffset() {
        return paramOffset;
    }

    public void setParamOffset(int paramOffset) {
        this.paramOffset = paramOffset;
        shiftParams(paramOffset);
    }

    public List<Type> getParamsWithoutCapturedValOrVar() {
        Type[] types = typeMapper.mapSignature(functionDescriptor).getAsmMethod().getArgumentTypes();
        return Arrays.asList(types);
    }

    public int getCapturedVarsSize() {
        int size = 0;
        for (CapturedParamInfo next : getCapturedVars()) {
            size += next.getType().getSize();
        }
        return size;
    }
}
