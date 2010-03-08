/*
* Copyright 2010 the original author or authors.
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
package org.gmetrics.report

import org.gmetrics.resultsnode.StubResultsNode
import org.gmetrics.analyzer.AnalysisContext

/**
 * Tests for BasicHtmlReportWriter
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class BasicHtmlReportWriterTest extends AbstractReportWriterTestCase {

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

    static reportFilename = "GMetricsReport.html" 
    private localizedMessages

    void testThatDefaultOutputFile_IsGmetricsReportHtml() {
        assert reportWriter.defaultOutputFile == 'GMetricsReport.html'
    }

    void testWriteReport_SingleResultsNodeWithSingleMetric() {
        final CONTENTS = [
                HTML_TAG,
                METRIC_RESULTS,
                ALL_PACKAGES, 10, 10,
                METRIC_DESCRIPTIONS,
                metric1.name, metricDescription(metric1),
                BOTTOM_LINK]
        analysisContext = new AnalysisContext(metricSet:metricSet1)
        def resultsNode = new StubResultsNode(metricResults:[metric1Result(10)])
        assertReportContents(resultsNode, CONTENTS)
    }

    void testWriteReport_SingleResultsNodeWithTwoMetrics() {
        final CONTENTS = [
                HTML_TAG,
                METRIC_RESULTS,
                ALL_PACKAGES, 10, 10, 20, 20,
                METRIC_DESCRIPTIONS,
                metric1.name, metricDescription(metric1),
                metric2.name, metricDescription(metric2),
                BOTTOM_LINK]
        def resultsNode = new StubResultsNode(metricResults:[metric1Result(10), metric2Result(20)])
        assertReportContents(resultsNode, CONTENTS)
    }

    void testWriteReport_SingleResultsNode_TwoMetrics_OneMetricDisabled() {
        metric2.enabled = false
        def resultsNode = new StubResultsNode(metricResults:[metric1Result(10), metric2Result(20)])
        assertReportDoesNotContain(resultsNode, [metric2.name, metricDescription(metric2)])
    }

    void testWriteReport_ChildPackageResultsNodesWithTwoMetrics() {
        final CONTENTS = [
                HTML_TAG,
                METRIC_RESULTS,
                ALL_PACKAGES, 10, 10, 20, 20,
                'Dir1', 11, 11, 21, 21,
                METRIC_DESCRIPTIONS,
                metric1.name, metricDescription(metric1),
                metric2.name, metricDescription(metric2),
                BOTTOM_LINK]
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
                PACKAGE_PREFIX, 'src/DirA', 11, 11,
                CLASS_PREFIX, 'ClassA1', 100, 100,
                METHOD_PREFIX, 'MethodA1a', 1000, 1000,
                METHOD_PREFIX, 'MethodA1b', 1001, 1001,
                CLASS_PREFIX, 'ClassA2', 101, 101,
                PACKAGE_PREFIX, 'src/DirA/DirC', 102, 102,
                PACKAGE_PREFIX, 'src/DirB', 12, 12,
                METRIC_DESCRIPTIONS,
                metric1.name, metricDescription(metric1),
                BOTTOM_LINK]

        def resultsNode = packageResultsNode(metricResults:[metric1Result(1)])
        def dirA = packageResultsNode(path:'src/DirA', metricResults:[metric1Result(11)])
        def dirB = packageResultsNode(path:'src/DirB', metricResults:[metric1Result(12)])
        resultsNode.children['DirA'] = dirA
        resultsNode.children['DirB'] = dirB
        def classA1 = classResultsNode(metricResults:[metric1Result(100)])
        def methodA1a = methodResultsNode(metricResults:[metric1Result(1000)])
        def methodA1b = methodResultsNode(metricResults:[metric1Result(1001)])
        classA1.children['MethodA1a'] = methodA1a
        classA1.children['MethodA1b'] = methodA1b
        def classA2 = classResultsNode(metricResults:[metric1Result(101)])
        def dirC = packageResultsNode(path:'src/DirA/DirC', metricResults:[metric1Result(102)])
        dirA.children['DirC'] = dirC
        dirA.children['ClassA1'] = classA1
        dirA.children['ClassA2'] = classA2

        assertReportContents(resultsNode, CONTENTS, true)
    }

    void testWriteReport_MissingMetricResultsForMetric() {
        final CONTENTS = [
                HTML_TAG,
                ALL_PACKAGES, 10, 10, NA, NA,
                metric1.name, metricDescription(metric1),
                metric2.name, metricDescription(metric2),
                BOTTOM_LINK]
        def resultsNode = new StubResultsNode(metricResults:[metric1Result(10)])
        assertReportContents(resultsNode, CONTENTS)
    }

    void setUp() {
        super.setUp()

        localizedMessages = [
            'basicHtmlReport.titlePrefix': TITLE_PREFIX,
            'basicHtmlReport.reportTimestamp.label':REPORT_TIMESTAMP,
            'basicHtmlReport.metricResults.title':METRIC_RESULTS,
            'basicHtmlReport.metricDescriptions.title':METRIC_DESCRIPTIONS,
            'basicHtmlReport.metricResults.nameHeading':'Package/Class/Method',
            'basicHtmlReport.metricResults.notApplicable':NA,
            'basicHtmlReport.metricDescriptions.nameHeading':'Metric Name',
            'basicHtmlReport.metricDescriptions.descriptionHeading':'Description',    
            'Metric1.description.html':metricDescription(metric1),
            'Metric1.total':'Metric1.total',
            'Metric1.average':'Metric1.average',
            'Metric2.description.html':metricDescription(metric2),
            'Metric2.total':'M2.total',
            'Metric2.average':'M2.average',
        ]
        reportWriter.initializeResourceBundle = { reportWriter.resourceBundle = [getString:{key -> localizedMessages[key] ?: 'NOT FOUND'}] }
    }

    protected ReportWriter createReportWriter() {
        return new BasicHtmlReportWriter()
    }
}