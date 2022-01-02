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

import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.SourceUnit
import org.gmetrics.source.SourceString
import org.gmetrics.test.AbstractTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * Tests for AstUtil
 *
 * @author Chris Mair
 */
class AstUtilTest extends AbstractTestCase {

    private static final SOURCE = '''
        class MyClass {
            def otherMethod() {
                object.print()
                if (true) {
                }
                ant.delete(dir:appBase, failonerror:false)
                "stringMethodName"(123)
                gstringMethodName = 'anotherMethod'
                "$gstringMethodName"(234)
                int myVariable = 99
            }
            @BeforeEach setUp() {  }
        }
        enum MyEnum {
            READ, WRITE
            private MyEnum() {
                println methodCallWithinEnum(true, 'abc', 123)
            }
        }
    '''
    private visitor

    @Test
	void testIsFromGeneratedSourceCode() {
        assert !AstUtil.isFromGeneratedSourceCode(methodCallNamed('print'))
    }

    @Test
	void testIsEmptyMethod() {
        assert AstUtil.isEmptyMethod(methodNamed('setUp'))
        assert !AstUtil.isEmptyMethod(methodNamed('otherMethod'))
    }

    @Test
	void testGetMethodArguments_ConstructorWithinEnum() {
        def methodCall = methodCallNamed('methodCallWithinEnum')
        def args = AstUtil.getMethodArguments(methodCall)
        assert args.size() == 3
    }

    @Test
	void testGetMethodArguments_NoArgument() {
        def methodCall = methodCallNamed('print')
        def args = AstUtil.getMethodArguments(methodCall)
        assert args.size() == 0
    }

    @Test
	void testGetMethodArguments_SingleArgument() {
        def methodCall = methodCallNamed('stringMethodName')
        def args = AstUtil.getMethodArguments(methodCall)
        assert args.size() == 1
        assert args[0].value == 123
    }

    @Test
	void testGetMethodArguments_NamedArguments() {
        def methodCall = methodCallNamed('delete')
        def args = AstUtil.getMethodArguments(methodCall)
        assert args[0].mapEntryExpressions.keyExpression.value == ['dir', 'failonerror']
    }

