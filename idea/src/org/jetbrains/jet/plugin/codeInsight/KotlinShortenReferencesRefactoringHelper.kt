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

package org.jetbrains.jet.plugin.codeInsight

import com.intellij.refactoring.RefactoringHelper
import org.jetbrains.jet.lang.psi.JetElement
import com.intellij.usageView.UsageInfo
import com.intellij.openapi.project.Project
import java.util.HashSet
import com.intellij.openapi.util.Key
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ApplicationAdapter
import com.intellij.psi.PsiDocumentManager
import org.jetbrains.jet.plugin.codeInsight.ShortenReferences
import java.util.Collections
import com.intellij.psi.SmartPsiElementPointer
import org.jetbrains.jet.lang.psi.JetSimpleNameExpression
import org.jetbrains.jet.lang.resolve.name.FqName
import org.jetbrains.jet.lang.psi.JetCallExpression
import org.jetbrains.jet.lang.psi.psiUtil.changeQualifiedName
import org.jetbrains.jet.lang.psi.JetPsiFactory
import org.jetbrains.jet.lang.psi.ValueArgument
import java.util.ArrayList
import org.jetbrains.jet.lang.psi.JetExpression
import org.jetbrains.jet.lang.psi.psiUtil.getQualifedElementSelector
import org.jetbrains.jet.lang.psi.JetQualifiedExpression
import org.jetbrains.jet.lang.psi.psiUtil.getOutermostQualifiedElement
import org.jetbrains.jet.plugin.references.getUnstableQualifier

public class KotlinShortenReferencesRefactoringHelper: RefactoringHelper<Set<ReferenceBindRequest>> {
    override fun prepareOperation(usages: Array<out UsageInfo>?): Set<ReferenceBindRequest> {
        if (usages == null || usages.isEmpty()) return Collections.emptySet()
        return usages[0].getProject().getElementsToShorten(true)!!
    }

    override fun performOperation(project: Project, operationData: Set<ReferenceBindRequest>?) {
        project.shortenStoredReferences()
    }
}

private val ELEMENTS_TO_SHORTEN_KEY = Key.create<MutableSet<ReferenceBindRequest>>("ELEMENTS_TO_SHORTEN_KEY")

data class ReferenceBindRequest(val refExpression: SmartPsiElementPointer<JetSimpleNameExpression>, val fqName: FqName) {
    private val unstableQualifier = refExpression.getElement()?.getUnstableQualifier()

    fun process(): JetSimpleNameExpression? {
        fun moveReceiverToArgumentList(receiver: JetExpression, selector: JetCallExpression): JetCallExpression? {
            val selectorArguments = selector.getValueArgumentList()
            if (selectorArguments == null) return null

            val project = receiver.getProject()

            val args = ArrayList<String>()
            args.add(receiver.getText()!!)
            selector.getValueArguments().mapTo(args) { arg -> arg.asElement().getText()!! }

            val newCallArguments = JetPsiFactory.createCallArguments(project, args.makeString(", ", "(", ")", -1, "..."))
            val newSelector = (selectorArguments.replace(newCallArguments).getParent() as JetCallExpression)

            return newSelector.getParent()!!.replace(newSelector) as JetCallExpression
        }

        fun bindToFqName(expression: JetSimpleNameExpression, fqName: FqName): JetSimpleNameExpression {
            val qualifier = expression.changeQualifiedName(fqName)
            val newExpression = qualifier.getQualifedElementSelector(true) as JetSimpleNameExpression?
            assert(newExpression != null) { "No selector in qualified element" }

            return newExpression!!
        }

        val originalExpression = refExpression.getElement()
        if (originalExpression == null) return null

        if (unstableQualifier != null) {
            // Inner class was moved to top level: transform receiver expression to call argument
            // e.g. a.A(b, c) -> A(a, b, c)

            val selector = (unstableQualifier.getParent() as JetQualifiedExpression).getSelectorExpression()
            if (selector is JetCallExpression) {
                val adjustedCall = moveReceiverToArgumentList(unstableQualifier, (selector as JetCallExpression))
                if (adjustedCall != null) {
                    return bindToFqName(adjustedCall.getCalleeExpression() as JetSimpleNameExpression, fqName)
                }
            }
        }
        else {
            return bindToFqName(originalExpression, fqName)
        }

        return null
    }
}

private fun Project.getElementsToShorten(createIfNeeded: Boolean): MutableSet<ReferenceBindRequest>? {
    var elementsToShorten = getUserData(ELEMENTS_TO_SHORTEN_KEY)
    if (createIfNeeded && elementsToShorten == null) {
        elementsToShorten = HashSet()
        putUserData(ELEMENTS_TO_SHORTEN_KEY, elementsToShorten)
    }

    return elementsToShorten
}

public fun Project.addReferenceBindRequest(request: ReferenceBindRequest) {
    getElementsToShorten(true)!!.add(request)
}

public fun Project.shortenStoredReferences() {
    getElementsToShorten(false)?.let { bindRequests ->
        putUserData(ELEMENTS_TO_SHORTEN_KEY, null)
        ApplicationManager.getApplication()!!.runWriteAction {
            ShortenReferences.process(bindRequests.map() { req -> req.process()?.getOutermostQualifiedElement() }.filterNotNull())
        }
    }
}