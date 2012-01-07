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
 * Tests for MetricResultBuilder
 *
 * @author Chris Mair
 */
class MetricResultBuilderTest extends AbstractTestCase {

    private static final DEFAULT_FUNCTIONS = ['total', 'average', 'minimum', 'maximum']
    private static final METRIC = [getName:{'TestMetric'}, getFunctions:{ DEFAULT_FUNCTIONS }] as Metric
    private static final BD = [0.23, 5.01, 3.67]
    private static final LINE_NUM = 67

    private metricResultBuilder = new MetricResultBuilder(METRIC, MetricLevel.METHOD)

    void testConstructorThrowsExceptionForNullMetricParameter() {
        shouldFailWithMessageContaining('metric') { new MetricResultBuilder(null, MetricLevel.METHOD) }
    }

    void testConstructorThrowsExceptionForNullMetricLevelParameter() {
        shouldFailWithMessageContaining('metricLevel') { new MetricResultBuilder(METRIC, null) }
    }

    void testCreateAggregateMetricResult_ThrowsExceptionForNullChildrenParameter() {
        shouldFailWithMessageContaining('children') { metricResultBuilder.createAggregateMetricResult(null, null) }
    }

    void testConstructorSetsMetricProperly() {
        assert metricResultBuilder.metric == METRIC
    }

    void testConstructorSetsMetricLevelProperly() {
        assert metricResultBuilder.metricLevel == MetricLevel.METHOD
    }

    void testGetLineNumberIsSameValuePassedIntoCreateAggregateMetricResult() {
        def result = metricResultBuilder.createAggregateMetricResult([], 67)
        assert result.getLineNumber() == 67
    }

    // Tests for no children

    void testFunctionValuesForNoChildrenAreAllZero() {
//        initializeNoChildMetricResults()
        def result = metricResultBuilder.createAggregateMetricResult([], null)
        assert result['average'] == 0
        assert result['total'] == 0
        assert result['minimum'] == 0
        assert result['maximum'] == 0
    }

    void testCountForNoChildrenIsZero() {
//        initializeNoChildMetricResults()
        def result = metricResultBuilder.createAggregateMetricResult([], null)
        assert result.count == 0
    }

    void testFunctionValuesForChildrenNullChildFunctionValues() {
        def children = [new StubMetricResult(metric:METRIC, metricLevel:MetricLevel.METHOD)]
        def result = metricResultBuilder.createAggregateMetricResult(children, null)
        assert result['average'] == 0
        assert result['total'] == 0
        assert result['minimum'] == null
        assert result['maximum'] == null
    }

    // Tests for a single child

    void testFunctionValuesForASingleMetricAreAllThatMetricValue() {
        def result = oneChildMetricResult(99.5)
        assert result['average'] == 99.5
        assert result['total'] == 99.5
        assert result['minimum'] == 99.5
        assert result['maximum'] == 99.5
    }

    // Tests for several children

    void testAverageValueForSeveralIntegerMetricsIsTheAverageOfTheMetricValues() {
        def result = threeIntegerChildMetricResults()
        assert result['average'] == scale(25 / 3)
    }

    void testTotalValueForSeveralIntegerMetricsIsTheSumOfTheMetricValues() {
        def result = threeIntegerChildMetricResults()
        assert result['total'] == 25
    }

    void testMinimumValueForSeveralIntegerMetrics() {
        def result = threeIntegerChildMetricResults()
        assert result['minimum'] == 1
    }

    void testMaximumValueForSeveralIntegerMetrics() {
        def result = threeIntegerChildMetricResults()
        assert result['maximum'] == 21
    }

    void testTotalValueForSeveralBigDecimalMetricsIsTheSumOfTheMetricValues() {
        def result = threeBigDecimalChildMetricResults()
        assert result['total'] == BD[0] + BD[1] + BD[2]
    }

    void testAverageValueForSeveralBigDecimalMetricsIsTheAverageOfTheMetricValues() {
        def result = threeBigDecimalChildMetricResults()
        def sum = (BD[0] + BD[1] + BD[2])
        assert result['average'] == scale(sum / 3)
    }

    void testMinimumValueForSeveralBigDecimalMetrics() {
        def result = threeBigDecimalChildMetricResults()
        assert result['minimum'] == BD.min()
    }

    void testMaximumValueForSeveralBigDecimalMetrics() {
        def result = threeBigDecimalChildMetricResults()
        assert result['maximum'] == BD.max()
    }

    void testCorrectCountForSeveralChildResults() {
        def result = threeIntegerChildMetricResults()
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
        assert result['average'] == 99.5
        assert result['total'] == 66
        assert result['minimum'] == 99.5
        assert result['maximum'] == 99.5
    }

    void testPredefinedValues_UsesAllPredefinedValues() {
        def result = oneChildMetricResult(99.5, [total:66, average:55, minimum:44, maximum:88])
        assert result['average'] == 55
        assert result['total'] == 66
        assert result['minimum'] == 44
        assert result['maximum'] == 88
    }

    // Other tests

    void testDefaultScaleIsAppliedToAverageValue() {
        def children = [new StubMetricResult(count:3, total:10)]
        def result = metricResultBuilder.createAggregateMetricResult(children, null)
        assert result['average'] == scale(10/3)
    }

    void testGetAt_NoSuchFunctionName_ReturnsNull() {
        def result = oneChildMetricResult(99.5)
        assert result['xxx'] == null
    }

//    void testConfiguredScaleIsAppliedToAverageValue() {
//        def children = [new StubMetricResult(count:3, total:10)]
//        metricResultBuilder = new AggregateNumberMetricResult(METRIC, children)
//        metricResultBuilder.scale = 3
//        assert result['average'] == scale(10/3, 3)
//    }

    void testUsesFunctionNamesFromMetric() {
        final FUNCTION_NAMES = ['average', 'maximum']
        def metric = [getName:{'TestMetric'}, getFunctions:{ FUNCTION_NAMES }] as Metric
        metricResultBuilder = new MetricResultBuilder(metric, MetricLevel.METHOD)
        def result = metricResultBuilder.createAggregateMetricResult([], LINE_NUM)
        assert result['average'] != null
        assert result['maximum'] != null
        assert result['total'] == null
        assert result['minimum'] == null
    }

    //--------------------------------------------------------------------------
    // Helper Methods
    //--------------------------------------------------------------------------

//    private void initializeNoChildMetricResults() {
//        metricResultBuilder = new MetricResultBuilder(METRIC, MetricLevel.METHOD, [], LINE_NUM)
//    }

    private MetricResult oneChildMetricResult(value, Map predefinedValues=null) {
        def children = [new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, value)]
        return metricResultBuilder.createAggregateMetricResult(children, LINE_NUM, predefinedValues)
    }

    private MetricResult threeIntegerChildMetricResults() {
        threeChildMetricResults(21, 1, 3)
    }

    private MetricResult threeBigDecimalChildMetricResults() {
        threeChildMetricResults(BD[0], BD[1], BD[2])
    }

    private MetricResult threeChildMetricResults(x, y, z) {
        def children = [new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, x),new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, y), new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, z)]
        return metricResultBuilder.createAggregateMetricResult(children, LINE_NUM)
    }

}