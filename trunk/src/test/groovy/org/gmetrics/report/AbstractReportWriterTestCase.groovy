package org.gmetrics.report

import org.gmetrics.metric.MetricLevel

import org.gmetrics.result.NumberMetricResult
import org.gmetrics.resultsnode.StubResultsNode
import org.gmetrics.test.AbstractTestCase
import org.gmetrics.analyzer.AnalysisContext
import org.gmetrics.metricset.MetricSet

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

/**
 * Abstract superclass for ReportWriter test classes.
 *
 * Each concrete subclass must implement the <code>createReportWriter()</code>
 * and define a property named "reportFilename".
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbstractReportWriterTestCase extends AbstractTestCase {

    protected reportWriter
    protected writer
    protected metricSet
    protected analysisContext

    // Each subclass must implement
    protected abstract ReportWriter createReportWriter()

    void testImplementsReportWriter() {
        assert reportWriter instanceof ReportWriter
    }

    void testWriteReport_NullResultsNode_ThrowsException() {
        def analysisContext = new AnalysisContext(metricSet:metricSet)
        shouldFailWithMessageContaining('results') { reportWriter.writeReport(null, analysisContext) }
    }

    void testWriteReport_NullAnalysisContext_ThrowsException() {
        def resultsNode = packageResultsNode(path:'test')
        shouldFailWithMessageContaining('analysisContext') { reportWriter.writeReport(resultsNode, null) }
    }

    void testWriteReport_NullMetricSet_ThrowsException() {
        def resultsNode = packageResultsNode(path:'test')
        def analysisContext_NoMetricSet = new AnalysisContext()
        shouldFailWithMessageContaining('metricSet') { reportWriter.writeReport(resultsNode, analysisContext_NoMetricSet) }
    }

    void testWriteReport_NullWriterThrowsException() {
        def resultsNode = new StubResultsNode()
        metricSet = [:] as MetricSet
        def analysisContext = new AnalysisContext(metricSet:metricSet)
        shouldFailWithMessageContaining('writer') { reportWriter.writeReport(null, resultsNode, analysisContext) }
    }

    void setUp() {
        super.setUp()
        reportWriter = createReportWriter()
        writer = new StringWriter()
//        metric1 = new StubMetric(name:'Metric1')
//        metric2 = new StubMetric(name:'Metric2')
//
//        metricSet1 = new ListMetricSet([metric1])
//        metricSet2 = new ListMetricSet([metric1, metric2])
    }

    protected void assertReportContents(resultsNode, expectedContents, boolean writeToFile=false) {
        def reportText = writeReport(resultsNode)
        writeOutToFile(reportText, writeToFile)
        assertContainsAllInOrder(reportText, expectedContents)
    }

    protected void assertReportDoesNotContain(resultsNode, notExpected) {
        def reportText = writeReport(resultsNode)
        notExpected.each { text ->
            assert !reportText.contains(text), "[$text] was present in the report"
        }
    }

    protected String writeReport(resultsNode, boolean writeToFile=false) {
        analysisContext = new AnalysisContext(metricSet:metricSet)
        reportWriter.writeReport(writer, resultsNode, analysisContext)
        def reportText = writer.toString()
        log("reportText=$reportText")
        writeOutToFile(reportText, writeToFile)
        return reportText
    }

//    private String metricDescription(metric) {
//        metric.toString()
//    }

    protected metric1Result(int value) {
        new NumberMetricResult(metric1, value)
    }

    protected metric2Result(int value) {
        new NumberMetricResult(metric2, value)
    }

    protected packageResultsNode(map) {
        def resultsNode = new StubResultsNode(map)
        resultsNode.level = MetricLevel.PACKAGE
        return resultsNode
    }

    protected classResultsNode(map) {
        def resultsNode = new StubResultsNode(map)
        resultsNode.level = MetricLevel.CLASS
        return resultsNode
    }

    protected methodResultsNode(map) {
        def resultsNode = new StubResultsNode(map)
        resultsNode.level = MetricLevel.METHOD
        return resultsNode
    }

    protected void writeOutToFile(String text, boolean writeToFile) {
        if (writeToFile) {
            def filename = getProperty('reportFilename')
            log("Writing report to file: [$filename]")
            new File(filename).withWriter { writer ->
                writer << text
            }
        }
    }
}