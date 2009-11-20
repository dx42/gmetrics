package org.gmetrics.report

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.StubMetric
import org.gmetrics.result.NumberMetricResult
import org.gmetrics.resultsnode.StubResultsNode
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.metric.MetricLevel

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

/**
 * Tests for HtmlReportWriter
 *
 * @author Chris Mair
 * @version $Revision: 60 $ - $Date: 2009-02-22 14:46:41 -0500 (Sun, 22 Feb 2009) $
 */
class HtmlReportWriterTest extends AbstractTestCase {

    private static final METRIC1 = new StubMetric(name:'Metric1')
    private static final METRIC2 = new StubMetric(name:'Metric2')

    private static final HTML_TAG = 'html'
    private static final TITLE_PREFIX = 'GMetrics Report'
    private static final BOTTOM_LINK = "<a href='http://www.gmetrics.org'>GMetrics"
    private static final TITLE = 'My Cool Project'
    private static final REPORT_TIMESTAMP = 'Report timestamp:'
    private static final METRIC_RESULTS = 'Metric Results'
    private static final METRIC_DESCRIPTIONS = 'Metric Descriptions'
    private static final NAME_HEADING = 'Package/Class/Method'
    private static final ALL_PACKAGES = 'All packages'
    private static final NA = 'N/A'
    private static final PACKAGE_PREFIX = '[p]'
    private static final CLASS_PREFIX = '[c]'
    private static final METHOD_PREFIX = '[m]'

    private reportWriter
    private writer
    private metricSet
    private localizedMessages

    void testWriteReport_SingleResultsNodeWithSingleMetric() {
        final CONTENTS = [
                HTML_TAG,
                METRIC_RESULTS,
                ALL_PACKAGES, 10, 10,
                METRIC_DESCRIPTIONS,
                METRIC1.name, metricDescription(METRIC1),
                BOTTOM_LINK]
        def resultsNode = new StubResultsNode(metricResults:[metric1Result(10)])
        assertReportContents(resultsNode, CONTENTS)
    }

    void testWriteReport_SingleResultsNodeWithTwoMetrics() {
        final CONTENTS = [
                HTML_TAG,
                METRIC_RESULTS,
                ALL_PACKAGES, 20, 20, 10, 10,
                METRIC_DESCRIPTIONS,
                METRIC1.name, metricDescription(METRIC1),
                METRIC2.name, metricDescription(METRIC2),
                BOTTOM_LINK]
        metricSet = new ListMetricSet([METRIC2, METRIC1])
        def resultsNode = new StubResultsNode(metricResults:[metric1Result(10), metric2Result(20)])
        assertReportContents(resultsNode, CONTENTS)
    }

    void testWriteReport_ChildPackageResultsNodesWithTwoMetrics() {
        final CONTENTS = [
                HTML_TAG,
                METRIC_RESULTS,
                ALL_PACKAGES, 10, 10, 20, 20,
                'Dir1', 11, 11, 21, 21,
                METRIC_DESCRIPTIONS,
                METRIC1.name, metricDescription(METRIC1),
                METRIC2.name, metricDescription(METRIC2),
                BOTTOM_LINK]
        metricSet = new ListMetricSet([METRIC1, METRIC2])
        def resultsNode = new StubResultsNode(metricResults:[metric1Result(10), metric2Result(20)])
        def childResultsNode = new StubResultsNode(metricResults:[metric1Result(11), metric2Result(21)])
        resultsNode.children['Dir1'] = childResultsNode
        assertReportContents(resultsNode, CONTENTS, true)
    }

    void testWriteReport_NestedChildPackageResultsNodes() {
        final CONTENTS = [
                HTML_TAG,
                TITLE_PREFIX, REPORT_TIMESTAMP,
                METRIC_RESULTS,
                NAME_HEADING,
                PACKAGE_PREFIX, ALL_PACKAGES, 1, 1,
                PACKAGE_PREFIX, 'DirA', 11, 11,
                CLASS_PREFIX, 'ClassA1', 100, 100,
                METHOD_PREFIX, 'MethodA1a', 1000, 1000,
                METHOD_PREFIX, 'MethodA1b', 1001, 1001,
                CLASS_PREFIX, 'ClassA2', 101, 101,
                PACKAGE_PREFIX, 'DirC', 102, 102,
                PACKAGE_PREFIX, 'DirB', 12, 12,
                METRIC_DESCRIPTIONS,
                METRIC1.name, metricDescription(METRIC1),
                BOTTOM_LINK]

        def resultsNode = packageResultsNode(metricResults:[metric1Result(1)])
        def dirA = packageResultsNode(metricResults:[metric1Result(11)])
        def dirB = packageResultsNode(metricResults:[metric1Result(12)])
        resultsNode.children['DirA'] = dirA
        resultsNode.children['DirB'] = dirB
        def classA1 = classResultsNode(metricResults:[metric1Result(100)])
        def methodA1a = methodResultsNode(metricResults:[metric1Result(1000)])
        def methodA1b = methodResultsNode(metricResults:[metric1Result(1001)])
        classA1.children['MethodA1a'] = methodA1a
        classA1.children['MethodA1b'] = methodA1b
        def classA2 = classResultsNode(metricResults:[metric1Result(101)])
        def dirC = packageResultsNode(metricResults:[metric1Result(102)])
        dirA.children['ClassA1'] = classA1
        dirA.children['ClassA2'] = classA2
        dirA.children['DirC'] = dirC

        assertReportContents(resultsNode, CONTENTS, true)
    }

