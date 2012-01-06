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
package org.gmetrics.result

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel

/**
 * Tests for NumberMetricResult
 *
 * @author Chris Mair
 */
class NumberMetricResultTest extends AbstractTestCase {
    private static final DEFAULT_FUNCTIONS = ['total', 'average', 'minimum', 'maximum']
    private static final METRIC = [getName:{'TestMetric'}, getFunctions:{ DEFAULT_FUNCTIONS }] as Metric

    void testPassingNullMetricIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('metric') { new SingleNumberMetricResult(null, MetricLevel.METHOD, 1) }
    }

    void testPassingNullMetricLevelIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('metricLevel') { new SingleNumberMetricResult(METRIC, null, 1) }
    }

    void testPassingNullValueIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('number') { new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, null) }
    }

    void testGetMetricIsSameMetricValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23)
        assert result.getMetric() == METRIC
    }

    void testGetMetricLevelIsSameMetricValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23)
        assert result.getMetricLevel() == MetricLevel.METHOD
    }

    void testGetLineNumberIsSameValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23, 67)
        assert result.getLineNumber() == 67
    }

    void testGetTotalValueIsSameIntegerValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23)
        assert result['total'] == 23
    }

    void testGetTotalValueIsSameBigDecimalValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 0.23456)
        assert result['total'] == 0.23456
    }

    void testGetAverageValueIsSameIntegerValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23)
        assert result['average'] == 23
    }

    void testGetMinimumValueIsSameIntegerValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23)
        assert result['minimum'] == 23
    }

    void testGetMaximumValueIsSameIntegerValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23)
        assert result['maximum'] == 23
    }

    void testGetCountIsOneForSingleValue() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 0.23456)
        assert result.getCount() == 1
    }

    void testGetValueForUnknownFunctionIsNull() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 0.23456)
        assert result['xxx'] == null
    }

    void testUsesFunctionNamesFromMetric() {
        final FUNCTION_NAMES = ['average', 'maximum']
        def metric = [getName:{'TestMetric'}, getFunctions:{ FUNCTION_NAMES }] as Metric
        def result = new SingleNumberMetricResult(metric, MetricLevel.METHOD, 1)
        assert result['average'] != null
        assert result['maximum'] != null
        assert result['total'] == null
        assert result['minimum'] == null
    }

}