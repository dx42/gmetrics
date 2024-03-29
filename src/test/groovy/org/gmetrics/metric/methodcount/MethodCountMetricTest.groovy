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
package org.gmetrics.metric.methodcount

import org.gmetrics.metric.AbstractMetricTestCase
import org.gmetrics.metric.MetricLevel
import org.junit.jupiter.api.Test

/**
 * Tests for MethodCountMetric
 *
 * @author Chris Mair
 */
class MethodCountMetricTest extends AbstractMetricTestCase {

    static Class metricClass = MethodCountMetric

    @Test
	void testBaseLevelIsClass() {
        assert metric.baseLevel == MetricLevel.CLASS
    }

    @Test
	void testHasProperName() {
        assert metric.name == 'MethodCount'
    }

    @Test
	void testApplyToClass_NoMethods() {
        final SOURCE = """
            class MyClass {
                int myValue
            }
        """
        assertApplyToClass(SOURCE, 0, 0)
    }

    @Test
	void testApplyToClass_OneMethod() {
        final SOURCE = """
            class MyClass {
                int myValue
                void printMe() { println this }
            }
        """
        assertApplyToClass(SOURCE, 1, 1)
    }

    @Test
	void testApplyToClass_TwoMethod() {
        final SOURCE = """
            class MyClass {
                int myValue

                int calculateStuff(int max) {
                    def other = max * 23
                    println "other=\$other"
                    return other
                }

                void printMe() { println this }
            }
        """
        assertApplyToClass(SOURCE, 2, 2)
    }

    @Test
	void testApplyToClass_Constructor() {
        final SOURCE = """
            class MyClass {
                int myValue
                MyClass() { myValue = 3 }
                void printMe() { println this }
            }
        """
        assertApplyToClass(SOURCE, 2, 2)
    }

    @Test
	void testApplyToClass_ClosureFields() {
        final SOURCE = """
            class MyClass {
                int myValue
                def closure1 = { println 123 }      // Closure field
                void printMe() { println this }     // Method
                def closure2 = { println 345 }      // Closure field
            }
        """
        assertApplyToClass(SOURCE, 3, 3)
    }

    @Test
	void testApplyToClass_IgnoreNonFieldClosures() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    def var1 = { println 123 }
                    def var2 = { println 345 }
                }
            }
        """
        assertApplyToClass(SOURCE, 1, 1)
    }

    @Test
	void testApplyToClass_Interface() {
        final SOURCE = """
            interface MyInterface {
                int doSomething(String name)

            }
        """
        assertApplyToClass(SOURCE, 1, 1)
    }

    @Test
	void testApplyToClass_ReturnsNullForSyntheticClass() {
        final SOURCE = """
            println 123
        """
        def classNode = parseClass(SOURCE)
        def results = metric.applyToClass(classNode, sourceCode)
        log("results=$results")
        assert results == null
    }

    @Test
	void testApplyToPackage_ResultsForNoChildren() {
        assertApplyToPackage([], 0, 0)
    }

    @Test
	void testApplyToPackage_ResultsForOneChild() {
        assertApplyToPackage([metricResult(23)], 23, 23)
    }

    @Test
	void testApplyToPackage_ResultsForThreeChildren() {
        assertApplyToPackage([metricResult(20), metricResult(6), metricResult(4)], 30, 10)
    }

}