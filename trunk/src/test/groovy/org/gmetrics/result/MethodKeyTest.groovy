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
 package org.gmetrics.result

import org.gmetrics.test.AbstractTestCase
import org.codehaus.groovy.ast.MethodNode
import org.gmetrics.source.SourceString
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.control.SourceUnit

/**
 * Tests for MethodKey
 *
 * @author Chris Mair
 */
@SuppressWarnings('ExplicitCallToEqualsMethod')
class MethodKeyTest extends AbstractTestCase {

    void testMethodName() {
        def methodKey = new MethodKey('abc')
        assert methodKey.methodName == 'abc'
    }

    void testMethodNode() {
        def methodNode = buildMethodNodes()[0]
        def methodKey = new MethodKey(methodNode)
        assert methodKey.methodName == 'myMethod'
    }

    @SuppressWarnings('ComparisonWithSelf')
    void testEqualsAndHashCode_SameInstance() {
        def methodKey = new MethodKey('abc')
        assert methodKey.equals(methodKey)
        assert methodKey.hashCode() == methodKey.hashCode()
    }

    void testEqualsAndHashCode_SameMethodNameValue() {
        def methodKey1 = new MethodKey('abc')
        def methodKey2 = new MethodKey('abc')
        assert methodKey1.equals(methodKey2)
        assert methodKey1.hashCode() == methodKey2.hashCode()
    }

    void testEqualsAndHashCode_SameMethodNodeValue() {
        def methodNode1 = buildMethodNodes()[0]
        def methodNode2 = buildMethodNodes()[0]
        assertEqualMethodKeys(methodNode1, methodNode2)
    }

    void testEqualsAndHashCode_DifferentMethodNameValue() {
        def methodKey1 = new MethodKey('abc')
        def methodKey2 = new MethodKey('xxx')
        assert !methodKey1.equals(methodKey2)
        assert methodKey1.hashCode() != methodKey2.hashCode()
    }

    void testEqualsAndHashCode_DifferentMethodNodeValues() {
        def methodNode1 = buildMethodNodes()[0]
        def methodNode2 = buildMethodNodes()[1]
        assertUnequalMethodKeys(methodNode1, methodNode2)
    }

    void testEqualsAndHashCode_OverriddenMethod() {
        def methodNode1 = buildMethodNodes()[0]
        def methodNode2 = buildMethodNodes()[2]
        assertUnequalMethodKeys(methodNode1, methodNode2)
    }

    void testEquals_NotAMethodKey() {
        def methodKey = new MethodKey('abc')
        assert !methodKey.equals('XXX')
    }

    void testEquals_Null() {
        def methodKey = new MethodKey('abc')
        assert !methodKey.equals(null)
    }

    void testConstructor_String_NullOrEmptyString() {
        shouldFailWithMessageContaining('methodName') { new MethodKey((String)null) }
        shouldFailWithMessageContaining('methodName') { new MethodKey('') }
    }

    void testConstructor_MethodNode_NullMethodNode() {
        shouldFailWithMessageContaining('methodNode') { new MethodKey((MethodNode)null) }
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    private void assertEqualMethodKeys(MethodNode methodNode1, MethodNode methodNode2) {
        def methodKey1 = new MethodKey(methodNode1)
        def methodKey2 = new MethodKey(methodNode2)
        assert methodKey1.equals(methodKey2)
        assert methodKey1.hashCode() == methodKey2.hashCode()
    }

    private void assertUnequalMethodKeys(MethodNode methodNode1, MethodNode methodNode2) {
        def methodKey1 = new MethodKey(methodNode1)
        def methodKey2 = new MethodKey(methodNode2)
        assert !methodKey1.equals(methodKey2)
        assert methodKey1.hashCode() != methodKey2.hashCode()
    }

    private List<MethodNode> buildMethodNodes() {
        final SOURCE = '''
            class MyClass {
                int myMethod(String name, long timestamp) { }
                def otherMethod() { }
                int myMethod(long timestamp) { }        // overridden
            }
        '''
        def sourceCode = new SourceString(SOURCE)
        def ast = sourceCode.ast
        def visitor = new MethodKeyTestVisitor()
        ast.classes.each {
            classNode -> visitor.visitClass(classNode)
        }
        return visitor.methodNodes
    }

    private static class MethodKeyTestVisitor extends ClassCodeVisitorSupport {
        def methodNodes = []

        @Override
        void visitMethod(MethodNode methodNode) {
            methodNodes << methodNode
        }

        @Override
        protected SourceUnit getSourceUnit() {
            return null
        }
    }

}
