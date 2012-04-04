/*
 * Copyright 2012 the original author or authors.
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
package org.gmetrics.result

import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel
import org.gmetrics.test.AbstractTestCase

/**
 * Tests for NumberMetricResult
 *
 * @author Chris Mair
 */
class NumberMetricResultTest extends AbstractTestCase {

    private static final DEFAULT_FUNCTIONS = ['total', 'average', 'minimum', 'maximum']
    private static final METRIC = [getName:{'TestMetric'}, getFunctions:{ DEFAULT_FUNCTIONS }] as Metric
    private static final VALUES = [total:23]

    void testPassingNullMetricIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('metric') { new NumberMetricResult(null, MetricLevel.METHOD, [:]) }
    }

    void testPassingNullMetricLevelIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('metricLevel') { new NumberMetricResult(METRIC, null, [:]) }
    }

    void testPassingNullValueIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('values') { new NumberMetricResult(METRIC, MetricLevel.METHOD, null) }
    }

    void testGetMetricIsSameMetricValuePassedIntoConstructor() {
        def result = new NumberMetricResult(METRIC, MetricLevel.METHOD, VALUES)
        assert result.getMetric() == METRIC
    }

    void testGetMetricLevelIsSameMetricValuePassedIntoConstructor() {
        def result = new NumberMetricResult(METRIC, MetricLevel.METHOD, VALUES)
        assert result.getMetricLevel() == MetricLevel.METHOD
    }

    void testGetLineNumberIsSameValuePassedIntoConstructor() {
        def result = new NumberMetricResult(METRIC, MetricLevel.METHOD, VALUES, 67)
        assert result.getLineNumber() == 67
    }

    void testGetCountIsSameValuePassedIntoConstructor() {
        def result = new NumberMetricResult(METRIC, MetricLevel.METHOD, VALUES, null, 5)
        assert result.getCount() == 5
    }

    void testGetAt_SameValuesPassedIntoConstructor() {
        def result = new NumberMetricResult(METRIC, MetricLevel.METHOD, [total:2, average:3, maximum:4, minimum:5])
        assert result['total'] == 2
        assert result['average'] == 3
        assert result['maximum'] == 4
        assert result['minimum'] == 5
    }

    void testGetAt_BigDecimalValuesPassedIntoConstructor() {
        def result = new NumberMetricResult(METRIC, MetricLevel.METHOD, [total:99.23456, average:2.5, minimum:0.7, maximum:9.8])
        assert result['total'] == 99.23456
        assert result['average'] == 2.5
        assert result['minimum'] == 0.7
        assert result['maximum'] == 9.8
    }

    void testGetAt_NullForUnspecifiedValue() {
        def result = new NumberMetricResult(METRIC, MetricLevel.METHOD, [:])
        assert result['total'] == null
        assert result['average'] == null
        assert result['maximum'] == null
        assert result['minimum'] == null
    }

    void testGetCount_DefaultsToOne() {
        def result = new NumberMetricResult(METRIC, MetricLevel.METHOD, VALUES)
        assert result.getCount() == 1
    }

    void testGetValueForUnknownFunctionIsNull() {
        def result = new NumberMetricResult(METRIC, MetricLevel.METHOD, VALUES)
        assert result['xxx'] == null
    }

//    void testUsesFunctionNamesFromMetric() {
//        final FUNCTION_NAMES = ['average', 'maximum']
//        def metric = [getName:{'TestMetric'}, getFunctions:{ FUNCTION_NAMES }] as Metric
//        def result = new NumberMetricResult(metric, MetricLevel.METHOD, [total:2, average:3, maximum:4, minimum:5])
//        assert result['average'] != null
//        assert result['maximum'] != null
//        assert result['total'] == null
//        assert result['minimum'] == null
//    }

}