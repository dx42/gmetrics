/*
 * Copyright 2017 the original author or authors.
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

/**
 * Java application to create the GMetrics sample reports
 *
 * @author Chris Mair
 */
class CreateSampleReports {

    private static final String REPORTS_DIR = "docs/reports"
    private static final BASE_DIR = 'src/main'
    private static final GROOVY_FILES = '**/*.groovy'

    private analysisContext
    private resultsNode

    static main(args) {
        new CreateSampleReports().writeReports()
    }

    void writeReports() {
        initialize()
        writeSampleBasicHtmlReport()
        writeSampleSingleSeriesHtmlReport()
        writeSampleXmlReport()
    }
    
    private void writeSampleBasicHtmlReport() {
        final REPORT_FILE = "$REPORTS_DIR/SampleGMetricsReport.html"
        def reportWriter = new BasicHtmlReportWriter(outputFile:REPORT_FILE)
        reportWriter.writeReport(resultsNode, analysisContext)
    }
    
    private void writeSampleSingleSeriesHtmlReport() {
        final REPORT_FILE = "$REPORTS_DIR/SampleGMetricsSingleSeriesReport.html"
        def reportWriter = new SingleSeriesHtmlReportWriter(outputFile:REPORT_FILE)
        reportWriter.metric = 'ABC'
        reportWriter.level = 'class'
        reportWriter.function = 'average'
        reportWriter.writeReport(resultsNode, analysisContext)
    }
    
    private void writeSampleXmlReport() {
        final REPORT_FILE = "$REPORTS_DIR/SampleGMetricsXmlReport.xml"
        def reportWriter = new XmlReportWriter(outputFile:REPORT_FILE)
        reportWriter.writeReport(resultsNode, analysisContext)
    }
    
    private void initialize() {
        def project = new Project(basedir:BASE_DIR)
        def fileSet = new FileSet(project:project, dir:new File(BASE_DIR), includes:GROOVY_FILES)
        def sourceAnalyzer = new AntFileSetSourceAnalyzer(project, [fileSet])

        def metricSet = new ListMetricSet([new MethodLineCountMetric(), new AbcMetric()])
        analysisContext = new AnalysisContext(metricSet:metricSet)
        resultsNode = sourceAnalyzer.analyze(metricSet)
    }
    
}
