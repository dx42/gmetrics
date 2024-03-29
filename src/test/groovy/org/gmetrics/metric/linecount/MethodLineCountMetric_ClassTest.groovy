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
import org.gmetrics.metric.MetricLevel
import org.junit.jupiter.api.Test

/**
 * Tests for MethodLineCountMetric - calculate aggregate metrics for a class
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class MethodLineCountMetric_ClassTest extends AbstractMetricTestCase {

    static Class metricClass = MethodLineCountMetric

    @Test
	void testBaseLevelIsMethod() {
        assert metric.baseLevel == MetricLevel.METHOD
    }

    @Test
	void testApplyToClass_ReturnNullForClassWithNoMethods() {
        final SOURCE = """
            int myValue
        """
        assert applyToClass(SOURCE) == null
    }

    @Test
	void testApplyToClass_ReturnNullForInterface() {
        final SOURCE = """
            interface MyInterface {
                int doSomething(String name)
            }
        """
        assert applyToClass(SOURCE) == null
    }

    @Test
	void testApplyToClass_IgnoresSyntheticMethods() {
        final SOURCE = """
            println 123     // this is a script; will generate main() and run() methods
        """
        assert applyToClass(SOURCE) == null
    }

    @Test
	void testApplyToClass_ResultsForClassWithOneMethod() {
        final SOURCE = """
            def a() {
                def x = 1
            }
        """
        assertApplyToClass(SOURCE, 3, 3, ['java.lang.Object a()':3])
    }

    @Test
	void testApplyToClass_ResultsForClassWithOverriddenMethods() {
        final SOURCE = """
            String a() { null }
            String a(int count) {
                return count as String
            }
        """
        assertApplyToClass(SOURCE, 4, 2, ['String a()':1, 'String a(int)':3])
    }

    @Test
	void testApplyToClass_ResultsForClassWithSeveralMethodsAndClosureFields() {
        final SOURCE = """
            class MyClass {
                MyClass() {                   // 3
                    def x = 1; y = x
                }

                def b = {                   // 5
                    new SomeClass(99)
                    new SomeClass().run()
                    x++
                }
                def c() {                   // 8
                    switch(x) {
                        case 1: break
                        case 3: break
                        case 5: break
                    }
                    return x
                }
                def d = { }                 // 1
                def e = {                   // 2
                    println 'ok' }
            }
        """
        assertApplyToClass(SOURCE, 19, scale(19/5), [(DEFAULT_CONSTRUCTOR):3, b:5, 'java.lang.Object c()':8, d:1, e:2])
    }

    @Test
	void testApplyToClass_ResultsForClassWithOneClosureField() {
        final SOURCE = """
            class MyClass {
                int count       // non-Closure field
                def myClosure = {
                    def x = 1; x++
                    doSomething()
                    if (x == 23) return 99 else return 0
                }
            }
        """
        assertApplyToClass(SOURCE, 5, 5, [myClosure:5])
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