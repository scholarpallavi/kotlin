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

package org.jetbrains.jet.codegen.inline;

import org.jetbrains.asm4.ClassVisitor;
import org.jetbrains.asm4.Type;
import org.jetbrains.jet.codegen.CallableMethod;
import org.jetbrains.jet.codegen.StackValue;
import org.jetbrains.jet.lang.descriptors.ValueParameterDescriptor;
import org.jetbrains.jet.lang.psi.JetExpression;
import org.jetbrains.jet.lang.psi.JetFunctionLiteralExpression;

public interface Inliner {

    Inliner NOT_INLINE = new Inliner() {
        @Override
        public void inlineCall(
                CallableMethod callableMethod, ClassVisitor visitor
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putInLocal(Type type, StackValue stackValue, ValueParameterDescriptor valueParameterDescriptor) {

        }

        @Override
        public void putCapturedInLocal(
                Type type, StackValue stackValue, ValueParameterDescriptor valueParameterDescriptor, int index
        ) {

        }

        @Override
        public void leaveTemps() {

        }

        @Override
        public boolean isInliningClosure(JetExpression expression, ValueParameterDescriptor valueParameterDescriptora) {
            return false;
        }

        @Override
        public void rememberClosure(JetFunctionLiteralExpression expression, Type type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putHiddenParams() {

        }

        @Override
        public boolean shouldPutValue(
                Type type, StackValue stackValue,
                ValueParameterDescriptor descriptor
        ) {
            return true;
        }
    };

    void inlineCall(CallableMethod callableMethod, ClassVisitor visitor);

    void putInLocal(Type type, StackValue stackValue, ValueParameterDescriptor valueParameterDescriptor);

    void putCapturedInLocal(Type type, StackValue stackValue, ValueParameterDescriptor valueParameterDescriptor, int index);

    boolean shouldPutValue(Type type, StackValue stackValue, ValueParameterDescriptor descriptor);

    void putHiddenParams();

    void leaveTemps();

    boolean isInliningClosure(JetExpression expression, ValueParameterDescriptor valueParameterDescriptora);

    void rememberClosure(JetFunctionLiteralExpression expression, Type type);
}
