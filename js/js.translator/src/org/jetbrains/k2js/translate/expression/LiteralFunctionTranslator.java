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

package org.jetbrains.k2js.translate.expression;

import com.google.dart.compiler.backend.js.ast.*;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor;
import org.jetbrains.jet.lang.descriptors.FunctionDescriptor;
import org.jetbrains.jet.lang.descriptors.ReceiverParameterDescriptor;
import org.jetbrains.jet.lang.psi.JetDeclarationWithBody;
import org.jetbrains.k2js.translate.context.AliasingContext;
import org.jetbrains.k2js.translate.context.Namer;
import org.jetbrains.k2js.translate.context.TranslationContext;
import org.jetbrains.k2js.translate.context.UsageTracker;
import org.jetbrains.k2js.translate.general.AbstractTranslator;

import java.util.List;

import static org.jetbrains.k2js.translate.expression.InnerDeclarationTranslator.getJsNameForCapturedReceiver;
import static org.jetbrains.k2js.translate.utils.BindingUtils.getFunctionDescriptor;
import static org.jetbrains.k2js.translate.utils.FunctionBodyTranslator.translateFunctionBody;
import static org.jetbrains.k2js.translate.utils.JsDescriptorUtils.getExpectedReceiverDescriptor;
import static org.jetbrains.k2js.translate.utils.TranslationUtils.getSuggestedName;

public class LiteralFunctionTranslator extends AbstractTranslator {
    private final JetDeclarationWithBody declaration;
    private final FunctionDescriptor descriptor;
    private final JsFunction lambda;
    private final JsFunction lambdaCreator;
    private final TranslationContext functionContext;

    private LiteralFunctionTranslator(@NotNull JetDeclarationWithBody declaration, @NotNull TranslationContext context) {
        super(context);

        this.declaration = declaration;
        this.descriptor = getFunctionDescriptor(context().bindingContext(), declaration);

        // Using for closure variables
        lambdaCreator = new JsFunction(context().scope());
        lambda = new JsFunction(lambdaCreator.getScope(), new JsBlock());
        lambdaCreator.setBody(new JsBlock(new JsReturn(lambda)));

        final UsageTracker funTracker = new UsageTracker(context().usageTracker(), descriptor);

        AliasingContext superAliasingContext = new AliasingContext(context().aliasingContext()) {
            @Nullable
            @Override
            protected JsExpression getAliasForDescriptor(@NotNull DeclarationDescriptor descriptor, boolean fromChild) {
                JsExpression alias = aliasesForDescriptors != null ? aliasesForDescriptors.get(descriptor) : null;
                if (alias != null) return alias;

                if (descriptor instanceof ReceiverParameterDescriptor) {
                    ReceiverParameterDescriptor receiverDescriptor = (ReceiverParameterDescriptor) descriptor;
                    if (funTracker.isCaptured(receiverDescriptor)) {
                        JsNameRef nameRef = getJsNameForCapturedReceiver(lambda.getScope(), receiverDescriptor).makeRef();
                        registerAlias(receiverDescriptor, nameRef);
                        return nameRef;
                    }
                }

                return super.getAliasForDescriptor(descriptor, fromChild);
            }
        };

        functionContext = context().newFunctionBody(lambdaCreator, superAliasingContext, funTracker);

        AliasingContext aliasingContext = functionContext.aliasingContext();

        DeclarationDescriptor receiverDescriptor = getExpectedReceiverDescriptor(descriptor);
        if (receiverDescriptor != null) {
            JsName receiverName = lambda.getScope().declareName(Namer.getReceiverParameterName());
            lambda.getParameters().add(new JsParameter(receiverName));
            aliasingContext.registerAlias(receiverDescriptor, receiverName.makeRef());
        }
    }

    private void translateBody() {
        JsBlock functionBody = translateFunctionBody(descriptor, declaration, functionContext);
        lambda.getBody().getStatements().addAll(functionBody.getStatements());
    }

    @NotNull
    private JsExpression finish() {
        JsExpression result;

        UsageTracker tracker = functionContext.usageTracker();
        assert tracker != null;

        JsFunction function = tracker.hasCaptured() ? lambdaCreator : lambda;

        JsNameRef funReference = context().define(getSuggestedName(functionContext, descriptor), function);

        InnerDeclarationTranslator innerTranslator = new InnerDeclarationTranslator(functionContext, context(), function);
        result = innerTranslator.translate(funReference);

        FunctionTranslator.addParameters(lambda.getParameters(), descriptor, functionContext);

        return result;
    }

    @NotNull
    private JsExpression translate() {
        translateBody();
        return finish();
    }

    @NotNull
    public JsVars translateLocalNamedFunction() {
        // Add ability to capture this named function.
        // Will be available like `foo.v` (for function `foo`)
        // Can not generate direct call because function may have some closures.
        JsName funName = functionContext.getNameForDescriptor(descriptor);
        functionContext.aliasingContext().registerAlias(descriptor, funName.makeRef());

        translateBody();

        UsageTracker funTracker = functionContext.usageTracker();
        assert funTracker != null;
        boolean funIsCaptured = funTracker.isCaptured(descriptor);

        if (funIsCaptured) {
            //TODO remove extra parameter in this case
            lambda.setName(funName);
        }

        JsExpression result = finish();

        List<JsVars.JsVar> vars = new SmartList<JsVars.JsVar>();
        JsVars.JsVar fun = new JsVars.JsVar(funName, result);
        vars.add(fun);

        return new JsVars(vars, /*mulitline =*/ false);
    }

    @NotNull
    public static JsVars translateLocalNamedFunction(@NotNull JetDeclarationWithBody declaration, @NotNull TranslationContext outerContext) {
        return new LiteralFunctionTranslator(declaration, outerContext).translateLocalNamedFunction();
    }

    @NotNull
    public static JsExpression translate(@NotNull JetDeclarationWithBody declaration, @NotNull TranslationContext outerContext) {
        return new LiteralFunctionTranslator(declaration, outerContext).translate();
    }
}
