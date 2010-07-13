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

import org.gmetrics.result.NumberMetricResult
import org.gmetrics.resultsnode.StubResultsNode
import org.gmetrics.test.AbstractTestCase
import org.gmetrics.analyzer.AnalysisContext
import org.gmetrics.metric.StubMetric
import org.gmetrics.metricset.ListMetricSet

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

    protected static final VERSION_FILE = 'src/main/resources/gmetrics-version.txt'
    protected static final VERSION = new File(VERSION_FILE).text

    protected static final SRC_DIR1 = 'c:/MyProject/src/main/groovy'
    protected static final SRC_DIR2 = 'c:/MyProject/src/test/groovy'

    protected reportWriter
    protected writer
    protected metric1, metric2, metric3
    protected metricSet1, metricSet2, metricSet3
    protected analysisContext

    // Each subclass must implement
    protected abstract ReportWriter createReportWriter()

    void testImplementsReportWriter() {
        assert reportWriter instanceof ReportWriter
    }

    void testWriteReport_NullResultsNode_ThrowsException() {
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
        shouldFailWithMessageContaining('writer') { reportWriter.writeReport(null, resultsNode, analysisContext) }
    }

    void setUp() {
        super.setUp()
        reportWriter = createReportWriter()
        writer = new StringWriter()
        metric1 = new StubMetric(name:'Metric1')
        metric2 = new StubMetric(name:'Metric2')
        metric3 = new StubMetric(name:'Metric3')
        metricSet1 = new ListMetricSet([metric1])
        metricSet2 = new ListMetricSet([metric1, metric2])
        metricSet3 = new ListMetricSet([metric1, metric2, metric3])
        analysisContext = new AnalysisContext(sourceDirectories:[SRC_DIR1, SRC_DIR2], metricSet:metricSet2)
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------
    
    protected static String getVersion() {
        return new File(VERSION_FILE).text
    }

    protected void assertReportContents(resultsNode, expectedContents, boolean writeToFile=false) {
        def reportText = writeReport(resultsNode)
        writeOutToFile(reportText, writeToFile)
        assertContainsAllInOrder(reportText, expectedContents)
    }

    protected void assertReportContents(resultsNode, expectedContents, List notExpectedContents, boolean writeToFile=false) {
        def reportText = writeReport(resultsNode)
        writeOutToFile(reportText, writeToFile)
        assertContainsAllInOrder(reportText, expectedContents)
        notExpectedContents.each { text ->
            assert !reportText.contains(text.toString()), "[$text] was present in the report"
        }
    }

    protected void assertReportDoesNotContain(resultsNode, List notExpected) {
        def reportText = writeReport(resultsNode)
        notExpected.each { text ->
            assert !reportText.contains(text.toString()), "[$text] was present in the report"
        }
    }

    protected String writeReport(resultsNode, boolean writeToFile=false) {
        reportWriter.writeReport(writer, resultsNode, analysisContext)
        def reportText = writer.toString()
        log("reportText=$reportText")
        writeOutToFile(reportText, writeToFile)
        return reportText
    }

    protected String metricDescription(metric) {
        "Description for " + metric.name
    }

    protected metric1Result(value) {
        new NumberMetricResult(metric1, value)
    }

    protected metric2Result(value) {
        new NumberMetricResult(metric2, value)
    }

    protected metric3Result(value) {
        new NumberMetricResult(metric3, value)
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