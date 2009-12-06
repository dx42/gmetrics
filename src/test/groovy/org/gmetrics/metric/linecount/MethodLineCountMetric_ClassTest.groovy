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

import org.gmetrics.metric.AbstractMetricTest
import org.gmetrics.metric.MetricLevel

/**
 * Tests for MethodLinesOfCodeMetric - calculate aggregate metrics for a class
 *
 * @author Chris Mair
 * @version $Revision: 224 $ - $Date: 2009-09-22 22:04:03 -0400 (Tue, 22 Sep 2009) $
 */
class MethodLineCountMetric_ClassTest extends AbstractMetricTest {
    static metricClass = MethodLineCountMetric

    void testMetricLevelIsMethod() {
        assert metric.baseLevel == MetricLevel.METHOD
    }

    void testApplyToClass_ZeroResultsForClassWithNoMethods() {
        final SOURCE = """
            int myValue
        """
        assertApplyToClass(SOURCE, 0, 0, null)
    }

    void testApplyToClass_ZeroResultsForInterface() {
        final SOURCE = """
            interface MyInterface {
                int doSomething(String name)
            }
        """
        assertApplyToClass(SOURCE, 0, 0, null)
    }

    void testApplyToClass_ResultsForClassWithOneMethod() {
        final SOURCE = """
            def a() {
                def x = 1
            }
        """
        assertApplyToClass(SOURCE, 3, 3, [a:3])
    }

    void testApplyToClass_ResultsForClassWithSeveralMethods() {
        final SOURCE = """
            def a() {                   // 3
                def x = 1; y = x
            }
            def b() {                   // 5
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
        """
        assertApplyToClass(SOURCE, 16, scale(16/3), [a:3, b:5, c:8])
    }

    void testApplyToClass_ResultsForClassWithOneClosureField() {
        final SOURCE = """
            class MyClass {
                def myClosure = {
                    def x = 1; x++
                    doSomething()
                    if (x == 23) return 99 else return 0
                }
            }
        """
        assertApplyToClass(SOURCE, 5, 5, [myClosure:5])
    }

    void testApplyToPackage_ResultsForNoChildren() {
        assertApplyToPackage([], 0, 0)
    }

    void testApplyToPackage_ResultsForOneChild() {
        assertApplyToPackage([metricResult(23)], 23, 23)
    }

    void testApplyToPackage_ResultsForThreeChildren() {
        assertApplyToPackage([metricResult(20), metricResult(6), metricResult(4)], 30, 10)
    }

}