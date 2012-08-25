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
import org.gmetrics.result.SingleNumberMetricResult
import org.gmetrics.metric.StubMetric
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.result.MethodKey

/**
 * Tests for ClassResultsNode
 *
 * @author Chris Mair
 */
class ClassResultsNodeTest extends AbstractTestCase {

    private static final NAME = 'name123'
    private static final FILE_NAME = 'filenameAbc'
    private static final FILE_PATH = 'abc/def'
    private static final METRIC = new StubMetric()
    private classResultsNode
    private classResult1, classResult2, classResult3

    void testImplementsResultsNode() {
        assert classResultsNode instanceof ResultsNode
    }

    void testNameAssignedFromConstructor() {
        assert classResultsNode.name == NAME
    }

    void testConstructorAssignsFileNameAndFilePath() {
        def newClassResultsNode = new ClassResultsNode(NAME, FILE_NAME, FILE_PATH)
        assert newClassResultsNode.fileName == FILE_NAME
        assert newClassResultsNode.filePath == FILE_PATH
    }

    void testThatMetricLevelIsClassLevel() {
        assert classResultsNode.level == MetricLevel.CLASS
    }

    void testThatContainsClassResultsIsTrue() {
        assert classResultsNode.containsClassResults()
    }

    void test_InitialMetricValuesIsEmpty() {
        assert classResultsNode.getMetricResults() == []
    }

    void test_InitialChildrenIsEmpty() {
        assert classResultsNode.getChildren() == [:]
    }

    void test_addClassMetricResult_NullClassMetricResultIsIgnored() {
        classResultsNode.addClassMetricResult(null)
        assert classResultsNode.getMetricResults() == []
        assert classResultsNode.getChildren() == [:]
    }

    void test_AddingASingleClassResultWithNoMethods() {
        classResultsNode.addClassMetricResult(classResult1)
        assert classResultsNode.metricResults.collect { it['total'] } == [1]
        assert classResultsNode.getChildren() == [:]
    }

    void test_AddingSeveralClassResult() {
        classResultsNode.addClassMetricResult(classResult1)
        classResultsNode.addClassMetricResult(classResult2)
        classResultsNode.addClassMetricResult(classResult3)
        assert classResultsNode.metricResults.collect { it['total'] } == [1, 2, 3]
        def children = classResultsNode.getChildren()
        log(children)
        assert children.every { k, v -> v instanceof MethodResultsNode }
        assert children.keySet().methodName as Set == ['a', 'b', 'c'] as Set
        assert children.values().name as Set == ['a', 'b', 'c'] as Set
        assert children[new MethodKey('a')].metricResults.collect { it['total'] } == [20, 30]
        assert children[new MethodKey('b')].metricResults.collect { it['total'] } == [20, 30]
        assert children[new MethodKey('c')].metricResults.collect { it['total'] } == [30]
    }

    void test_getMetricResult_NullMetricThrowsException() {
        shouldFailWithMessageContaining('metric') { classResultsNode.getMetricResult(null) }
    }

    void test_getMetricResult_ReturnsCorrectMetricResult() {
        final METRIC_RESULT = new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, 11)
        def metric2 = new StubMetric()
        def metric3 = new StubMetric()
        classResultsNode.addClassMetricResult(new ClassMetricResult(new SingleNumberMetricResult(metric2, MetricLevel.METHOD, 22), [:]))
        classResultsNode.addClassMetricResult(new ClassMetricResult(METRIC_RESULT, [:]))
        classResultsNode.addClassMetricResult(new ClassMetricResult(new SingleNumberMetricResult(metric3, MetricLevel.METHOD, 33), [:]))

        assert classResultsNode.getMetricResult(METRIC) == METRIC_RESULT
    }

    void test_getMetricResult_ReturnsNullIfNoMatchingMetricResultIsFound() {
        assert classResultsNode.getMetricResult(METRIC) == null
    }

    void setUp() {
        super.setUp()
        classResultsNode = new ClassResultsNode(NAME)
        classResult1 = new ClassMetricResult(m(1), [:])
        def methodKey1 = new MethodKey('a')
        def methodKey2 = new MethodKey('b')
        def methodKey3 = new MethodKey('c')
        classResult2 = new ClassMetricResult(m(2), [(methodKey1):m(20), (methodKey2):m(20)])
        classResult3 = new ClassMetricResult(m(3), [(methodKey1):m(30), (methodKey2):m(30), (methodKey3):m(30)])
    }

    private SingleNumberMetricResult m(int number) {
        return new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, number)
    }

}