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

/**
 * Tests for BasicHtmlReportWriter
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class BasicHtmlReportWriter_IntegrationTest extends AbstractTestCase {
    private static REPORT_FILE = 'GMetricsReport.html'
    private static final BASE_DIR = 'src/main'
    private sourceAnalyzer
    private reportWriter
    private metricSet

    void test_RunAnalysis_And_GenerateReport() {
        def resultsNode = sourceAnalyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")
        assertReportContents(resultsNode, [], true) 
    }

    void setUp() {
        super.setUp()
        sourceAnalyzer = new FilesystemSourceAnalyzer(baseDirectory:BASE_DIR)
        reportWriter = new BasicHtmlReportWriter()
        metricSet = new ListMetricSet([new MethodLineCountMetric(), new AbcMetric()])    
    }

    private void assertReportContents(resultsNode, expectedContents, boolean writeToFile=false) {
        def file = new File(REPORT_FILE)
        file.delete()
        reportWriter.writeReport(resultsNode, metricSet)
        assert file.exists()
        def reportText = file.text
        assertContainsAll(reportText, ['org.gmetrics'])
        assertContainsAll(reportText, metricSet.metrics*.name)
    }

}