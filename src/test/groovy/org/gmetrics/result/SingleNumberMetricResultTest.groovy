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
import org.junit.jupiter.api.Test

/**
 * Tests for SingleNumberMetricResult
 *
 * @author Chris Mair
 */
class SingleNumberMetricResultTest extends AbstractTestCase {

    private static final List DEFAULT_FUNCTIONS = ['total', 'average', 'minimum', 'maximum']
    private static final Metric METRIC = [getName:{'TestMetric'}, getFunctions:{ DEFAULT_FUNCTIONS }] as Metric

    @Test
	void testPassingNullMetricIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('metric') { new SingleNumberMetricResult(null, MetricLevel.METHOD, 1) }
    }

    @Test
	void testPassingNullMetricLevelIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('metricLevel') { new SingleNumberMetricResult(METRIC, null, 1) }
    }

    @Test
	void testPassingNullValueIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('number') { new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, null) }
    }

    @Test
	void testGetMetricIsSameMetricValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23)
        assert result.getMetric() == METRIC
    }

    @Test
	void testGetMetricLevelIsSameMetricValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23)
        assert result.getMetricLevel() == MetricLevel.METHOD
    }

    @Test
	void testGetLineNumberIsSameValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23, 67)
        assert result.getLineNumber() == 67
    }

    @Test
	void testGetTotalValueIsSameIntegerValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23)
        assert result['total'] == 23
    }

    @Test
	void testGetTotalValueIsSameBigDecimalValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 0.23456)
        assert result['total'] == 0.23456
    }

    @Test
	void testGetAverageValueIsSameIntegerValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23)
        assert result['average'] == 23
    }

    @Test
	void testGetMinimumValueIsSameIntegerValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23)
        assert result['minimum'] == 23
    }

    @Test
	void testGetMaximumValueIsSameIntegerValuePassedIntoConstructor() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 23)
        assert result['maximum'] == 23
    }

    @Test
	void testGetCountIsOneForSingleValue() {
        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 0.23456)
        assert result.getCount() == 1
    }

//    @Test
//	 void testGetValueForUnknownFunctionIsNull() {
//        def result = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 0.23456)
//        assert result['xxx'] == null
//    }

//    @Test
//	 void testUsesFunctionNamesFromMetric() {
//        final FUNCTION_NAMES = ['average', 'maximum']
//        def metric = [getName:{'TestMetric'}, getFunctions:{ FUNCTION_NAMES }] as Metric
//        def result = new SingleNumberMetricResult(metric, MetricLevel.METHOD, 1)
//        assert result['average'] != null
//        assert result['maximum'] != null
//        assert result['total'] == null
//        assert result['minimum'] == null
//    }

}