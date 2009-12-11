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
package org.gmetrics.metric.abc

import org.gmetrics.util.AstUtil
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.gmetrics.metric.AbstractAstVisitor

/**
 * AST Visitor for calculating the ABC Metric for a class/method.
 *
 * @see AbcMetric
 *
 * See http://www.softwarerenovation.com/ABCMetric.pdf
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbcAstVisitor extends AbstractAstVisitor {

    private static final ASSIGNMENT_OPERATIONS =
        ['=', '++', '--', '+=', '-=', '/=', '*=', '%=', '<<=', '>>=', '>>>=', '&=', '|=', '^=']
    private static final COMPARISON_OPERATIONS = ['<', '>', '>=', '<=', '==', '!=', '<=>', '=~', '==~']
    private static final BOOLEAN_LOGIC_OPERATIONS = ['&&', '||']

    int numberOfAssignments = 0
    int numberOfBranches = 0
    int numberOfConditions = 0

    void visitBinaryExpression(BinaryExpression expression) {
        handleExpressionContainingOperation(expression)
        super.visitBinaryExpression(expression)
    }

    void visitPrefixExpression(PrefixExpression expression) {
        handleExpressionContainingOperation(expression)
        super.visitPrefixExpression(expression)
    }

    void visitPostfixExpression(PostfixExpression expression) {
        handleExpressionContainingOperation(expression)
        super.visitPostfixExpression(expression)
    }

    void visitMethodCallExpression(MethodCallExpression call)  {
        numberOfBranches ++
        super.visitMethodCallExpression(call)
    }

    void visitPropertyExpression(PropertyExpression expression) {
        // Treat a property access as a method call
        numberOfBranches ++
        super.visitPropertyExpression(expression)
    }

    void visitConstructorCallExpression(ConstructorCallExpression call) {
        numberOfBranches ++
        super.visitConstructorCallExpression(call)
    }

    void visitIfElse(IfStatement ifElse) {
        if (isNotEmptyStatement(ifElse.elseBlock)) {
            numberOfConditions ++
        }
        super.visitIfElse(ifElse)
    }

    void visitSwitch(SwitchStatement statement) {
        numberOfConditions += statement.caseStatements.size()
        if (isNotEmptyStatement(statement.defaultStatement)) {
            numberOfConditions ++
        }
        super.visitSwitch(statement)
    }

    void visitTryCatchFinally(TryCatchStatement statement) {
        numberOfConditions ++                                   // for the 'try'
        numberOfConditions += statement.catchStatements.size()  // for each 'catch'
        super.visitTryCatchFinally(statement)
    }

    void visitTernaryExpression(TernaryExpression expression) {
        numberOfConditions ++
        super.visitTernaryExpression(expression)
    }

    void visitBooleanExpression(BooleanExpression booleanExpression) {
        if (isSingleVariable(booleanExpression.expression)) {
            numberOfConditions++
        }
        super.visitBooleanExpression(booleanExpression)
    }

    void visitNotExpression(NotExpression notExpression) {
        if (isSingleVariable(notExpression.expression)) {
            numberOfConditions++
        }
        super.visitNotExpression(notExpression)
    }

    //--------------------------------------------------------------------------
    // Internal helper methods
    //--------------------------------------------------------------------------

    private void handleExpressionContainingOperation(Expression expression) {
        def operationName = expression.operation.text
        if (operationName in ASSIGNMENT_OPERATIONS && !isFinalVariableDeclaration(expression)) {
            numberOfAssignments ++
        }
        if (operationName in COMPARISON_OPERATIONS) {
            numberOfConditions ++
        }
        if (operationName in BOOLEAN_LOGIC_OPERATIONS) {
            numberOfConditions += countUnaryConditionals(expression)
        }
    }

    // Use Groovy dynamic dispatch to achieve pseudo-polymorphism.
    // Call appropriate countUnaryConditionals() logic based on type of expression

    private int countUnaryConditionals(BinaryExpression binaryExpression) {
        def count = 0
        def operationName = binaryExpression.operation.text
        if (operationName in BOOLEAN_LOGIC_OPERATIONS) {
            if (isSingleVariable(binaryExpression.leftExpression)) {
                count ++
            }
            if (isSingleVariable(binaryExpression.rightExpression)) {
                count ++
            }
        }
        return count
    }

    private int countUnaryConditionals(Expression expression) {     // Not necessary?
        return 0
    }

    private boolean isSingleVariable(expression) {
        return expression instanceof VariableExpression
    }

    private boolean isFinalVariableDeclaration(expression) {
        return expression instanceof DeclarationExpression &&
            AstUtil.isFinalVariable(expression, sourceCode)
    }

    private boolean respondsTo(Object object, String methodName) {
        return object.metaClass.respondsTo(object, methodName)
    }

    private boolean isNotEmptyStatement(Statement statement) {
        statement.class != EmptyStatement
    }

}