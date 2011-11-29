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
package org.gmetrics.metric.cyclomatic

import org.codehaus.groovy.ast.MethodNode
import org.gmetrics.metric.AbstractAstVisitor
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.WhileStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.PropertyExpression

/**
 * AST Visitor for calculating the Cyclomatic Complexity for a method or closure field.
 *
 * @see CyclomaticComplexityMetric
 *
 * @author Chris Mair
 */
class CyclomaticComplexityAstVisitor extends AbstractAstVisitor {

    private static final BOOLEAN_LOGIC_OPERATIONS = ['&&', '||']
    Integer complexity = 1

    void visitMethod(MethodNode methodNode) {
        if (methodNode.isAbstract() || isSyntheticNonRunMethod(methodNode) ) {
            complexity = null
        }
        else {
            super.visitMethod(methodNode)
        }
    }

    void visitIfElse(IfStatement ifElse) {
        complexity++
        super.visitIfElse(ifElse)
    }

    void visitWhileLoop(WhileStatement loop) {
        complexity++
        super.visitWhileLoop(loop)
    }

    void visitForLoop(ForStatement forLoop) {
        complexity++
        super.visitForLoop(forLoop)
    }

    void visitSwitch(SwitchStatement statement) {
        complexity += statement.caseStatements.size()
        super.visitSwitch(statement)
    }

    void visitCatchStatement(CatchStatement statement) {
        complexity++
        super.visitCatchStatement(statement)
    }

    void visitBinaryExpression(BinaryExpression expression) {
        handleExpressionContainingOperation(expression)
        super.visitBinaryExpression(expression)
    }

    void visitTernaryExpression(TernaryExpression expression) {
        complexity++
        super.visitTernaryExpression(expression)
    }

    void visitPropertyExpression(PropertyExpression expression) {
        complexity += expression.safe ? 1 : 0
        super.visitPropertyExpression(expression)
    }

    private void handleExpressionContainingOperation(Expression expression) {
        def operationName = expression.operation.text
        if (operationName in BOOLEAN_LOGIC_OPERATIONS) {
            complexity++
        }
    }
}