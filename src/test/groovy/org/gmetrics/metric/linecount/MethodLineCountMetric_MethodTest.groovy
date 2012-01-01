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

/**
 * Tests for MethodLinesOfCodeMetric - calculate lines of code for methods
 *
 * @author Chris Mair
 */
class MethodLineCountMetric_MethodTest extends AbstractMetricTestCase {
    static metricClass = MethodLineCountMetric

    void testCalculate_CorrectSizeForSingleLineMethod() {
        final SOURCE = """
            def myMethod() { }
        """
        assert calculateForMethod(SOURCE) == 1
    }

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

    void testCalculate_ReturnsNullForAbstractMethodDeclaration() {
        final SOURCE = """
            abstract class MyClass {
                abstract void doSomething()
            }
        """
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    void testCalculate_ReturnsNullForSyntheticMethod() {
        final SOURCE = """
            println 123
        """
        def methodNode = findSyntheticMethod(SOURCE)
        assert metric.calculate(methodNode, sourceCode) == null
    }

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

}