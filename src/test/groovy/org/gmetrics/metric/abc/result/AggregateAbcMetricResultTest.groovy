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
package org.gmetrics.metric.abc.result

import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel
import org.gmetrics.metric.abc.AbcTestUtil
import org.gmetrics.metric.abc.AbcVector
import org.gmetrics.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for AggregateAbcMetricResult
 *
 * @author Chris Mair
 */
class AggregateAbcMetricResultTest extends AbstractTestCase {

    private static final DEFAULT_FUNCTIONS = ['total', 'average', 'minimum', 'maximum']
    private static final METRIC = [getFunctions:{ DEFAULT_FUNCTIONS }] as Metric
    private aggregateAbcMetricResult

    @Test
	void testConstructorThrowsExceptionForNullMetricParameter() {
        shouldFailWithMessageContaining('metric') { new AggregateAbcMetricResult(null, MetricLevel.METHOD, []) }
    }

    @Test
	void testConstructorThrowsExceptionForNullMetricLevelParameter() {
        shouldFailWithMessageContaining('metric') { new AggregateAbcMetricResult(METRIC, null, []) }
    }

    @Test
	void testConstructorThrowsExceptionForNullChildrenParameter() {
        shouldFailWithMessageContaining('children') { new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, null) }
    }

    @Test
	void testConstructorSetsMetricProperly() {
        def mr = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, [])
        assert mr.metric == METRIC
    }

    @Test
	void testConstructorSetsMetricLevelProperly() {
        def mr = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, [])
        assert mr.metricLevel == MetricLevel.METHOD
    }

    @Test
	void testGetLineNumberIsSameValuePassedIntoConstructor() {
        def result = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, [], 67)
        assert result.getLineNumber() == 67
    }

    // Tests for no children

    @Test
	void testAverageAbcVectorForNoVectorsIsZeroVector() {
        initializeWithZeroChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.averageAbcVector, [0, 0, 0])
    }

    @Test
	void testTotalAbcVectorForNoVectorsIsZeroVector() {
        initializeWithZeroChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.totalAbcVector, [0, 0, 0])
    }

//    @Test
//	  void testMinimumAbcVectorForNoVectorsIsZeroVector() {
//        initializeWithZeroChildMetricResults()
//        AbcTestUtil.assertEquals(aggregateAbcMetricResult.minimumAbcVector, [0, 0, 0])
//    }
//
//    @Test
//	  void testMaximumAbcVectorForNoVectorsIsZeroVector() {
//        initializeWithZeroChildMetricResults()
//        AbcTestUtil.assertEquals(aggregateAbcMetricResult.maximumAbcVector, [0, 0, 0])
//    }

    @Test
	void testAverageValueForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult['average'] == 0
    }

    @Test
	void testTotalValueForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult['total'] == 0
    }

    @Test
	void testMinimumValueForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult['minimum'] == 0
    }

    @Test
	void testMaximumValueForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult['maximum'] == 0
    }

    @Test
	void testCountForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult.count == 0
    }

    // Tests for single child

    @Test
	void testAverageAbcVectorForSingleVectorIsThatVector() {
        initializeWithOneChildMetricResult()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.averageAbcVector, [7, 9, 21])
    }

    @Test
	void testTotalAbcVectorForSingleVectorIsThatVector() {
        initializeWithOneChildMetricResult()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.totalAbcVector, [7, 9, 21])
    }

    @Test
	void testMinimumValueForSingleVectorsIsThatVectorMagnitude() {
        initializeWithOneChildMetricResult()
        assert aggregateAbcMetricResult['minimum'] == new AbcVector(7, 9, 21).magnitude
    }

    @Test
	void testMaximumValueForSingleVectorsIsThatVectorMagnitude() {
        initializeWithOneChildMetricResult()
        assert aggregateAbcMetricResult['maximum'] == new AbcVector(7, 9, 21).magnitude
    }

    // Tests for several children

    @Test
	void testCorrectRoundedAverageForSeveralVectors() {
        initializeWithThreeChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.averageAbcVector, [9, 4, 23])     // A is rounded down; C is rounded up
    }

    @Test
	void testCorrectTotalAbcVectorForSeveralVectors() {
        initializeWithThreeChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.totalAbcVector, [27, 12, 68])
    }

    @Test
	void testAbcVectorIsTheSameAsTheTotalAbcVector() {
        initializeWithThreeChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.abcVector, [27, 12, 68])
    }

    @Test
	void testTotalValueForSeveralVectorsIsTheMagnitudeOfTheSumOfTheVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult['total'] == new AbcVector(27, 12, 68).magnitude
    }

    @Test
	void testAverageValueForSeveralVectorsIsTheMagnitudeOfTheAverageOfTheVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult['average'] == new AbcVector(9, 4, 23).magnitude
    }

    @Test
	void testMinimumValueForSeveralVectorsIsTheMinimumMagnitudeOfTheVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult['minimum'] == new AbcVector(7, 9, 21).magnitude
    }

    @Test
	void testMaximumValueForSeveralVectorsIsTheMaximumMagnitudeOfTheVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult['maximum'] == new AbcVector(9, 2, 25).magnitude
    }

    @Test
	void testCorrectCountForSeveralVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult.count == 3
    }

    @Test
	void testCorrectCountForChildResultsWithCountsGreaterThanOne() {
        initializeWithThreeChildMetricResults()
        def aggregate = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, [aggregateAbcMetricResult, aggregateAbcMetricResult])
        assert aggregate.count == 6
    }

    // Other tests

    @Test
	void testGetValueForUnknownFunctionIsNull() {
        initializeWithOneChildMetricResult()
        assert aggregateAbcMetricResult['xxx'] == null
    }

    @Test
	void testUsesFunctionNamesFromMetric() {
        final FUNCTION_NAMES = ['average', 'maximum']
        def metric = [getName:{'TestMetric'}, getFunctions:{ FUNCTION_NAMES }] as Metric
        aggregateAbcMetricResult = new AggregateAbcMetricResult(metric, MetricLevel.METHOD, [])
        assert aggregateAbcMetricResult['average'] != null
        assert aggregateAbcMetricResult['maximum'] != null
        assert aggregateAbcMetricResult['total'] == null
        assert aggregateAbcMetricResult['minimum'] == null
    }

    //--------------------------------------------------------------------------
    // Helper Methods
    //--------------------------------------------------------------------------
    
    private void initializeWithZeroChildMetricResults() {
        aggregateAbcMetricResult = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, [])
    }

    private void initializeWithOneChildMetricResult() {
        def children = [AbcTestUtil.abcMetricResult(METRIC, 7, 9, 21)]
        aggregateAbcMetricResult = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, children)
    }

    private void initializeWithThreeChildMetricResults() {
        def child1 = AbcTestUtil.abcMetricResult(METRIC, 7, 9, 21)
        def child2 = AbcTestUtil.abcMetricResult(METRIC, 11, 1, 22)
        def child3 = AbcTestUtil.abcMetricResult(METRIC, 9, 2, 25)
        aggregateAbcMetricResult = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, [child1, child2, child3])
    }
}