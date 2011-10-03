/*
 * Copyright 2011 the original author or authors.
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
package org.gmetrics.metric.classcount

import org.gmetrics.metric.AbstractMetricTestCase
import org.gmetrics.metric.MetricLevel

/**
 * Tests for ClassCountMetric
 *
 * @author Chris Mair
 */
class ClassCountMetricTest extends AbstractMetricTestCase {

    static metricClass = ClassCountMetric

    void testBaseLevelIsClass() {
        assert metric.baseLevel == MetricLevel.PACKAGE
    }

    void testHasProperName() {
        assert metric.name == 'ClassCount'
    }

    void testApplyToClass_SingleClass() {
        final SOURCE = """
            class MyClass { int myValue }
        """
        assertApplyToClass(SOURCE, 1)
    }

    void testApplyToClass_Enum() {
        final SOURCE = """
            enum MyEnum { ONE, TWO, THREE }
        """
        assertApplyToClass(SOURCE, 1)
    }

    void testApplyToClass_Interface() {
        final SOURCE = """
            interface MyInterface {
                int doSomething(String name)
            }
        """
        assertApplyToClass(SOURCE, 1)
    }

   void testApplyToPackage_ResultsForNoChildren() {
        assertApplyToPackage([], 0, 0)
    }

    void testApplyToPackage_ResultsForOneChildClass() {
        assertApplyToPackage([metricResultForClass(1)], 1, 1)
    }

    void testApplyToPackage_ResultsForThreeChildClasses() {
        assertApplyToPackage([metricResultForClass(1), metricResultForClass(1), metricResultForClass(1)], 3, 1)
    }

    void testApplyToPackage_ResultsForClassesAndSubPackages_IgnoreSubPackages() {
        assertApplyToPackage([
            metricResultForClass(1), metricResultForClass(1), metricResultForPackage(99), metricResultForClass(1)
        ], 3, 1)
    }

}