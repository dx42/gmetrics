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
package org.gmetrics.ant

import org.apache.tools.ant.BuildException
import org.apache.tools.ant.Project
import org.apache.tools.ant.types.FileSet
import org.gmetrics.metricset.DefaultMetricSet
import org.gmetrics.metricset.MetricSetTestFiles
import org.gmetrics.report.BasicHtmlReportWriter
import org.gmetrics.test.AbstractTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for GMetricsTask
 *
 * @author Chris Mair
 */
class GMetricsTaskTest extends AbstractTestCase {

    private static final String HTML_REPORT_WRITER = 'org.gmetrics.report.BasicHtmlReportWriter'

    private GMetricsTask gMetricsTask
    private fileSet
    private project
    private called = [:]

    @BeforeEach
    void setUp() {
        project = new Project(basedir:'.')
        fileSet = new FileSet(dir:new File('.'), project:project)
        gMetricsTask = new GMetricsTask(project:project)
    }

    @Test
	void testExecute_NullFileSet() {
        shouldFailWithMessageContaining('fileSet') { gMetricsTask.execute() }
    }

    @Test
	void testExecute_CreatesConfiguresAndExecutesGMetricsRunner() {
        gMetricsTask.addFileset(fileSet)
        gMetricsTask.addConfiguredReport(createReport(HTML_REPORT_WRITER))
        def gMetricsRunner = [execute:{ -> called.execute = true }]
        gMetricsTask.createGMetricsRunner = { gMetricsRunner }

        gMetricsTask.execute()

        assert called.execute
        assert gMetricsRunner.metricSet instanceof DefaultMetricSet
        assert gMetricsRunner.sourceAnalyzer instanceof AntFileSetSourceAnalyzer
        assert gMetricsRunner.reportWriters.size() == 1
        assert gMetricsRunner.reportWriters[0] instanceof BasicHtmlReportWriter
    }

    @Test
	void testExecute_UsesConfiguredMetricSetFile() {
        gMetricsTask.metricSetFile = MetricSetTestFiles.METRICSET1
        gMetricsTask.addFileset(fileSet)
        def gMetricsRunner = [execute:{ -> called.execute = true }]
        gMetricsTask.createGMetricsRunner = { gMetricsRunner }
        gMetricsTask.execute()
        log(gMetricsRunner.metricSet.metrics)
        assert gMetricsRunner.metricSet.metrics*.name == ['Stub', 'XXX']
    }

    @Test
	void testExecute_MetricSetFileNotFound_ThrowsException() {
        gMetricsTask.metricSetFile = 'DoesNotExist.groovy'
        gMetricsTask.addFileset(fileSet)
        shouldFailWithMessageContaining('DoesNotExist.groovy') { gMetricsTask.execute() }
    }

    @Test
	void testAddConfiguredReport_AddsToReportWriters() {
        gMetricsTask.addConfiguredReport(createReport(HTML_REPORT_WRITER))
        assert gMetricsTask.reportWriters.size() == 1
        assert gMetricsTask.reportWriters[0] instanceof BasicHtmlReportWriter
    }

    @Test
	void testAddConfiguredReport_Twice_AddsToReportWriters() {
        gMetricsTask.addConfiguredReport(createReport(HTML_REPORT_WRITER, [title:'abc']))
        gMetricsTask.addConfiguredReport(createReport(HTML_REPORT_WRITER, [title:'def']))
        assert gMetricsTask.reportWriters[0] instanceof BasicHtmlReportWriter
        assert gMetricsTask.reportWriters[1] instanceof BasicHtmlReportWriter
        assert gMetricsTask.reportWriters.title == ['abc', 'def']
    }

    @Test
	void testAddConfiguredReport_ReportOptionsSetPropertiesOnReportWriter() {
        def report = createReport(HTML_REPORT_WRITER, [title:'abc', outputFile:'def'])
        gMetricsTask.addConfiguredReport(report)
        assert gMetricsTask.reportWriters.title == ['abc']
        assert gMetricsTask.reportWriters.outputFile == ['def']
    }

    @Test
	void testAddConfiguredReport_ThrowsExceptionForInvalidReportType() {
        shouldFail(ClassNotFoundException) { gMetricsTask.addConfiguredReport(new Report(type:'XXX')) }
    }

    @Test
	void testAddConfiguredReport_ThrowsExceptionForMissingReportType() {
        shouldFail(BuildException) { gMetricsTask.addConfiguredReport(new Report()) }
    }

    @Test
	void testAddFileSet_ThrowsExceptionIfFileSetIsNull() {
        shouldFailWithMessageContaining('fileSet') { gMetricsTask.addFileset(null) }
    }

    @Test
	void testAddFileSet_AddsToFileSets() {
        gMetricsTask.addFileset(fileSet)
        assert gMetricsTask.fileSets == [fileSet]

        def fileSet2 = new FileSet()
        gMetricsTask.addFileset(fileSet2)
        assert gMetricsTask.fileSets == [fileSet, fileSet2]
    }

    private Report createReport(String type, Map options=null) {
        def report = new Report(type:type)
        options?.each { name, value ->
            report.addConfiguredOption(new ReportOption(name:name, value:value))
        }
        return report
    }

}