    @Test
	void testIsMethodCall_ExactMatch() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert AstUtil.isMethodCall(statement, 'object', 'print', 0)
        assert AstUtil.isMethodCall(statement.expression, 'object', 'print', 0)
        assert AstUtil.isMethodCall(statement.expression, 'object', 'print')
    }

    @Test
	void testIsMethodCall_WrongMethodName() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isMethodCall(statement, 'object', 'print2', 0)
        assert !AstUtil.isMethodCall(statement.expression, 'object', 'print2', 0)
        assert !AstUtil.isMethodCall(statement.expression, 'object', 'print2')
    }

    @Test
	void testIsMethodCall_WrongMethodObjectName() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isMethodCall(statement, 'object2', 'print', 0)
        assert !AstUtil.isMethodCall(statement.expression, 'object2', 'print', 0)
        assert !AstUtil.isMethodCall(statement.expression, 'object2', 'print')
    }

    @Test
	void testIsMethodCall_WrongNumberOfArguments() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isMethodCall(statement, 'object', 'print', 1)
        assert !AstUtil.isMethodCall(statement.expression, 'object', 'print', 1)
        assert AstUtil.isMethodCall(statement.expression, 'object', 'print')
    }

    @Test
	void testIsMethodCall_NamedArgumentList() {
        def methodCall = visitor.methodCallExpressions.find { mc -> mc.method.value == 'delete' }
        assert AstUtil.isMethodCall(methodCall, 'ant', 'delete', 1)
        assert !AstUtil.isMethodCall(methodCall, 'ant', 'delete', 2)
        assert AstUtil.isMethodCall(methodCall, 'ant', 'delete')
    }

    @Test
	void testIsMethodCall_StringLiteralMethodName() {
        def methodCall = methodCallNamed('stringMethodName')
        assert AstUtil.isMethodCall(methodCall, 'this', 'stringMethodName', 1)
        assert !AstUtil.isMethodCall(methodCall, 'this', 'stringMethodName', 2)
        assert AstUtil.isMethodCall(methodCall, 'this', 'stringMethodName')
    }

    @Test
	void testIsMethodCall_GStringMethodName() {
        def methodCall = visitor.methodCallExpressions.find { mc -> log(mc.method); mc.method instanceof GStringExpression }
        assert !AstUtil.isMethodCall(methodCall, 'this', 'anotherMethod', 1)
        assert !AstUtil.isMethodCall(methodCall, 'this', 'anotherMethod', 2)
        assert !AstUtil.isMethodCall(methodCall, 'this', 'anotherMethod')
    }

    @Test
	void testIsMethodCall_NotAMethodCall() {
        def statement = visitor.statements.find { st -> st instanceof BlockStatement }
        assert !AstUtil.isMethodCall(statement, 'object', 'print', 0)
    }

    @Test
	void testIsMethodNamed() {
        def methodCall = methodCallNamed('print')
        assert AstUtil.isMethodNamed(methodCall, 'print')
        assert !AstUtil.isMethodNamed(methodCall, 'other')
    }

    @Test
	void testIsMethodNamed_GStringMethodName() {
        def methodCall = visitor.methodCallExpressions.find { mc -> mc.method instanceof GStringExpression }
        assert !AstUtil.isMethodNamed(methodCall, 'print')
    }

    @Test
	void testIsBlock_Block() {
        applyVisitor(SOURCE)
        def statement = visitor.statements.find { st -> st instanceof BlockStatement }
        assert AstUtil.isBlock(statement)
    }

    @Test
	void testIsBlock_NotABlock() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isBlock(statement)
    }

    @Test
	void testIsEmptyBlock_NonEmptyBlock() {
        def statement = visitor.statements.find { st -> st instanceof BlockStatement }
        assert !AstUtil.isEmptyBlock(statement)
    }

    @Test
	void testIsEmptyBlock_EmptyBlock() {
        def statement = visitor.statements.find { st -> st instanceof IfStatement }
        assert AstUtil.isEmptyBlock(statement.ifBlock)
    }

    @Test
	void testIsEmptyBlock_NotABlock() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isEmptyBlock(statement)
    }

    @Test
	void testGetAnnotation() {
        assert AstUtil.getAnnotation(visitor.methodNodes['otherMethod'], 'doesNotExist') == null
        assert AstUtil.getAnnotation(visitor.methodNodes['setUp'], 'doesNotExist') == null
        assert AstUtil.getAnnotation(visitor.methodNodes['setUp'], 'BeforeEach') instanceof AnnotationNode
    }

    @Test
	void testGetVariableExpressions_SingleDeclaration() {
        log("declarationExpressions=${visitor.declarationExpressions}")
        def variableExpressions = AstUtil.getVariableExpressions(visitor.declarationExpressions[0])
        log("variableExpressions=$variableExpressions")
        assert variableExpressions.size() == 1
        assert variableExpressions.name == ['myVariable']
    }

    @Test
	void testGetVariableExpressions_MultipleDeclarations() {
        final NEW_SOURCE = '''
            class MyClass {
                def otherMethod() {
                    def (name1, name2) = ['abc', 'def']
                }
            }
        '''
        applyVisitor(NEW_SOURCE)
        //visitor.declarationExpressions.eachWithIndex { dec, index -> println "$index $dec" }
        def variableExpressions = AstUtil.getVariableExpressions(visitor.declarationExpressions[3])
        assert variableExpressions.name == ['name1', 'name2']
    }

    @Test
	void testIsClosureField() {
        final NEW_SOURCE = '''
            class MyClass {
                int count
                def closure = { prinltn 123 }
            }
            '''
        applyVisitor(NEW_SOURCE)
        log visitor.fieldNodes.name
        def nonClosureField = visitor.fieldNodes.find { fieldNode -> fieldNode.name == 'count' }
        assert !AstUtil.isClosureField(nonClosureField)

        def closureField = visitor.fieldNodes.find { fieldNode -> fieldNode.name == 'closure' }
        assert AstUtil.isClosureField(closureField)
    }

    @BeforeEach
    void setUp() {
        visitor = new AstUtilTestVisitor()
        applyVisitor(SOURCE)
    }

    private void applyVisitor(String source) {
        def sourceCode = new SourceString(source)
        def ast = sourceCode.ast
        ast.classes.each {
            classNode -> visitor.visitClass(classNode)
        }
    }

    private MethodCallExpression methodCallNamed(String name) {
        def methodCall = visitor.methodCallExpressions.find { mc ->
            if (mc.method instanceof GStringExpression) {
                return mc.text.startsWith(name)
            }
            mc.method.value == name
        }
        return methodCall
    }

    private MethodNode methodNamed(String name) {
        visitor.methodNodes[name]
    }
}

class AstUtilTestVisitor extends ClassCodeVisitorSupport {
    static final LOG = LoggerFactory.getLogger(AstUtilTestVisitor)
    def methodNodes = [:]
    def methodCallExpressions = []
    def statements = []
    def declarationExpressions = []
    def fieldNodes = []

    @Override
    void visitClass(ClassNode node) {
        super.visitClass(node)
        fieldNodes = node.fields
    }

    @Override
    void visitMethod(MethodNode methodNode) {
        methodNodes[methodNode.name] = methodNode
        super.visitMethod(methodNode)
    }

    @Override
    void visitStatement(Statement statement) {
        this.statements << statement
        super.visitStatement(statement)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression methodCallExpression) {
        this.methodCallExpressions << methodCallExpression
        super.visitMethodCallExpression(methodCallExpression)
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression declarationExpression) {
        declarationExpressions << declarationExpression
        super.visitDeclarationExpression(declarationExpression)
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return source
    }
}
