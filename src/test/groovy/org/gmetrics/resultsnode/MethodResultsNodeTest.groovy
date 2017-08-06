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

import org.gmetrics.metric.MetricLevel
import org.gmetrics.metric.StubMetric
import org.gmetrics.result.SingleNumberMetricResult
import org.gmetrics.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for MethodResultsNode
 *
 * @author Chris Mair
 */
class MethodResultsNodeTest extends AbstractTestCase {

    private static final NAME = 'name123'
    private static final SIGNATURE = 'signature123'
    private static final METRIC = new StubMetric()
    private static final METRIC_RESULT1 = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 1)
    private static final METRIC_RESULT2 = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 2)
    private static final METRIC_RESULT3 = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 3)

    private methodResultsNode = new MethodResultsNode(NAME)

    @Test	void testImplementsResultsNode() {
        assert methodResultsNode instanceof ResultsNode
    }

    @Test	void testNameAssignedFromConstructor() {
        assert methodResultsNode.name == NAME
    }

    @Test	void testNameAndSignatureAssignedFromConstructor() {
        def node = new MethodResultsNode(NAME, SIGNATURE)
        assert node.name == NAME
        assert node.signature == SIGNATURE
    }

    @Test	void testThatMetricLevelIsMethodLevel() {
        assert methodResultsNode.level == MetricLevel.METHOD
    }

    @Test	void testThatContainsClassResultsIsFalse() {
        assert !methodResultsNode.containsClassResults()
    }

    @Test	void test_InitialMetricValuesIsEmpty() {
        assert methodResultsNode.getMetricResults() == []
    }

    @Test	void test_ChildrenIsAlwaysEmpty() {
        assert methodResultsNode.getChildren() == [:]
    }

    @Test	void test_addMetricResult_NullThrowsException() {
        shouldFailWithMessageContaining('metricResult') { methodResultsNode.addMetricResult(null) }
    }

    @Test	void test_AddingSeveralMetricResults() {
        methodResultsNode.addMetricResult(METRIC_RESULT1)
        methodResultsNode.addMetricResult(METRIC_RESULT2)
        methodResultsNode.addMetricResult(METRIC_RESULT3)
        def metricValues = methodResultsNode.getMetricResults()
        assert metricValues.collect { it['total'] } == [1, 2, 3]
        assert metricValues.metric == [METRIC, METRIC, METRIC]
    }

    @Test	void test_getMetricResult_NullMetricThrowsException() {
        shouldFailWithMessageContaining('metric') { methodResultsNode.getMetricResult(null) }
    }

    @Test	void test_getMetricResult_ReturnsCorrectMetricResult() {
        def metric2 = new StubMetric()
        def metric3 = new StubMetric()
        methodResultsNode.addMetricResult(new SingleNumberMetricResult(metric2, MetricLevel.METHOD, 2))
        methodResultsNode.addMetricResult(METRIC_RESULT1)
        methodResultsNode.addMetricResult(new SingleNumberMetricResult(metric3, MetricLevel.METHOD, 3))

        assert methodResultsNode.getMetricResult(METRIC) == METRIC_RESULT1
    }

    @Test	void test_getMetricResult_ReturnsNullIfNoMatchingMetricResultIsFound() {
        assert methodResultsNode.getMetricResult(METRIC) == null
    }

}