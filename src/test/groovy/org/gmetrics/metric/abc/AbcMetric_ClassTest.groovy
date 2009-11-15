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

/**
 * Tests for AbcMetric calculation of class-level metrics
 *
 * @author Chris Mair
 * @version $Revision: 239 $ - $Date: 2009-11-11 20:17:36 -0500 (Wed, 11 Nov 2009) $
 */
class AbcMetric_ClassTest extends AbstractAbcMetricTest {
    static metricClass = AbcMetric

    void testMetricLevelIsMethod() {
        assert metric.metricLevel == MetricLevel.METHOD
    }

    void testCalculate_EmptyResultsForClassWithNoMethods() {
        final SOURCE = """
            int myValue
        """
        assertApplyToClass(SOURCE, ZERO_VECTOR, ZERO_VECTOR, null)
    }

    void testCalculate_ResultsForClassWithOneMethod() {
        final SOURCE = """
            def a() {
                def x = 1               // A=1
            }
        """
        assertApplyToClass(SOURCE, [1, 0, 0], [1,0,0], [a:[1, 0, 0]])
    }

    void testCalculate_ResultsForClassWithSeveralMethods() {
        final SOURCE = """
            def a() {
                def x = 1; y = x            // A=2
            }
            def b() {
                new SomeClass(99)           // B=1
                new SomeClass().run()       // B=2
                x++                         // A=1
            }
            def c() {
                switch(x) {
                    case 1: break           // C=1
                    case 3: break           // C=1
                }
                return x && x > 0 && x < 100 && !ready      // C=4
            }
        """
        assertApplyToClass(SOURCE, [3,3,6], [1,1,2], [a:[2,0,0], b:[1,3,0], c:[0,0,6]])
    }

    void testCalculate_ResultsForClassWithOneClosureField() {
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

    protected void assertApplyToClass(String source, classTotalValue, classAverageValue, Map methodValues) {
        def classNode = parseClass(source)
        def results = metric.applyToClass(classNode, sourceCode)
        log("results=$results")
        def classMetricResult = results.classMetricResult
        AbcTestUtil.assertEquals(classMetricResult.averageAbcVector, classAverageValue)
        AbcTestUtil.assertEquals(classMetricResult.totalAbcVector, classTotalValue)

        def methodMetricResults = results.methodMetricResults
        def methodNames = methodValues?.keySet()
        methodNames.each { methodName ->
            def methodValue = methodMetricResults[methodName].abcVector
            AbcTestUtil.assertEquals(methodValue, methodValues[methodName])
        }
    }

 }