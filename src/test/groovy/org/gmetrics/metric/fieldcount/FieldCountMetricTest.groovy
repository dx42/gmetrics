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
package org.gmetrics.metric.fieldcount

import org.gmetrics.metric.AbstractMetricTestCase
import org.gmetrics.metric.MetricLevel
import org.junit.jupiter.api.Test

/**
 * Tests for FieldCountMetric
 *
 * @author Chris Mair
 */
class FieldCountMetricTest extends AbstractMetricTestCase {

    static Class metricClass = FieldCountMetric

    @Test
	void testBaseLevelIsClass() {
        assert metric.baseLevel == MetricLevel.CLASS
    }

    @Test
	void testHasProperName() {
        assert metric.name == 'FieldCount'
    }

    @Test
	void testApplyToClass_NoFields() {
        final SOURCE = """
            class MyClass {
                int getValue() { }
            }
        """
        assertApplyToClass(SOURCE, 0, 0)
    }

    @Test
	void testApplyToClass_OneField() {
        final SOURCE = """
            class MyClass {
                int myValue
                void printMe() { println this }
            }
        """
        assertApplyToClass(SOURCE, 1, 1)
    }

    @Test
	void testApplyToClass_TwoFields() {
        final SOURCE = """
            class MyClass {
                final myValue = 7

                int calculateStuff(int max) {
                    return max + 1
                }

                def name
            }
        """
        assertApplyToClass(SOURCE, 2, 2)
    }

    @Test
	void testApplyToClass_StaticField() {
        final SOURCE = """
            class MyClass {
                private static final int MAX = 99
            }
        """
        assertApplyToClass(SOURCE, 1, 1)
    }

    @Test
	void testApplyToClass_InterfaceWithConstant() {
        final SOURCE = """
            interface MyInterface {
                static final String NAME = 'abc'

            }
        """
        assertApplyToClass(SOURCE, 1, 1)
    }

    @Test
	void testApplyToClass_ReturnsNullForSyntheticClass() {
        final SOURCE = """
            int value = 7
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