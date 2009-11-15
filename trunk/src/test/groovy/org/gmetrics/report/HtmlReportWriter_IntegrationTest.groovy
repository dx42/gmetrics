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
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.analyzer.FilesystemSourceAnalyzer
import org.gmetrics.metric.linecount.MethodLineCountMetric
import org.gmetrics.metric.abc.AbcMetric

class HtmlReportWriter_IntegrationTest extends AbstractTestCase {
    private static final BASE_DIR = 'src'
    private sourceAnalyzer
    private reportWriter
    private writer
    private metricSet

    void test_RunAnalysis_And_GenerateReport() {
        def resultsNode = sourceAnalyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")
        assertReportContents(resultsNode, [], true) 
    }

    void setUp() {
        super.setUp()
        sourceAnalyzer = new FilesystemSourceAnalyzer(baseDirectory:BASE_DIR)
        reportWriter = new HtmlReportWriter()
        writer = new StringWriter()
        metricSet = new ListMetricSet([new MethodLineCountMetric(), new AbcMetric()])    
    }

    private void assertReportContents(resultsNode, expectedContents, boolean writeToFile=false) {
        reportWriter.writeReport(resultsNode, metricSet, writer)
        def reportText = writer.toString()
        log("reportText=$reportText")
        writeOutToFile(reportText, writeToFile)
//        assertContainsAllInOrder(reportText, expectedContents)
    }

    private void writeOutToFile(String text, boolean writeToFile) {
        if (writeToFile) {
            new File("GMetricsReport.html").withWriter { writer ->
                writer << text
            }
        }
    }
}