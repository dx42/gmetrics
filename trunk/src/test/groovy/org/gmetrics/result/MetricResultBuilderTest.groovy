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

import static FunctionNames.*

import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel
import org.gmetrics.test.AbstractTestCase

/**
 * Tests for MetricResultBuilder
 *
 * @author Chris Mair
 */
class MetricResultBuilderTest extends AbstractTestCase {

    private static final DEFAULT_FUNCTIONS = [TOTAL, AVERAGE, MINIMUM, MAXIMUM]
    private static final METRIC = [getName:{'TestMetric'}, getFunctions:{ DEFAULT_FUNCTIONS }] as Metric
    private static final BD = [0.23, 5.01, 3.67]
    private static final LINE_NUM = 67

    private metricResultBuilder = new MetricResultBuilder(metric:METRIC, metricLevel:MetricLevel.METHOD)

    void testCreateAggregateMetricResult_ThrowsExceptionForNullMetric() {
        def builder = new MetricResultBuilder(metricLevel:MetricLevel.METHOD)
        shouldFailWithMessageContaining('metric') { builder.createAggregateMetricResult([], null) }
    }

    void testCreateAggregateMetricResult_ThrowsExceptionForNullMetricLevel() {
        def builder = new MetricResultBuilder(metric:METRIC)
        shouldFailWithMessageContaining('metricLevel') { builder.createAggregateMetricResult([], null) }
    }

    void testCreateAggregateMetricResult_ThrowsExceptionForNullChildrenParameter() {
        shouldFailWithMessageContaining('children') { metricResultBuilder.createAggregateMetricResult(null, null) }
    }

    void testGetLineNumberIsSameValuePassedIntoCreateAggregateMetricResult() {
        def result = metricResultBuilder.createAggregateMetricResult([], 67)
        assert result.getLineNumber() == 67
    }

    // Tests for no children

    void testFunctionValuesForNoChildrenAreAllZero() {
        def result = noChildMetricResult()
        assertMetricResultFunctionValues(result, [average:0, total:0, minimum:0, maximum:0])
    }

    void testCountForNoChildrenIsZero() {
        def result = noChildMetricResult()
        assert result.count == 0
    }

    void testFunctionValuesForChildrenNullChildFunctionValues() {
        def children = [new StubMetricResult(metric:METRIC, metricLevel:MetricLevel.METHOD)]
        def result = metricResultBuilder.createAggregateMetricResult(children, null)
        assertMetricResultFunctionValues(result, [average:0, total:0])
    }

    // Tests for a single child

    void testFunctionValuesForASingleMetricAreAllThatMetricValue() {
        def result = oneChildMetricResult(99.5)
        assertMetricResultFunctionValues(result, [average:99.5, total:99.5, minimum:99.5, maximum:99.5])
    }

    // Tests for several children

    void testAverageValueForSeveralIntegerMetricsIsTheAverageOfTheMetricValues() {
        def result = threeIntegerChildMetricResult()
        assert result[AVERAGE] == scale(25 / 3)
    }

    void testTotalValueForSeveralIntegerMetricsIsTheSumOfTheMetricValues() {
        def result = threeIntegerChildMetricResult()
        assert result[TOTAL] == 25
    }

    void testMinimumValueForSeveralIntegerMetrics() {
        def result = threeIntegerChildMetricResult()
        assert result[MINIMUM] == 1
    }

    void testMaximumValueForSeveralIntegerMetrics() {
        def result = threeIntegerChildMetricResult()
        assert result[MAXIMUM] == 21
    }

    void testTotalValueForSeveralBigDecimalMetricsIsTheSumOfTheMetricValues() {
        def result = threeBigDecimalChildMetricResult()
        assert result[TOTAL] == BD[0] + BD[1] + BD[2]
    }

    void testAverageValueForSeveralBigDecimalMetricsIsTheAverageOfTheMetricValues() {
        def result = threeBigDecimalChildMetricResult()
        def sum = (BD[0] + BD[1] + BD[2])
        assert result[AVERAGE] == scale(sum / 3)
    }

