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
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.descriptors.*;
import org.jetbrains.jet.lang.resolve.name.Name;
import org.jetbrains.k2js.translate.context.TranslationContext;
import org.jetbrains.k2js.translate.context.UsageTracker;

import java.util.List;

public class InnerDeclarationTranslator {
    protected final TranslationContext context;
    protected final JsFunction fun;
    private final TranslationContext invokeContext;

    public InnerDeclarationTranslator(
            @NotNull TranslationContext context,
            @NotNull TranslationContext invokeContext,
            @NotNull JsFunction fun
    ) {
        this.context = context;
        this.invokeContext = invokeContext;
        this.fun = fun;
    }

    @NotNull
    public JsExpression translate(@NotNull JsNameRef nameRef) {
        UsageTracker usageTracker = context.usageTracker();
        assert usageTracker != null : "Usage tracker should not be null for InnerDeclarationTranslator";

        if (!usageTracker.hasCaptured()) {
            return nameRef;
        }

        JsInvocation invocation = new JsInvocation(nameRef);
        final List<JsExpression> invocationArguments = invocation.getArguments();
        usageTracker.forEachCaptured(new Consumer<CallableDescriptor>() {
            @Override
            public void consume(CallableDescriptor descriptor) {
                fun.getParameters().add(new JsParameter(getParameterNameForDeclaration(descriptor)));
                invocationArguments.add(getParameterNameRefForInvocation(descriptor));
            }
        });

        return invocation;
    }

    @NotNull
    protected JsName getParameterNameForDeclaration(@NotNull CallableDescriptor descriptor) {
        if (descriptor instanceof ReceiverParameterDescriptor) {
            return getJsNameForCapturedReceiver(context.scope(), (ReceiverParameterDescriptor) descriptor);
        }

        return context.getNameForDescriptor(descriptor);
    }

    @NotNull
    protected JsExpression getParameterNameRefForInvocation(@NotNull CallableDescriptor descriptor) {
        // TODO think tests when it will fail?
        if (descriptor instanceof VariableDescriptor) {
            return invokeContext.getNameForDescriptor(descriptor).makeRef();
        }
        //if (descriptor instanceof ReceiverParameterDescriptor) {
        JsExpression alias = invokeContext.getAliasForDescriptor(descriptor);
        if (alias != null) return alias;
        //}

        // TODO ???
        if (descriptor instanceof ReceiverParameterDescriptor) {
            return JsLiteral.THIS;
        }

        return invokeContext.getNameForDescriptor(descriptor).makeRef();
    }

    public static JsName getJsNameForCapturedReceiver(JsScope scope, ReceiverParameterDescriptor receiverDescriptor) {
        String namePrefix = "this$";
        String nameSuffix = "";
        ClassifierDescriptor classifierDescriptor = receiverDescriptor.getType().getConstructor().getDeclarationDescriptor();
        assert classifierDescriptor != null;

        if (classifierDescriptor instanceof ClassDescriptor) {
            ClassDescriptor classDescriptor = (ClassDescriptor) classifierDescriptor;
            if (classDescriptor.getKind() == ClassKind.CLASS_OBJECT) {
                classifierDescriptor = (ClassDescriptor) classDescriptor.getContainingDeclaration();
                //TODO think about better name
                nameSuffix = "$";
            }
        }

        Name name = classifierDescriptor.getName();
        assert !name.isSpecial();

        String suggestedName = namePrefix + name.asString() + nameSuffix;

        //TODO Do we need *fresh* name?
        return scope.declareFreshName(suggestedName);

    }
}
