/*
 * Copyright 2010 the original author or authors.
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

import org.gmetrics.metric.AbstractMetricTestCase
import org.gmetrics.metric.MetricLevel
import org.junit.jupiter.api.Test

/**
 * Tests for CyclomaticComplexityMetric - calculate aggregate metrics for a class
 *
 * @author Chris Mair
 */
class CyclomaticComplexityMetric_ClassTest extends AbstractMetricTestCase {

    static Class metricClass = CyclomaticComplexityMetric

    @Test
	void testBaseLevelIsMethod() {
        assert metric.baseLevel == MetricLevel.METHOD
    }

    @Test
	void testApplyToClass_ReturnNullForClassWithNoMethods() {
        final SOURCE = """
            class MyClass {
                int myValue
            }
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
	void testApplyToClass_IgnoresSyntheticNonRunMethods() {
        final SOURCE = """
            println 123     // this is a script; will generate main() and run() methods
        """
        assertApplyToClass(SOURCE, 1, 1, [(RUN_METHOD):1])
    }

    @Test
	void testApplyToClass_ResultsForClassWithOneMethod() {
        final SOURCE = """
            class MyClass {
                String a() {
                    if (ready) { }
                }
            }
        """
        assertApplyToClass(SOURCE, 2, 2, ['String a()':2])
    }

    @Test
	void testApplyToClass_ResultsForClassWithSeveralMethodsAndClosureFields() {
        final SOURCE = """
            class MyClass {
                MyClass() {                   // 3
                    done = x && y || y < 0
                }

                def b = {                   // 3
                    if (ready || started) { }
                    else { }
                }
                String c() {                   // 5
                    switch(x) {
                        case 1: break
                        case 3: break
                        case 5: break
                    }
                    return result?.sum
                }
                def d = { }                 // 1
                def e = {                   // 2
                    try {  }
                    catch(Exception e) {

                    }
                }

            }
        """
        assertApplyToClass(SOURCE, 14, scale(14/5), [(DEFAULT_CONSTRUCTOR):3, b:3, 'String c()':5, d:1, e:2])
    }

    @Test
	void testApplyToClass_ResultsForClassWithOneClosureField() {
        final SOURCE = """
            class MyClass {
                def myClosure = {
                    if (x == 23) return 99 else return 0
                }
            }
        """
        assertApplyToClass(SOURCE, 2, 2, [myClosure:2])
    }

    @Test
	void testApplyToClass_ResultsForScript_RunMethod() {
        final SOURCE = """
            def myClosure = {           // this is actually inside the implicit run() method
                if (x == 23) return 99 else return 0
            }
        """
        assertApplyToClass(SOURCE, 2, 2, [(RUN_METHOD):2])
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