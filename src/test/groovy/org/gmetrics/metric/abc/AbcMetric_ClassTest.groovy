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

import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.MethodKey

/**
 * Tests for AbcMetric calculation of class-level metrics
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbcMetric_ClassTest extends AbstractAbcMetricTest {
    static metricClass = AbcMetric

    void testMetricLevelIsMethod() {
        assert metric.baseLevel == MetricLevel.METHOD
    }

    void testApplyToClass_ReturnNullForClassWithNoMethods() {
        final SOURCE = """
            class MyClass {
                int myValue
            }
        """
        assert applyToClass(SOURCE) == null
    }

    void testApplyToClass_ReturnNullForInterface() {
        final SOURCE = """
            interface MyInterface {
                int doSomething(String name)
            }
        """
        assert applyToClass(SOURCE) == null
    }

    void testApplyToClass_IgnoresSyntheticMethods() {
        final SOURCE = """
            println 123     // this is a script; will generate main() and run() methods
        """
        assertApplyToClass(SOURCE, [0, 1, 0], [0,1,0], ['java.lang.Object run()':[0, 1, 0]])
    }

    void testApplyToClass_ResultsForClassWithOneMethod() {
        final SOURCE = """
            String a() {
                def x = 1               // A=1
            }
        """
        assertApplyToClass(SOURCE, [1, 0, 0], [1,0,0], ['String a()':[1, 0, 0]])
    }

    void testApplyToClass_ResultsForClassWithSeveralMethods() {
        final SOURCE = """
            class MyClass {
                def MyClass() {
                    def x = 1; y = x            // A=2
                }
                String b() {
                    new SomeClass(99)           // B=1
                    new SomeClass().run()       // B=2
                    x++                         // A=1
                }
                String c() {
                    switch(x) {
                        case 1: break           // C=1
                        case 3: break           // C=1
                    }
                    return x && x > 0 && x < 100 && !ready      // C=4
                }
            }
        """
        assertApplyToClass(SOURCE, [3,3,6], [1,1,2], [(DEFAULT_CONSTRUCTOR):[2,0,0], 'String b()':[1,3,0], 'String c()':[0,0,6]])
    }

    void testApplyToClass_ResultsForScript_RunMethod() {
        final SOURCE = """
            def myClosure = {           // this is actually inside the implicit run() method
                def x = 1
            }
        """
        assertApplyToClass(SOURCE, [2, 0, 0], [2,0,0], [(RUN_METHOD):[2, 0, 0]])
    }

    void testApplyToClass_ResultsForClassWithOneClosureField() {
        final SOURCE = """
            class MyClass {
                def myClosure = {
                    def x = 1; x++                         // A=2
                    doSomething()                          // B=1
                    if (x == 23) return 99 else return 0   // C=2
                }
            }
        """
        assertApplyToClass(SOURCE, [2,1,2], [2,1,2], [myClosure:[2,1,2]])
    }

    void testApplyToClass_ProcessesMultipleClosureFields() {
        final SOURCE = """
            class MyClass {
                def a = {
                    def x = 1; x++
                }
                def b = { println 'ok' }
                def c = {
                    println 'ok'
                }
            }
        """
        def results = applyToClass(SOURCE)
        def methodResults = results.methodMetricResults
        log("methodResults=$methodResults")
        assertEqualSets(methodResults.keySet().methodName, ['a', 'b', 'c'])
    }

    protected void assertApplyToClass(String source, classTotalValue, classAverageValue, Map methodValues) {
        def classNode = parseClass(source)
        def results = metric.applyToClass(classNode, sourceCode)
        log("results=$results")
        def classMetricResult = results.classMetricResult
        assert classMetricResult.metricLevel == MetricLevel.CLASS
        AbcTestUtil.assertEquals(classMetricResult.averageAbcVector, classAverageValue)
        AbcTestUtil.assertEquals(classMetricResult.totalAbcVector, classTotalValue)

        def methodMetricResults = results.methodMetricResults
        assertBothAreFalseOrElseNeitherIs(methodValues, methodMetricResults) 

        def methodNames = methodValues?.keySet()
        methodNames.each { methodName ->
            def methodKey = new MethodKey(methodName)
            def methodResult = methodMetricResults[methodKey]
            assert methodResult, "Method named [$methodName] does not exist"
            def methodValue = methodResult.abcVector
            AbcTestUtil.assertEquals(methodValue, methodValues[methodName])
            assert methodResult.metricLevel == MetricLevel.METHOD
        }
    }

 }