/*
 * Copyright 2010-2014 JetBrains s.r.o.
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

package org.jetbrains.jet.plugin.vfilefinder;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.file.impl.JavaFileManager;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.kotlin.ClassFileFinder;


public class IDEClassFileFinder implements ClassFileFinder {

    private static final Logger LOG = Logger.getInstance(IDEClassFileFinder.class);

    @NotNull private final Project project;

    public IDEClassFileFinder(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public VirtualFile find(@NotNull String internalName) {
        JavaFileManager fileFinder = ServiceManager.getService(project, JavaFileManager.class);

        String qName = internalName.replace('/', '.');
        PsiClass psiClass = fileFinder.findClass(qName, GlobalSearchScope.allScope(project));
        if (psiClass == null) {
            int dollarIndex = qName.indexOf('$');
            String newName = qName.substring(0, dollarIndex);
            psiClass = fileFinder.findClass(newName, GlobalSearchScope.allScope(project));
            if (psiClass != null) {
                int i = qName.lastIndexOf('.');
                VirtualFile child = psiClass.getContainingFile().getVirtualFile().getParent().findChild((i < 0 ? qName : qName.substring(i + 1)) + ".class");
                return child;
            }
        }
        if (psiClass != null) {
            return psiClass.getContainingFile().getVirtualFile();
        }
        return null;
    }
}
