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
import org.gmetrics.result.NumberMetricResult
import org.gmetrics.result.AggregateNumberMetricResult
import org.gmetrics.metric.Metric

/**
 * Tests for AggregateNumberMetricResults
 *
 * @author Chris Mair
 * @version $Revision: 228 $ - $Date: 2009-09-29 21:52:31 -0400 (Tue, 29 Sep 2009) $
 */
class AggregateNumberMetricResultTest extends AbstractTestCase {

    private static final METRIC = [:] as Metric
    private static final BD = [0.23, 5.01, 3.67]
    private aggregateNumberMetricResult

    void testConstructorThrowsExceptionForNullMetricParameter() {
        shouldFailWithMessageContaining('metric') { new AggregateNumberMetricResult(null, []) }
    }

    void testConstructorThrowsExceptionForNullChildrenParameter() {
        shouldFailWithMessageContaining('children') { new AggregateNumberMetricResult(METRIC, null) }
    }

    void testConstructorSetsMetricProperly() {
        def mr = new AggregateNumberMetricResult(METRIC, [])
        assert mr.metric == METRIC
    }

    void testAverageValueForNoChildrenIsZero() {
        initializeNoChildMetricResults()
        assert aggregateNumberMetricResult.averageValue == 0
    }

    void testTotalValueForNoChildrenIsZero() {
        initializeNoChildMetricResults()
        assert aggregateNumberMetricResult.totalValue == 0
    }

    void testCountForNoChildrenIsZero() {
        initializeNoChildMetricResults()
        assert aggregateNumberMetricResult.count == 0
    }

    void testAverageValueForASingleMetricIsThatMetricValue() {
        initializeOneChildMetricResult(99.5)
        assert aggregateNumberMetricResult.averageValue == 99.5
    }

    void testTotalValueForASingleMetricIsThatMetricValue() {
        initializeOneChildMetricResult(99.5)
        assert aggregateNumberMetricResult.totalValue == 99.5
    }

    void testAverageValueForSeveralIntegerMetricsIsTheAverageOfTheMetricValues() {
        initializeThreeIntegerChildMetricResults()
        assert aggregateNumberMetricResult.averageValue == scale(25 / 3)
    }

    void testTotalValueForSeveralIntegerMetricsIsTheSumOfTheMetricValues() {
        initializeThreeIntegerChildMetricResults()
        assert aggregateNumberMetricResult.totalValue == 25
    }


    void testTotalValueForSeveralBigDecimalMetricsIsTheSumOfTheMetricValues() {
        initializeThreeBigDecimalChildMetricResults()
        assert aggregateNumberMetricResult.totalValue == BD[0] + BD[1] + BD[2]
    }

    void testAverageValueForSeveralBigDecimalMetricsIsTheAverageOfTheMetricValues() {
        initializeThreeBigDecimalChildMetricResults()
        def sum = (BD[0] + BD[1] + BD[2])
        assert aggregateNumberMetricResult.averageValue == scale(sum / 3)
    }

    void testCorrectCountForSeveralChildResults() {
        initializeThreeIntegerChildMetricResults()
        assert aggregateNumberMetricResult.count == 3
    }

    void testCorrectCountForChildResultsWithCountsGreaterThanOne() {
        def children = [new StubMetricResult(count:3, totalValue:0), new StubMetricResult(count:7, totalValue:0)]
        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, children)
        assert aggregateNumberMetricResult.count == 10
    }

    void testDefaultScaleIsAppliedToAverageValue() {
        def children = [new StubMetricResult(count:3, totalValue:10)]
        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, children)
        assert aggregateNumberMetricResult.averageValue == scale(10/3)
    }

    void testConfiguredScaleIsAppliedToAverageValue() {
        def children = [new StubMetricResult(count:3, totalValue:10)]
        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, children)
        aggregateNumberMetricResult.scale = 3
        assert aggregateNumberMetricResult.averageValue == scale(10/3, 3)
    }

    private void initializeNoChildMetricResults() {
        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, [])
    }

    private void initializeOneChildMetricResult(value) {
        def children = [new NumberMetricResult(METRIC, value)]
        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, children)
    }

    private void initializeThreeIntegerChildMetricResults() {
        initializeThreeChildMetricResults(21, 1, 3)
    }

    private void initializeThreeBigDecimalChildMetricResults() {
        initializeThreeChildMetricResults(BD[0], BD[1], BD[2])
    }

    private void initializeThreeChildMetricResults(x, y, z) {
        def children = [new NumberMetricResult(METRIC, x),new NumberMetricResult(METRIC, y), new NumberMetricResult(METRIC, z)]
        aggregateNumberMetricResult = new AggregateNumberMetricResult(METRIC, children)
    }

}