    void testWriteReport_MissingMetricResultsForMetric() {
        final CONTENTS = [
                HTML_TAG,
                ALL_PACKAGES, 10, 10, NA, NA,
                METRIC1.name, metricDescription(METRIC1),
                METRIC2.name, metricDescription(METRIC2),
                BOTTOM_LINK]
        metricSet = new ListMetricSet([METRIC1, METRIC2])
        def resultsNode = new StubResultsNode(metricResults:[metric1Result(10)])
        assertReportContents(resultsNode, CONTENTS)
    }

    void testWriteReport_NullResultsNodeThrowsException() {
        shouldFailWithMessageContaining('resultsNode') { reportWriter.writeReport(null, metricSet, writer) }  
    }

    void testWriteReport_NullMetricSetThrowsException() {
        def resultsNode = new StubResultsNode()
        shouldFailWithMessageContaining('metricSet') { reportWriter.writeReport(resultsNode, null, writer) }  
    }

    void testWriteReport_NullWriterThrowsException() {
        def resultsNode = new StubResultsNode()
        shouldFailWithMessageContaining('writer') { reportWriter.writeReport(resultsNode, metricSet, null) }  
    }

    void setUp() {
        super.setUp()
        reportWriter = new HtmlReportWriter()
        writer = new StringWriter()
        metricSet = new ListMetricSet([METRIC1])

        localizedMessages = [
            'htmlReport.titlePrefix': TITLE_PREFIX,
            'htmlReport.reportTimestamp.label':REPORT_TIMESTAMP,
            'htmlReport.metricResults.title':METRIC_RESULTS,
            'htmlReport.metricDescriptions.title':METRIC_DESCRIPTIONS,
            'htmlReport.metricResults.nameHeading':'Package/Class/Method',
            'htmlReport.metricResults.notApplicable':NA,
            'htmlReport.metricDescriptions.nameHeading':'Metric Name',
            'htmlReport.metricDescriptions.descriptionHeading':'Description',    
            'Metric1.description.html':metricDescription(METRIC1),
            'Metric1.totalValue':'Metric1.totalValue',
            'Metric1.averageValue':'Metric1.averageValue',
            'Metric2.description.html':metricDescription(METRIC2)
        ]
        reportWriter.initializeResourceBundle = { reportWriter.resourceBundle = [getString:{key -> localizedMessages[key] ?: 'NOT FOUND'}] }
    }

    private void assertReportContents(resultsNode, expectedContents, boolean writeToFile=false) {
        reportWriter.writeReport(resultsNode, metricSet, writer)
        def reportText = writer.toString()
        log("reportText=$reportText")
        writeOutToFile(reportText, writeToFile)
        assertContainsAllInOrder(reportText, expectedContents)
    }

    private String metricDescription(metric) {
        metric.toString()
    }

    private metric1Result(int value) {
        new NumberMetricResult(METRIC1, value)
    }

    private metric2Result(int value) {
        new NumberMetricResult(METRIC2, value)
    }

    private packageResultsNode(map) {
        def resultsNode = new StubResultsNode(map)
        resultsNode.level = MetricLevel.PACKAGE
        return resultsNode
    }

    private classResultsNode(map) {
        def resultsNode = new StubResultsNode(map)
        resultsNode.level = MetricLevel.CLASS
        return resultsNode
    }

    private methodResultsNode(map) {
        def resultsNode = new StubResultsNode(map)
        resultsNode.level = MetricLevel.METHOD
        return resultsNode
    }

    private void writeOutToFile(String text, boolean writeToFile) {
        if (writeToFile) {
            new File("GMetricsReport.html").withWriter { writer ->
                writer << text
            }
        }
    }
}