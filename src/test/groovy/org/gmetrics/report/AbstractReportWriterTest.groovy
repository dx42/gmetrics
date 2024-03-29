/*
* Copyright 2011 the original author or authors.
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

import org.gmetrics.analyzer.AnalysisContext
import org.gmetrics.metric.StubMetric
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.resultsnode.ResultsNode
import org.gmetrics.resultsnode.StubResultsNode
import org.gmetrics.test.AbstractTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for AbstractReportWriter
 *
 * @author Chris Mair
 */
class AbstractReportWriterTest extends AbstractTestCase {

    private static final RESULTS_NODE = new StubResultsNode()
    private static final METRIC = new StubMetric(name:METRIC_NAME)
    private static final METRIC_SET = new ListMetricSet([METRIC])
    private static final METRIC_NAME = 'MyCustomMetric'
    private static final ANALYSIS_CONTEXT = new AnalysisContext(metricSet:METRIC_SET)
    private static final DEFAULT_STRING = '?'
    private static final CUSTOM_FILENAME = 'abc.txt'
    private ReportWriter reportWriter

    @Test
	void testWriteReport_WritesToDefaultOutputFile_IfOutputFileIsNull() {
        def defaultOutputFile = SampleAbstractReportWriter.defaultOutputFile
        reportWriter.writeReport(RESULTS_NODE, ANALYSIS_CONTEXT)
        assertOutputFile(defaultOutputFile) 
    }

    @Test
	void testWriteReport_WritesToOutputFile_IfOutputFileIsDefined() {
        final NAME = 'abc.txt'
        reportWriter.outputFile = NAME
        reportWriter.writeReport(RESULTS_NODE, ANALYSIS_CONTEXT)
        assertOutputFile(NAME) 
    }

    @Test
	void testWriteReport_WritesToStandardOut_IfWriteToStandardOutIsTrue_String() {
        reportWriter.outputFile = CUSTOM_FILENAME
        reportWriter.writeToStandardOut = "true"
        def output = captureSystemOut {
            reportWriter.writeReport(RESULTS_NODE, ANALYSIS_CONTEXT)
        }
        assertFileDoesNotExist(CUSTOM_FILENAME)
        assertContents(output)
    }

    @Test
	void testWriteReport_WritesToStandardOut_IfWriteToStandardOutIsTrue() {
        reportWriter.outputFile = CUSTOM_FILENAME
        reportWriter.writeToStandardOut = true
        def output = captureSystemOut {
            reportWriter.writeReport(RESULTS_NODE, ANALYSIS_CONTEXT)
        }
        assertFileDoesNotExist(CUSTOM_FILENAME)
        assertContents(output)
    }

    @Test
	void testWriteReport_WritesToStandardOut_AndResetsSystemOut() {
        def originalSystemOut = System.out
        reportWriter.writeToStandardOut = true
        reportWriter.writeReport(RESULTS_NODE, ANALYSIS_CONTEXT)
        assert System.out == originalSystemOut
    }

    @Test
	void testWriteReport_WritesToOutputFile_IfWriteToStandardOutIsNotTrue() {
        reportWriter.outputFile = CUSTOM_FILENAME
        reportWriter.writeToStandardOut = "false"
        reportWriter.writeReport(RESULTS_NODE, ANALYSIS_CONTEXT)
        assertOutputFile(CUSTOM_FILENAME)
    }

    @Test
	void testWriteReport_InitializesFormatters() {
        def localizedMessages = [ (METRIC_NAME+'.formatter'): 'org.gmetrics.formatter.PercentageFormatter' ]
        reportWriter.initializeResourceBundle = { reportWriter.resourceBundle = [getString:{ key -> localizedMessages[key] }] }
        reportWriter.writeToStandardOut = true
        reportWriter.writeReport(RESULTS_NODE, ANALYSIS_CONTEXT)
        assert reportWriter.formatters[METRIC.name]
    }

    // Tests for initializeResourceBundle()

    @Test
	void testInitializeResourceBundle_CustomMessagesFileExists() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('basicHtmlReport.titlePrefix', null)   // in "gmetrics-base-messages.properties"
        assert reportWriter.getResourceBundleString('abc', null)                      // in "gmetrics-messages.properties"
    }

    @Test
	void testInitializeResourceBundle_CustomMessagesFileDoesNotExist() {
        reportWriter.customMessagesBundleName = 'DoesNotExist'
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('basicHtmlReport.titlePrefix')   // in "gmetrics-base-messages.properties"
        assert reportWriter.getResourceBundleString('abc') == DEFAULT_STRING 
    }

    // Tests for getResourceBundleString()

    @Test
	void testGetResourceBundleString() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('abc') == '123'
    }

    @Test
	void testGetResourceBundleString_ReturnsDefaultStringIfKeyNotFound() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('DoesNotExist') == DEFAULT_STRING
    }

    // Tests for getResourceBundleStringOrNull()

    @Test
	void testGetResourceBundleStringOrNull_ReturnsMatchingEntryFromResourceBundle() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleStringOrNull('abc') == '123'
    }

    @Test
	void testGetResourceBundleStringOrNull_ReturnsNullForNoMatchingEntryFromResourceBundle() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleStringOrNull('DoesNotExist') == null
    }

    // Tests for formatMetricResultValue()

    @Test
	void testFormatMetricResultValue_NoFormatterConfiguredForMetric_ReturnsValueToString() {
        assert reportWriter.formatMetricResultValue(METRIC.name, 0.65) == '0.65'
    }

    @Test
	void testFormatMetricResultValue_FormatterConfiguredForMetric_FormatsValue() {
        def localizedMessages = [ (METRIC_NAME+'.formatter'): 'org.gmetrics.formatter.PercentageFormatter' ]
        reportWriter.resourceBundle = [getString:{key -> localizedMessages[key]}]
        reportWriter.initializeFormatters(METRIC_SET)

        assert reportWriter.formatMetricResultValue(METRIC.name, 0.65) == '65%'
    }

    // Tests for getFormattedTimestamp()

    @Test
	void testGetFormattedTimestamp() {
        def timestamp = new Date(1262361072497)
        reportWriter.getTimestamp = { timestamp }
        def expected = java.text.DateFormat.getDateTimeInstance().format(timestamp)
        assert reportWriter.getFormattedTimestamp() == expected
    }

    //------------------------------------------------------------------------------------
    // Setup and helper methods
    //------------------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        reportWriter = new SampleAbstractReportWriter()
    }

    private void assertOutputFile(String outputFile) {
        def file = new File(outputFile)
        assert file.exists(), "The output file [$outputFile] does not exist"
        def contents = file.text
        file.delete()
        assertContents(contents)
    }

    private assertContents(String contents) {
        assert contents == 'abc'
    }

    private void assertFileDoesNotExist(String filename) {
        assert new File(filename).exists() == false
    }
}

/**
 * Concrete subclass of AbstractReportWriter for testing
 */
class SampleAbstractReportWriter extends AbstractReportWriter {
    static defaultOutputFile = 'TestReportWriter.txt'

    @SuppressWarnings('UnusedMethodParameter')
    void writeReport(Writer writer, ResultsNode resultsNode, AnalysisContext analysisContext) {
        writer.write('abc')
        writer.flush()        
    }
}