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
package org.gmetrics.resultsnode

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.SingleNumberMetricResult
import org.gmetrics.metric.StubMetric
import org.gmetrics.metric.linecount.MethodLineCountMetric

/**
 * Tests for PackageResultsNode
 *
 * @author Chris Mair
 */
class PackageResultsNodeTest extends AbstractTestCase {

    private static final NAME = 'name123'
    private static final PATH = 'path123'
    private static final PACKAGE_NAME = 'org.gmetrics'
    private static final METRIC = new MethodLineCountMetric()
    private static final MR1 = new SingleNumberMetricResult(METRIC, MetricLevel.CLASS, 23)
    private static final MR2 = new SingleNumberMetricResult(METRIC, MetricLevel.CLASS, 99)
    private static final TOTAL = 23 + 99
    private static final AVG = TOTAL / 2

    private packageResultsNode
    private emptyResultsNode, classResultsNode
    private resultsNode1, resultsNode2, packageResultsNode2

    void testImplementsResultsNode() {
        assert packageResultsNode instanceof ResultsNode
    }

    void testName_AssignedFromConstructor() {
        assert packageResultsNode.name == NAME
    }

    void testNameAndPath_AssignedFromConstructor() {
        def newResultsNode = new PackageResultsNode(NAME, PACKAGE_NAME, PATH)
        assert newResultsNode.name == NAME
        assert newResultsNode.path == PATH
    }

    void testNamePackageNameAndPath_AssignedFromConstructor() {
        def newResultsNode = new PackageResultsNode(NAME, PACKAGE_NAME, PATH)
        assert newResultsNode.name == NAME
        assert newResultsNode.packageName == PACKAGE_NAME
        assert newResultsNode.path == PATH
    }

    void testThatMetricLevelIsPackageLevel() {
        assert packageResultsNode.level == MetricLevel.PACKAGE
    }

    void test_InitialMetricValuesIsEmpty() {
        assert packageResultsNode.getMetricResults() == []
    }

    void test_InitialChildrenIsEmpty() {
        assert packageResultsNode.getChildren() == [:]
    }

    void test_addChildIfNotEmpty_NullNameThrowsException() {
        shouldFailWithMessageContaining('name') { packageResultsNode.addChildIfNotEmpty(null, emptyResultsNode) }
    }

    void test_addChildIfNotEmpty_NullChildThrowsException() {
        shouldFailWithMessageContaining('child') { packageResultsNode.addChildIfNotEmpty('a', null) }
    }

    void test_AddingASingleChildWithNoResultMetrics() {
        packageResultsNode.addChildIfNotEmpty('a', emptyResultsNode)
        log(packageResultsNode)

        assert packageResultsNode.getMetricResults() == []
        assert packageResultsNode.getChildren() == [:]
    }

    void test_addChildIfNotEmpty_AddsToChildren() {
        packageResultsNode.addChildIfNotEmpty('a', resultsNode1)
        packageResultsNode.addChildIfNotEmpty('b', emptyResultsNode)
        packageResultsNode.addChildIfNotEmpty('c', resultsNode2)
        log(packageResultsNode)

        def children = packageResultsNode.getChildren()
        assert children.keySet() == ['a', 'c'] as Set
        assert packageResultsNode.metricResults == []
    }

    void test_addChild_NullNameThrowsException() {
        shouldFailWithMessageContaining('name') { packageResultsNode.addChild(null, emptyResultsNode) }
    }

    void test_addChild_NullChildThrowsException() {
        shouldFailWithMessageContaining('child') { packageResultsNode.addChild('a', null) }
    }

    void test_addChild_AddsToChildren() {
        packageResultsNode.addChild('a', resultsNode1)
        packageResultsNode.addChild('b', emptyResultsNode)
        assert packageResultsNode.children.keySet() == ['a', 'b'] as Set
    }

    void test_applyMetric_PreventsAddingAnyMoreChildren() {
        packageResultsNode.addChildIfNotEmpty('a', resultsNode1)
        packageResultsNode.applyMetric(METRIC)
        shouldFail { packageResultsNode.addChildIfNotEmpty('b', resultsNode2) }
    }

