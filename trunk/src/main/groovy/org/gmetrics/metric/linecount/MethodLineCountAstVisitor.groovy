/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gmetrics.metric.linecount

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.gmetrics.metric.AbstractAstVisitor

/**
 * AST Visitor for calculating the lines of code for a method or closure field.
 *
 * @author Chris Mair
 * @version $Revision: 224 $ - $Date: 2009-09-22 22:04:03 -0400 (Tue, 22 Sep 2009) $
 */
class MethodLineCountAstVisitor extends AbstractAstVisitor {
    int numberOfLinesInMethod
    int numberOfLinesInClosure

    void visitMethod(MethodNode methodNode) {
        if (methodNode.lineNumber >= 0) {
            numberOfLinesInMethod = methodNode.lastLineNumber - methodNode.lineNumber + 1
        }
        super.visitMethod(methodNode)
    }

    void visitClosureExpression(ClosureExpression expression) {
        if (expression.lineNumber >= 0) {
            numberOfLinesInClosure = expression.lastLineNumber - expression.lineNumber + 1
        }
        super.visitClosureExpression(expression)
    }

}