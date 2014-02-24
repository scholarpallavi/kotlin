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

import org.jetbrains.asm4.Type;

import java.util.List;
import java.util.Map;

public class ConstructorInvocation {

    private final String ownerInternalName;

    private final Map<Integer, InlinableAccess> access;

    private Type newLambdaType;

    private String newConstructorDescriptor;

    private List<CapturedParamInfo> recaptured;

    private Map<String, LambdaInfo> recapturedLambdas;

    ConstructorInvocation(String ownerInternalName, Map<Integer, InlinableAccess> access) {
        this.ownerInternalName = ownerInternalName;
        this.access = access;
    }

    public String getOwnerInternalName() {
        return ownerInternalName;
    }

    public boolean isInlinable() {
        return !access.isEmpty();
    }

    public Map<Integer, InlinableAccess> getAccess() {
        return access;
    }


    public Type getNewLambdaType() {
        return newLambdaType;
    }

    public void setNewLambdaType(Type newLambdaType) {
        this.newLambdaType = newLambdaType;
    }

    public String getNewConstructorDescriptor() {
        return newConstructorDescriptor;
    }

    public void setNewConstructorDescriptor(String newConstructorDescriptor) {
        this.newConstructorDescriptor = newConstructorDescriptor;
    }

    public List<CapturedParamInfo> getRecaptured() {
        return recaptured;
    }

    public void setRecaptured(List<CapturedParamInfo> recaptured) {
        this.recaptured = recaptured;
    }

    public Map<String, LambdaInfo> getRecapturedLambdas() {
        return recapturedLambdas;
    }

    public void setRecapturedLambdas(Map<String, LambdaInfo> recapturedLambdas) {
        this.recapturedLambdas = recapturedLambdas;
    }
}
