/*
 * Copyright 2008 the original author or authors.
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
package org.gmetrics.resultsnode

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.NumberMetricResult
import org.gmetrics.metric.StubMetric

/**
 * Tests for MethodResultsNode
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class MethodResultsNodeTest extends AbstractTestCase {

    private static final METRIC = new StubMetric()
    private static final METRIC_RESULT1 = new NumberMetricResult(METRIC, 1)
    private static final METRIC_RESULT2 = new NumberMetricResult(METRIC, 2)
    private static final METRIC_RESULT3 = new NumberMetricResult(METRIC, 3)

    private methodResultsNode

    void testThatMetricLevelIsMethodLevel() {
        assert methodResultsNode.level == MetricLevel.METHOD
    }

    void testThatContainsClassResultsIsFalse() {
        assert !methodResultsNode.containsClassResults()
    }

    void test_InitialMetricValuesIsEmpty() {
        assert methodResultsNode.getMetricResults() == []
    }

    void test_ChildrenIsAlwaysEmpty() {
        assert methodResultsNode.getChildren() == [:]
    }

    void test_addMetricResult_NullThrowsException() {
        shouldFailWithMessageContaining('metricResult') { methodResultsNode.addMetricResult(null) }
    }

    void test_AddingSeveralMetricResults() {
        methodResultsNode.addMetricResult(METRIC_RESULT1)
        methodResultsNode.addMetricResult(METRIC_RESULT2)
        methodResultsNode.addMetricResult(METRIC_RESULT3)
        def metricValues = methodResultsNode.getMetricResults()
        assert metricValues.total == [1, 2, 3]
        assert metricValues.metric == [METRIC, METRIC, METRIC]
    }

    void test_getMetricResult_NullMetricThrowsException() {
        shouldFailWithMessageContaining('metric') { methodResultsNode.getMetricResult(null) }
    }

    void test_getMetricResult_ReturnsCorrectMetricResult() {
        def metric2 = new StubMetric()
        def metric3 = new StubMetric()
        methodResultsNode.addMetricResult(new NumberMetricResult(metric2, 2))
        methodResultsNode.addMetricResult(METRIC_RESULT1)
        methodResultsNode.addMetricResult(new NumberMetricResult(metric3, 3))

        assert methodResultsNode.getMetricResult(METRIC) == METRIC_RESULT1
    }

    void test_getMetricResult_ReturnsNullIfNoMatchingMetricResultIsFound() {
        assert methodResultsNode.getMetricResult(METRIC) == null
    }

    void setUp() {
        super.setUp()
        methodResultsNode = new MethodResultsNode()
    }

}