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

import org.gmetrics.metric.AbstractMetricTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for MethodLinesOfCodeMetric - calculate lines of code for methods
 *
 * @author Chris Mair
 */
class MethodLineCountMetric_MethodTest extends AbstractMetricTestCase {

    static Class metricClass = MethodLineCountMetric

    @Test
	void testApplyToMethod() {
        final SOURCE = """
            def myMethod() { }
        """
        assert applyToMethodValue(SOURCE) == 1
    }

    @Test
	void testCalculate_CorrectSizeForSingleLineMethod() {
        final SOURCE = """
            def myMethod() { }
        """
        assert calculateForMethod(SOURCE) == 1
    }

    @Test
	void testCalculate_CorrectSizeForMultiLineMethod() {
        final SOURCE = """
            def myMethod() {
                println "started"

                doSomethingElse()
                return count
            }
        """
        assert calculateForMethod(SOURCE) == 6
    }

    @Test
	void testCalculate_ReturnsNullForAbstractMethodDeclaration() {
        final SOURCE = """
            abstract class MyClass {
                abstract void doSomething()
            }
        """
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    @Test
	void testCalculate_ReturnsNullForSyntheticMethod() {
        final SOURCE = """
            println 123
        """
        def methodNode = findSyntheticMethod(SOURCE)
        assert metric.calculate(methodNode, sourceCode) == null
    }

    @Test
	void testCalculate_CountsForConstructor() {
        final SOURCE = """
            class MyClass {
                MyClass() {
                    def x = 1; x++
                    doSomething()
                    if (x == 23) return 99 else return 0
                }
            }
        """
        assert calculateForConstructor(SOURCE) == 5
    }

    @Test
    void testCalculate_CorrectLineNumberForAnnotatedMethod_NextLine() {
        final SOURCE = """
            @SomeAnnotation
            def myMethod() { }
        """
        assert metricLineNumber(findFirstMethod(SOURCE)) == 3
    }

    @Test
    void testCalculate_CorrectLineNumberForAnnotatedMethod_SameLine() {
        final SOURCE = """
            @SomeAnnotation def myMethod() { }
        """
        assert metricLineNumber(findFirstMethod(SOURCE)) == 2
    }

    @Test
    void testCalculate_CorrectLineNumberForAnnotatedMethod_CommentInBetween() {
        final SOURCE = """
            @SomeAnnotation
            // comment

            def myMethod() {
                println 123
            }
        """
        assert metricLineNumber(findFirstMethod(SOURCE)) == 5
    }

}