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

/**
 * Tests for ClassLineCountMetric
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ClassLineCountMetricTest extends AbstractMetricTestCase {
    static metricClass = ClassLineCountMetric

    void testBaseLevelIsClass() {
        assert metric.baseLevel == MetricLevel.CLASS
    }

    void testHasProperName() {
        assert metric.name == 'ClassLineCount'
    }

    void testApplyToClass_SmallClass() {
        final SOURCE = """
            class MyClass {
                int myValue
            }
        """
        assertApplyToClass(SOURCE, 3, 3)
    }

    void testApplyToClass_LargerClass() {
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
        assertApplyToClass(SOURCE, 11, 11)
    }

    void testApplyToClass_Interface() {
        final SOURCE = """
            interface MyInterface {
                int doSomething(String name)

            }
        """
        assertApplyToClass(SOURCE, 4, 4)
    }

    void testApplyToClass_ReturnsNullForSyntheticClass() {
        final SOURCE = """
            println 123
        """
        def classNode = parseClass(SOURCE)
        def results = metric.applyToClass(classNode, sourceCode)
        log("results=$results")
        assert results == null
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