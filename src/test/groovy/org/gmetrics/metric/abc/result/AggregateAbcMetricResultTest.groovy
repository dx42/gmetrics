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
import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.abc.result.AggregateAbcMetricResult
import org.gmetrics.metric.abc.AbcTestUtil
import org.gmetrics.metric.abc.AbcVector

/**
 * Tests for AggregateAbcMetricResult
 *
 * @author Chris Mair
 * @version $Revision: 237 $ - $Date: 2009-11-09 21:17:54 -0500 (Mon, 09 Nov 2009) $
 */
class AggregateAbcMetricResultTest extends AbstractTestCase {

    private static final METRIC = [:] as Metric
    private aggregateAbcMetricResult

    void testConstructorThrowsExceptionForNullMetricParameter() {
        shouldFailWithMessageContaining('metric') { new AggregateAbcMetricResult(null, []) }
    }

    void testConstructorThrowsExceptionForNullChildrenParameter() {
        shouldFailWithMessageContaining('children') { new AggregateAbcMetricResult(METRIC, null) }
    }

    void testConstructorSetsMetricProperly() {
        def mr = new AggregateAbcMetricResult(METRIC, [])
        assert mr.metric == METRIC
    }

    void testAverageAbcVectorForNoVectorsIsZeroVector() {
        initializeWithZeroChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.averageAbcVector, [0, 0, 0])
    }

    void testTotalAbcVectorForNoVectorsIsZeroVector() {
        initializeWithZeroChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.totalAbcVector, [0, 0, 0])
    }

    void testAverageValueForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult.average == 0
    }

    void testTotalValueForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult.total == 0
    }

    void testCountForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult.count == 0
    }

    void testAverageAbcVectorForSingleVectorIsThatVector() {
        initializeWithOneChildMetricResult()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.averageAbcVector, [7, 9, 21])
    }

    void testTotalAbcVectorForSingleVectorIsThatVector() {
        initializeWithOneChildMetricResult()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.totalAbcVector, [7, 9, 21])
    }

    void testCorrectRoundedAverageForSeveralVectors() {
        initializeWithThreeChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.averageAbcVector, [9, 4, 23])     // A is rounded down; C is rounded up
    }

    void testCorrectTotalAbcVectorForSeveralVectors() {
        initializeWithThreeChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.totalAbcVector, [27, 12, 68])
    }

    void testAbcVectorIsTheSameAsTheTotalAbcVector() {
        initializeWithThreeChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.abcVector, [27, 12, 68])
    }

    void testTotalValueForSeveralVectorsIsTheMagnitudeOfTheSumOfTheVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult.total == new AbcVector(27, 12, 68).magnitude
    }

    void testAverageValueForSeveralVectorsIsTheMagnitudeOfTheAverageOfTheVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult.average == new AbcVector(9, 4, 23).magnitude
    }

    void testCorrectCountForSeveralVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult.count == 3
    }

    void testCorrectCountForChildResultsWithCountsGreaterThanOne() {
        initializeWithThreeChildMetricResults()
        def aggregate = new AggregateAbcMetricResult(METRIC, [aggregateAbcMetricResult, aggregateAbcMetricResult])
        assert aggregate.count == 6
    }

    private void initializeWithZeroChildMetricResults() {
        aggregateAbcMetricResult = new AggregateAbcMetricResult(METRIC, [])
    }

    private void initializeWithOneChildMetricResult() {
        def children = [AbcTestUtil.abcMetricResult(METRIC, 7, 9, 21)]
        aggregateAbcMetricResult = new AggregateAbcMetricResult(METRIC, children)
    }

    private void initializeWithThreeChildMetricResults() {
        def child1 = AbcTestUtil.abcMetricResult(METRIC, 7, 9, 21)
        def child2 = AbcTestUtil.abcMetricResult(METRIC, 11, 1, 22)
        def child3 = AbcTestUtil.abcMetricResult(METRIC, 9, 2, 25)
        aggregateAbcMetricResult = new AggregateAbcMetricResult(METRIC, [child1, child2, child3])
    }
}