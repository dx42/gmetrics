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
 package org.gmetrics.metric.coverage

import org.gmetrics.test.AbstractTestCase
import org.codehaus.groovy.ast.builder.AstBuilder

/**
 * Tests for CoberturaSignatureParser
 *
 * @author Chris Mair
 */
class CoberturaSignatureParserTest extends AbstractTestCase {

    void testMatchesCoberturaMethod_MethodNode_Match() {
        final SOURCE = '''
            class MyClass {
                void m(String name) { }
            }
            '''
        def nodes = new AstBuilder().buildFromString(SOURCE)
        def methodNode = nodes[1].getDeclaredMethods('m')[0]

        assert CoberturaSignatureParser.matchesCoberturaMethod(methodNode, 'm', '(Ljava/lang/String;)V')
        assert !CoberturaSignatureParser.matchesCoberturaMethod(methodNode, 'other', '(Ljava/lang/String;)V')
        assert !CoberturaSignatureParser.matchesCoberturaMethod(methodNode, 'm', '(Ljava/lang/Object;)V')
        assert !CoberturaSignatureParser.matchesCoberturaMethod(methodNode, 'm', '()V')
    }

    void testMatchesCoberturaMethod_Match() {
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(String)', 'm', '(Ljava/lang/String;)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(String, String)', 'm', '(Ljava/lang/String;Ljava/lang/String;)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'String m()', 'm', '()Ljava/lang/String;')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'Channel m(String)', 'm', '(Ljava/lang/String;)Lcom/example/model/Channel')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'def m(java.lang.Object)', 'm', '(Ljava/lang/Object;)Ljava/lang/Object;')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m()', 'm', '()V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'Map m(Map)', 'm', '(Ljava/util/Map;)Ljava/util/Map;')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'Map m()', 'm', '()Ljava/util/Map;')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'java.lang.Object m()', 'm', '()Ljava/lang/Object;')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'Map m(String, Closure)', 'm', '(Ljava/lang/String;Lgroovy/lang/Closure;)Ljava/util/Map;')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(boolean)', 'm', '(Z)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(int)', 'm', '(I)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(float)', 'm', '(F)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(double)', 'm', '(D)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(char)', 'm', '(C)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(long)', 'm', '(J)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(byte)', 'm', '(B)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(short)', 'm', '(S)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(int, String, long, boolean)', 'm', '(ILjava/lang/String;JZ)V')
    }

    void testMatchesCoberturaMethod_Arrays() {
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(int[])', 'm', '([I)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m([I)', 'm', '([I)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(String[])', 'm', '([Ljava/lang/String;)V')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'int m(int[], String[], long[], boolean[], int[])', 'm', '([I[Ljava/lang/String;[J[Z[I)I')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'int m([I, String[], [J, [Z, [I)', 'm', '([I[Ljava/lang/String;[J[Z[I)I')
        assert CoberturaSignatureParser.matchesCoberturaMethod('m', 'int m(int[], String, int)', 'm', '([ILjava/lang/String;I)')
    }

    void testMatchesCoberturaMethod_NoMatch() {
        assert !CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m()', 'm', '(Ljava/lang/String;)V')
        assert !CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m()', 'other', '()V')
        assert !CoberturaSignatureParser.matchesCoberturaMethod('m', 'void m(String, String))', 'm', '(Ljava/lang/String;)V')
        assert !CoberturaSignatureParser.matchesCoberturaMethod('m', 'String m(int)', 'm', '()Ljava/lang/String;')
        assert !CoberturaSignatureParser.matchesCoberturaMethod('m', 'Channel m(String)', 'm', '(Ljava/lang/Integer;)Lcom/example/model/Channel')
        assert !CoberturaSignatureParser.matchesCoberturaMethod('m', 'def m(String)', 'm', '(Ljava/lang/Object;)Ljava/lang/Object;')
    }

    void testParseSignatureParameterTypes() {
        assert CoberturaSignatureParser.parseSignatureParameterTypes('void m()') == []
        assert CoberturaSignatureParser.parseSignatureParameterTypes('void m(String)') == ['String']
        assert CoberturaSignatureParser.parseSignatureParameterTypes('void m(String, String)') == ['String', 'String']
        assert CoberturaSignatureParser.parseSignatureParameterTypes('def m(java.lang.Object)') == ['Object']
    }

    void testParseSignatureParameterTypes_Arrays() {
        assert CoberturaSignatureParser.parseSignatureParameterTypes('void m([I)') == ['int[]']
        assert CoberturaSignatureParser.parseSignatureParameterTypes('void m([J)') == ['long[]']
        assert CoberturaSignatureParser.parseSignatureParameterTypes('void m(String[])') == ['String[]']
        assert CoberturaSignatureParser.parseSignatureParameterTypes('def m([I, String[], [J, [Z)') == ['int[]', 'String[]', 'long[]', 'boolean[]']
    }

    void testParseCoberturaSignatureParameterTypes() {
        assert CoberturaSignatureParser.parseCoberturaSignatureParameterTypes('()Ljava/lang/String;') == []
        assert CoberturaSignatureParser.parseCoberturaSignatureParameterTypes('(IJZ)V') == ['int', 'long', 'boolean']
        assert CoberturaSignatureParser.parseCoberturaSignatureParameterTypes('(BS)V') == ['byte', 'short']
        assert CoberturaSignatureParser.parseCoberturaSignatureParameterTypes('(Ljava/lang/String;)V') == ['String']
        assert CoberturaSignatureParser.parseCoberturaSignatureParameterTypes('(Ljava/lang/Object;)Ljava/lang/Object;') == ['Object']
        assert CoberturaSignatureParser.parseCoberturaSignatureParameterTypes('(Ljava/lang/String;Lgroovy/lang/Closure;)Ljava/util/Map;') == ['String', 'Closure']
        assert CoberturaSignatureParser.parseCoberturaSignatureParameterTypes('(ILjava/lang/String;JZ)V') == ['int', 'String', 'long', 'boolean']
    }

    void testParseCoberturaSignatureParameterTypes_Arrays() {
        assert CoberturaSignatureParser.parseCoberturaSignatureParameterTypes('([IJ[Z)V') == ['int[]', 'long', 'boolean[]']
        assert CoberturaSignatureParser.parseCoberturaSignatureParameterTypes('([Ljava/lang/String;)V') == ['String[]']
    }
}
