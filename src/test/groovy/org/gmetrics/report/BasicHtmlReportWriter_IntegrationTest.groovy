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

import org.apache.tools.ant.Project
import org.apache.tools.ant.types.FileSet
import org.gmetrics.analyzer.AnalysisContext
import org.gmetrics.ant.AntFileSetSourceAnalyzer
import org.gmetrics.metric.abc.AbcMetric
import org.gmetrics.metric.linecount.MethodLineCountMetric
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.test.AbstractTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for BasicHtmlReportWriter
 *
 * @author Chris Mair
 */
class BasicHtmlReportWriter_IntegrationTest extends AbstractTestCase {

    private static final String REPORT_FILE = "$REPORTS_DIR/GMetricsReport.html"
    private static final String BASE_DIR = 'src/main'
    private static final String GROOVY_FILES = '**/*.groovy'

    private sourceAnalyzer
    private reportWriter
    private metricSet
    private analysisContext

    @Test
	void test_RunAnalysis_And_GenerateReport() {
        def resultsNode = sourceAnalyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")
        assertReportContents(resultsNode)
    }

    @BeforeEach
    void setUp() {
        def project = new Project(basedir:BASE_DIR)
        def fileSet = new FileSet(project:project, dir:new File(BASE_DIR), includes:GROOVY_FILES)
        sourceAnalyzer = new AntFileSetSourceAnalyzer(project, [fileSet])

        reportWriter = new BasicHtmlReportWriter(outputFile:REPORT_FILE)
        metricSet = new ListMetricSet([new MethodLineCountMetric(), new AbcMetric()])
        analysisContext = new AnalysisContext(metricSet:metricSet)
    }

    private void assertReportContents(resultsNode) {
        def file = new File(REPORT_FILE)
        file.delete()
        reportWriter.writeReport(resultsNode, analysisContext)
        assert file.exists()
        def reportText = file.text
        assertContainsAll(reportText, ['org.gmetrics'])
        assertContainsAll(reportText, metricSet.metrics*.name)
    }

}