    void test_applyMetric_AddsToMetricResults() {
        def metric = new StubMetric()
        packageResultsNode.addChildIfNotEmpty('a', resultsNode1)
        def metricResult = new SingleNumberMetricResult(metric, MetricLevel.CLASS, 23)
        metric.packageMetricResult = metricResult
        packageResultsNode.applyMetric(metric)
        assert packageResultsNode.metricResults == [metricResult]
    }

    void test_applyMetric_AddsPackageResultsForPackageName() {
        def metric = new StubMetric()
        packageResultsNode.applyMetric(metric)
        assert metric.packageName == PATH
    }

    void test_applyMetric_AddsNothingIfMetricReturnsNullForThePackage() {
        def metric = new StubMetric()
        metric.packageMetricResult = null
        packageResultsNode.applyMetric(metric)
        log("packageResultsNode.metricResults=${packageResultsNode.metricResults}")
        assert packageResultsNode.metricResults == []
    }

    void test_applyMetric_AggregatesResultsAcrossChildrenOfSameMetricType() {
        packageResultsNode.addChildIfNotEmpty('a', resultsNode1)
        packageResultsNode.addChildIfNotEmpty('b', emptyResultsNode)
        packageResultsNode.addChildIfNotEmpty('c', resultsNode2)

        packageResultsNode.applyMetric(METRIC)
        log(packageResultsNode)

        assert packageResultsNode.metricResults.size() == 1
        def metricResult = packageResultsNode.metricResults[0]
        assert metricResult.count == 2
        assert metricResult['total'] == TOTAL
        assert metricResult['average'] == AVG
    }

    void test_getMetricResult_NullMetricThrowsException() {
        shouldFailWithMessageContaining('metric') { packageResultsNode.getMetricResult(null) }
    }

    void test_getMetricResult_ReturnsCorrectMetricResult() {
        def metric1 = new StubMetric()
        final METRIC_RESULT = new SingleNumberMetricResult(metric1, MetricLevel.METHOD, 11)
        metric1.packageMetricResult = METRIC_RESULT
        def metric2 = new StubMetric()
        metric2.packageMetricResult = new SingleNumberMetricResult(metric2, MetricLevel.METHOD, 22)
        def metric3 = new StubMetric()
        metric3.packageMetricResult = new SingleNumberMetricResult(metric3, MetricLevel.METHOD, 33)
        packageResultsNode.applyMetric(metric2)
        packageResultsNode.applyMetric(metric1)
        packageResultsNode.applyMetric(metric3)
        assert packageResultsNode.getMetricResult(metric1) == METRIC_RESULT
    }

    void test_getMetricResult_ReturnsNullIfNoMatchingMetricResultIsFound() {
        assert packageResultsNode.getMetricResult(METRIC) == null
    }

    void testContainsClassResults_ReturnsFalseIfHasNoChildren() {
        assertFalse packageResultsNode.containsClassResults()
    }

    void testContainsClassResults_ReturnsFalseIfContainsOnlyChildPackageResults() {
        packageResultsNode.addChildIfNotEmpty('a', packageResultsNode2)
        assertFalse packageResultsNode.containsClassResults()
    }

    void testContainsClassResults_ReturnsTrueIfChildContainsClassResults() {
        packageResultsNode.addChildIfNotEmpty('a', packageResultsNode2)
        packageResultsNode.addChildIfNotEmpty('b', classResultsNode)
        assertTrue packageResultsNode.containsClassResults()
    }

    void setUp() {
        super.setUp()
        packageResultsNode = new PackageResultsNode(NAME, PACKAGE_NAME, PATH)
        emptyResultsNode = new StubResultsNode()
        resultsNode1 = new StubResultsNode(metricResults:[MR1])
        resultsNode2 = new StubResultsNode(metricResults:[MR2])
        classResultsNode = new StubResultsNode(metricResults:[MR1], containsClassResults:true)
        packageResultsNode2 = new PackageResultsNode(NAME, null, null)
    }
}