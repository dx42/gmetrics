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
package org.gmetrics.report

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.resultsnode.StubResultsNode
import org.gmetrics.resultsnode.ResultsNode
import org.gmetrics.metricset.MetricSet

/**
 * Tests for AbstractReportWriter
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbstractReportWriterTest extends AbstractTestCase {
    private static final RESULTS_NODE = new StubResultsNode()
    private static final METRIC_SET = [:] as MetricSet
    private static final DEFAULT_STRING = '?'
    private static final DEFAULT_OUTPUT_FILE = '?'
    private reportWriter

    void testWriteReport_WritesToDefaultOutputFile_IfOutputFileIsNull() {
        def defaultOutputFile = TestAbstractReportWriter.defaultOutputFile
        reportWriter.writeReport(RESULTS_NODE, METRIC_SET)
        assertOutputFile(defaultOutputFile) 
    }

    void testWriteReport_WritesToOutputFile_IfOutputFileIsDefined() {
        final NAME = 'abc.txt'
        reportWriter.outputFile = NAME
        reportWriter.writeReport(RESULTS_NODE, METRIC_SET)
        assertOutputFile(NAME) 
    }

    void testInitializeResourceBundle_CustomMessagesFileExists() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('basicHtmlReport.titlePrefix')   // in "gmetrics-base-messages.properties"
        assert reportWriter.getResourceBundleString('abc')                      // in "gmetrics-messages.properties"
    }

    void testInitializeResourceBundle_CustomMessagesFileDoesNotExist() {
        reportWriter.customMessagesBundleName = 'DoesNotExist'
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('basicHtmlReport.titlePrefix')   // in "gmetrics-base-messages.properties"
        assert reportWriter.getResourceBundleString('abc') == DEFAULT_STRING 
    }

    void testGetResourceBundleString() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('abc') == '123'
    }

    void testGetResourceBundleString_ReturnsDefaultStringIfKeyNotFound() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('DoesNotExist') == DEFAULT_STRING
    }

    void testGetGMetricsVersion() {
        assert reportWriter.getGMetricsVersion() == new File('src/main/resources/gmetrics-version.txt').text
    }

    void setUp() {
        super.setUp()
        reportWriter = new TestAbstractReportWriter()
    }

    private void assertOutputFile(String outputFile) {
        def file = new File(outputFile)
        assert file.exists(), "The output file [$outputFile] does not exist"
        def contents = file.text
        file.delete()
        assert contents == 'abc'
    }
}

/**
 * Concrete subclass of AbstractReportWriter for testing
 */
protected class TestAbstractReportWriter extends AbstractReportWriter {
    static defaultOutputFile = 'TestReportWriter.txt'

    void writeReport(Writer writer, ResultsNode resultsNode, MetricSet metricSet) {
        writer.withWriter { w -> w.write('abc') }
    }
}