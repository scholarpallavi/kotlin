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

import org.jetbrains.asm4.Opcodes;
import org.jetbrains.asm4.Type;
import org.jetbrains.asm4.tree.AbstractInsnNode;
import org.jetbrains.asm4.tree.FieldInsnNode;
import org.jetbrains.asm4.tree.MethodNode;
import org.jetbrains.asm4.tree.VarInsnNode;

import static org.jetbrains.jet.codegen.asm.MethodInliner.getPreviousNoLabelNoLine;

public class InlineFieldRemapper extends LambdaFieldRemapper {

    private String oldOwnerType;

    private String newOwnerType;

    public InlineFieldRemapper(String oldOwnerType, String newOwnerType) {
        this.oldOwnerType = oldOwnerType;
        this.newOwnerType = newOwnerType;
    }

    @Override
    public AbstractInsnNode doTransform(
            MethodNode node, FieldInsnNode fieldInsnNode, CapturedParamInfo capturedField
    ) {
        AbstractInsnNode prev = getPreviousNoLabelNoLine(fieldInsnNode);

        assert prev.getType() == AbstractInsnNode.VAR_INSN;
        VarInsnNode loadThis = (VarInsnNode) prev;
        assert /*loadThis.var == info.getCapturedVarsSize() - 1 && */loadThis.getOpcode() == Opcodes.ALOAD;

        int opcode = Opcodes.GETSTATIC;

        String descriptor = Type.getObjectType(newOwnerType).getDescriptor();

        FieldInsnNode thisStub = new FieldInsnNode(opcode, newOwnerType, "$$$this", descriptor);

        node.instructions.insertBefore(loadThis, thisStub);
        node.instructions.remove(loadThis);

        fieldInsnNode.owner = newOwnerType;
        fieldInsnNode.name = LambdaTransformer.getNewFieldName(fieldInsnNode.name);

        return fieldInsnNode;
    }
}