    void testMinimumValueForSeveralBigDecimalMetrics() {
        def result = threeBigDecimalChildMetricResult()
        assert result[MINIMUM] == BD.min()
    }

    void testMaximumValueForSeveralBigDecimalMetrics() {
        def result = threeBigDecimalChildMetricResult()
        assert result[MAXIMUM] == BD.max()
    }

    void testCorrectCountForSeveralChildResults() {
        def result = threeIntegerChildMetricResult()
        assert result.count == 3
    }

    void testCorrectCountForChildResultsWithCountsGreaterThanOne() {
        def children = [new StubMetricResult(count:3, total:0), new StubMetricResult(count:7, total:0)]
        def result = metricResultBuilder.createAggregateMetricResult(children, null)
        assert result.count == 10
    }

    // Tests for predefinedValues

    void testPredefinedValues_OnlyUsesPredefinedValueThatWasSpecified() {
        def result = oneChildMetricResult(99.5, [total:66])
        assertMetricResultFunctionValues(result, [average:99.5, total:66, minimum:99.5, maximum:99.5])
    }

    void testPredefinedValues_UsesAllPredefinedValues() {
        def result = oneChildMetricResult(99.5, [total:66, average:55, minimum:44, maximum:88])
        assertMetricResultFunctionValues(result, [average:55, total:66, minimum:44, maximum:88])
    }

    // Other tests

    void testDefaultScaleIsAppliedToAverageValue() {
        def children = [new StubMetricResult(count:3, total:10)]
        def result = metricResultBuilder.createAggregateMetricResult(children, null)
        assert result[AVERAGE] == scale(10/3)
    }

    void testGetAt_NoSuchFunctionName_ReturnsNull() {
        def result = oneChildMetricResult(99.5)
        assert result['xxx'] == null
    }

    void testConfiguredScaleIsAppliedToAverageValue() {
        def children = [new StubMetricResult(count:3, total:10)]
        def builder = new MetricResultBuilder(metric:METRIC, metricLevel:MetricLevel.METHOD, scale:3)
        def result = builder.createAggregateMetricResult(children, null)
        assert result[AVERAGE] == scale(10/3, 3)
    }

    void testUsesFunctionNamesFromMetric() {
        final FUNCTION_NAMES = [AVERAGE, MAXIMUM]
        def metric = [getName:{'TestMetric'}, getFunctions:{ FUNCTION_NAMES }] as Metric
        metricResultBuilder = new MetricResultBuilder(metric:metric, metricLevel:MetricLevel.METHOD)
        def result = metricResultBuilder.createAggregateMetricResult([], LINE_NUM)
        assertMetricResultFunctionValues(result, [average:0, total:null, minimum:null, maximum:0])
    }

    //--------------------------------------------------------------------------
    // Helper Methods
    //--------------------------------------------------------------------------

    private MetricResult noChildMetricResult() {
        return metricResultBuilder.createAggregateMetricResult([], null)
    }

    private MetricResult oneChildMetricResult(value, Map predefinedValues=null) {
        def children = [new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, value)]
        return metricResultBuilder.createAggregateMetricResult(children, LINE_NUM, predefinedValues)
    }

    private MetricResult threeIntegerChildMetricResult() {
        threeChildMetricResult(21, 1, 3)
    }

    private MetricResult threeBigDecimalChildMetricResult() {
        threeChildMetricResult(BD[0], BD[1], BD[2])
    }

    private MetricResult threeChildMetricResult(x, y, z) {
        def children = [new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, x),new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, y), new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, z)]
        return metricResultBuilder.createAggregateMetricResult(children, LINE_NUM)
    }

    private void assertMetricResultFunctionValues(MetricResult metricResult, Map<String,Number> functionValues) {
        assert metricResult[AVERAGE] == functionValues[AVERAGE]
        assert metricResult[TOTAL] == functionValues[TOTAL]
        assert metricResult[MINIMUM] == functionValues[MINIMUM]
        assert metricResult[MAXIMUM] == functionValues[MAXIMUM]
    }

}