/*
 * Copyright 2011 the original author or authors.
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
package org.gmetrics.util

import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.gmetrics.source.SourceCode
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.ArrayExpression
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.ClosureExpression

/**
 * Contains static utility methods related to Groovy AST.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AstUtil {

    /**
     * Return true only if the specified FieldNode has an initial expression that is a Closure
     * @param fieldNode - the FieldNode
     * @return true if the field is a Closure field; otherwise return false
     */
    static boolean isClosureField(FieldNode fieldNode) {
        !isFromGeneratedSourceCode(fieldNode) && fieldNode.initialExpression instanceof ClosureExpression
    }

    /**
     * Return true if the Statement is a block
     * @param statement - the Statement to check
     * @return true if the Statement is a block
     */
    static boolean isBlock(Statement statement) {
        return statement instanceof BlockStatement
    }

    /**
     * Return true if the Statement is a block and it is empty (contains no "meaningful" statements).
     * This implementation also addresses some "weirdness" around some statement types (specifically finally)
     * where the BlockStatement answered false to isEmpty() even if it was.
     * @param statement - the Statement to check
     * @return true if the BlockStatement is empty
     */
    static boolean isEmptyBlock(Statement statement) {
        return statement instanceof BlockStatement &&
            (statement.empty ||
            (statement.statements.size() == 1 && statement.statements[0].empty))
    }

   /**
    * Return the List of Arguments for the specified MethodCallExpression. The returned List contains
    * either ConstantExpression or MapEntryExpression objects.
    * @param methodCall - the AST MethodCallExpression
    * @return the List of argument objects
    */
    static List getMethodArguments(MethodCallExpression methodCall) {
        def argumentsExpression = methodCall.arguments
        if (respondsTo(argumentsExpression, 'getExpressions')) {
            return argumentsExpression.expressions
        }
        if (respondsTo(argumentsExpression, 'getMapEntryExpressions')) {
            return argumentsExpression.mapEntryExpressions
        }
        return []
    }

    /**
     * Return true only if the Statement represents a method call for the specified method object (receiver),
     * method name, and with the specified number of arguments.
     * @param stmt - the AST Statement
     * @param methodObject - the name of the method object (receiver)
     * @param methodName - the name of the method being called
     * @param numArguments - the number of arguments passed into the method
     * @return true only if the Statement is a method call matching the specified criteria
     */
    static boolean isMethodCall(Statement stmt, String methodObject, String methodName, int numArguments) {
        def match = false
        if (stmt instanceof ExpressionStatement) {
            def expression = stmt.expression
            if (expression instanceof MethodCallExpression) {
                match = isMethodCall(expression, methodObject, methodName, numArguments)
            }
        }
        return match
    }

    /**
     * Return true only if the MethodCallExpression represents a method call for the specified method object (receiver),
     * method name, and with the specified number of arguments.
     * @param methodCall - the AST MethodCallExpression
     * @param methodObject - the name of the method object (receiver)
     * @param methodName - the name of the method being called
     * @param numArguments - the number of arguments passed into the method
     * @return true only if the method call matches the specified criteria
     */
    static boolean isMethodCall(MethodCallExpression methodCall, String methodObject, String methodName, int numArguments) {
        def match = isMethodCall(methodCall, methodObject, methodName)
        return match && getMethodArguments(methodCall).size() == numArguments
    }

    /**
     * Return true only if the MethodCallExpression represents a method call for the specified method
     * object (receiver) and method name.
     * @param methodCall - the AST MethodCallExpression
     * @param methodObject - the name of the method object (receiver)
     * @param methodName - the name of the method being called
     * @return true only if the method call matches the specified criteria
     */
    static boolean isMethodCall(MethodCallExpression methodCall, String methodObject, String methodName) {
        def match = false
        def objectExpression = methodCall.objectExpression
        if (objectExpression instanceof VariableExpression) {
            def objectName = objectExpression.name
            match = (objectName == methodObject)
        }
        return match && isMethodNamed(methodCall, methodName)
    }

    /**
     * Return true only if the MethodCallExpression represents a method call for the specified method name
     * @param methodCall - the AST MethodCallExpression
     * @param methodName - the expected name of the method being called
     * @return true only if the method call name matches
     */
    static boolean isMethodNamed(MethodCallExpression methodCall, String methodName) {
        def method = methodCall.method
        return method.properties['value'] == methodName
    }

    /**
     * Return the AnnotationNode for the named annotation, or else null.
     * Supports Groovy 1.5 and Groovy 1.6.
     * @param node - the AnnotatedNode
     * @param name - the name of the annotation
     * @return the AnnotationNode or else null 
     */
    static AnnotationNode getAnnotation(AnnotatedNode node, String name) {
        def annotations = node.annotations
        return annotations instanceof Map ?
            annotations[name] :                                         // Groovy 1.5
            annotations.find { annot -> annot.classNode.name == name }  // Groovy 1.6
    }

    /**
     * Return the List of VariableExpression objects referenced by the specified DeclarationExpression.
     * @param declarationExpression - the DeclarationExpression
     * @return the List of VariableExpression objects
     */
    static List getVariableExpressions(DeclarationExpression declarationExpression) {
        def leftExpression = declarationExpression.leftExpression

        // !important: performance enhancement
        if (leftExpression instanceof ArrayExpression) {
            leftExpression.expressions ?: [leftExpression]
        } else if (leftExpression instanceof ListExpression) {
            leftExpression.expressions ?: [leftExpression]
        } else if (leftExpression instanceof TupleExpression) {
            leftExpression.expressions ?: [leftExpression]
        } else if (leftExpression instanceof VariableExpression) {
            [leftExpression]
        } else {
            leftExpression.properties['expressions'] ?: [leftExpression]
        }
    }

    /**
     * Return true if the DeclarationExpression represents a 'final' variable declaration.
     *
     * NOTE: THIS IS A WORKAROUND.
     * 
     * There does not seem to be an easy way to determine whether the 'final' modifier has been
     * specified for a variable declaration. Return true if the 'final' is present before the variable name.
     */
    static boolean isFinalVariable(DeclarationExpression declarationExpression, SourceCode sourceCode) {
        if (isFromGeneratedSourceCode(declarationExpression)) {
            return false
        }
        def variableExpressions = AstUtil.getVariableExpressions(declarationExpression)
        def variableExpression = variableExpressions[0]
        def startOfDeclaration = declarationExpression.columnNumber
        def startOfVariableName = variableExpression.columnNumber
        def sourceLine = sourceCode.lines[declarationExpression.lineNumber-1]

        def modifiers = (startOfDeclaration >= 0 && startOfVariableName >= 0) ?
            sourceLine[startOfDeclaration-1..startOfVariableName-2] : ''
        return modifiers.contains('final')
    }

    /**
     * @return true if the ASTNode was generated (synthetic) rather than from the "real" input source code.
     */
    static boolean isFromGeneratedSourceCode(ASTNode node) {
        return node.lineNumber < 0
    }

    /**
     * Return true only if the specified object responds to the named method
     * @param object - the object to check
     * @param methodName - the name of the method
     * @return true if the object responds to the named method
     */
    private static boolean respondsTo(Object object, String methodName) {
        return object.metaClass.respondsTo(object, methodName)
    }

    /**
     * Private constructor. All methods are static.
     */
    private AstUtil() { }
}