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
 * Tests for MutableMapMetricResult
 *
 * @author Chris Mair
 */
class MutableMapMetricResultTest extends AbstractTestCase {

    private static final List DEFAULT_FUNCTIONS = ['total', 'average', 'minimum', 'maximum']
    private static final Metric METRIC = [getName:{'TestMetric'}, getFunctions:{ DEFAULT_FUNCTIONS }] as Metric
    private static final Map MAP = [a:123, b:'xyz']

    @Test
	void testPassingNullMetricIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('metric') { new MutableMapMetricResult(null, MetricLevel.METHOD, MAP) }
    }

    @Test
	void testPassingNullMetricLevelIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('metricLevel') { new MutableMapMetricResult(METRIC, null, MAP) }
    }

    @Test
	void testPassingNullValueIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('map') { new MutableMapMetricResult(METRIC, MetricLevel.METHOD, null) }
    }

    @Test
	void testGetMetricIsSameMetricValuePassedIntoConstructor() {
        def result = new MutableMapMetricResult(METRIC, MetricLevel.METHOD, MAP)
        assert result.getMetric() == METRIC
    }

    @Test
	void testGetMetricLevelIsSameMetricValuePassedIntoConstructor() {
        def result = new MutableMapMetricResult(METRIC, MetricLevel.METHOD, MAP)
        assert result.getMetricLevel() == MetricLevel.METHOD
    }

    @Test
	void testGetAt_ReturnsValuesPassedIntoConstructor() {
        def result = new MutableMapMetricResult(METRIC, MetricLevel.METHOD, MAP)
        assert result['a'] == 123
        assert result['b'] == 'xyz'
    }

    @Test
	void testGetAt_ReturnsNullForUndefinedValue() {
        def result = new MutableMapMetricResult(METRIC, MetricLevel.METHOD, MAP)
        assert result['unknnown'] == null
    }

    @Test
	void testChangesToOriginalMapHaveNoEffect() {
        def map = [a:123]
        def result = new MutableMapMetricResult(METRIC, MetricLevel.METHOD, map)
        map['a'] = 99
        assert result['a'] == 123
    }

    @Test
	void testModifyingMap() {
        def result = new MutableMapMetricResult(METRIC, MetricLevel.METHOD, [a:123])
        assert result['a'] == 123
        result.map['a'] = 99
        assert result['a'] == 99
    }

    @Test
	void testModifyingValuesUsingSubscriptOperator() {
        def result = new MutableMapMetricResult(METRIC, MetricLevel.METHOD, [a:123])
        assert result['a'] == 123
        result['a'] = 99
        assert result['a'] == 99
    }

    @Test
	void testGetCount_DefaultsToZero() {
        def result = new MutableMapMetricResult(METRIC, MetricLevel.METHOD, MAP)
        assert result.getCount() == 0
    }

    @Test
	void testGetCount() {
        def result = new MutableMapMetricResult(METRIC, MetricLevel.METHOD, MAP)
        result.count = 7
        assert result.getCount() == 7
    }

}