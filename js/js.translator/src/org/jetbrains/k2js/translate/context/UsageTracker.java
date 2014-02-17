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

package org.jetbrains.k2js.translate.context;

import com.intellij.util.Consumer;
import com.intellij.util.SmartList;
import com.intellij.util.containers.OrderedSet;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.*;

import java.util.List;
import java.util.Set;

public final class UsageTracker {
    private final MemberDescriptor containingDescriptor;
    private final List<UsageTracker> children = new SmartList<UsageTracker>();

    private final Set<CallableDescriptor> capturedVariables = new OrderedSet<CallableDescriptor>();

    public UsageTracker(@Nullable UsageTracker parent, @NotNull MemberDescriptor containingDescriptor) {
        this.containingDescriptor = containingDescriptor;

        if (parent != null) {
            parent.addChild(this);
        }
    }

    private void addChild(@NotNull UsageTracker child) {
        children.add(child);
    }

    private void used(@Nullable CallableDescriptor descriptor) {
        if (descriptor == null) return;
        capturedVariables.add(descriptor);
    }

    public void triggerUsed(@NotNull DeclarationDescriptor descriptor) {
        // optimization
        if (!(descriptor instanceof CallableDescriptor) || capturedVariables.contains(descriptor)) return;

        if (descriptor instanceof CallableMemberDescriptor) {
            // TODO ???
            if (!isAncestor(containingDescriptor, descriptor)) {
                CallableMemberDescriptor callableMemberDescriptor = (CallableMemberDescriptor) descriptor;
                used(callableMemberDescriptor.getExpectedThisObject());
                used(callableMemberDescriptor.getReceiverParameter());

                // local named function
                if (callableMemberDescriptor.getVisibility() == Visibilities.LOCAL && !isAncestor(containingDescriptor, descriptor)) {
                    used(callableMemberDescriptor);
                }
            }
        }
        else if (descriptor instanceof VariableDescriptor) {
            VariableDescriptor variableDescriptor = (VariableDescriptor) descriptor;
            if (!capturedVariables.contains(variableDescriptor) && !isAncestor(containingDescriptor, variableDescriptor)) {
                used(variableDescriptor);
            }
        }
        // TODO test isAncestor cases
        else if (descriptor instanceof ReceiverParameterDescriptor && !isAncestor(containingDescriptor, descriptor)) {
            used((ReceiverParameterDescriptor) descriptor);
        }
    }

    public void forEachCaptured(@NotNull Consumer<CallableDescriptor> consumer) {
        forEachCaptured(containingDescriptor, new THashSet<CallableDescriptor>(), consumer);
    }

    private void forEachCaptured(
            @NotNull MemberDescriptor containingDescriptor,
            @NotNull Set<CallableDescriptor> visited,
            @NotNull Consumer<CallableDescriptor> consumer
    ) {
        for (CallableDescriptor callableDescriptor : capturedVariables) {
            if (!isAncestor(containingDescriptor, callableDescriptor) && visited.add(callableDescriptor)) {
                consumer.consume(callableDescriptor);
            }
        }
        for (UsageTracker child : children) {
            child.forEachCaptured(containingDescriptor, visited, consumer);
        }
    }

    public boolean hasCaptured() {
        if (!capturedVariables.isEmpty()) return true;

        for (UsageTracker child : children) {
            if (child.hasCaptured()) return true;
        }

        return false;
    }

    public boolean isCaptured(@NotNull CallableDescriptor descriptor) {
        if (capturedVariables.contains(descriptor)) return true;

        for (UsageTracker child : children) {
            if (child.isCaptured(descriptor)) return true;
        }

        return false;
    }

    // differs from DescriptorUtils - fails if reach PackageFragmentDescriptor
    private static boolean isAncestor(
            @NotNull DeclarationDescriptor ancestor,
            @NotNull DeclarationDescriptor declarationDescriptor
    ) {
        DeclarationDescriptor descriptor = declarationDescriptor.getContainingDeclaration();
        while (descriptor != null && !(descriptor instanceof PackageFragmentDescriptor)) {
            if (ancestor == descriptor) {
                return true;
            }
            descriptor = descriptor.getContainingDeclaration();
        }
        return false;
    }
}
