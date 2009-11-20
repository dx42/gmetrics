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
package org.gmetrics.ant

import org.gmetrics.test.AbstractTestCase
import org.apache.tools.ant.Project
import org.apache.tools.ant.types.FileSet
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.report.HtmlReportWriter
import org.apache.tools.ant.BuildException

/**
 * Tests for GMetricsTask
 *
 * @author Chris Mair
 * @version $Revision: 219 $ - $Date: 2009-09-07 21:48:47 -0400 (Mon, 07 Sep 2009) $
 */
class GMetricsTaskTest extends AbstractTestCase {
    private static final HTML = 'html'
    private static final BASE_DIR = 'src/test/resources'
    private static final REPORT_FILE = 'GMetricsTaskHtmlReport.html'
//    private static final RESULTS = new FileResults('path', [])

    private gMetricsTask
    private metricSet
    private fileSet
    private project

    void setUp() {
        super.setUp()

        project = new Project(basedir:'.')
        fileSet = new FileSet(dir:new File(BASE_DIR), project:project)
        fileSet.setIncludes('sourcewithdirs/**/*.groovy')

        metricSet = new ListMetricSet([])
        gMetricsTask = new GMetricsTask(project:project)
//        gMetricsTask.ruleSetFiles = RULESET_FILE
    }

    void testExecute_NullFileSet() {
        shouldFailWithMessageContaining('fileSet') { gMetricsTask.execute() }
    }

//    void testExecute_SingleRuleSetFile() {
//        def codeNarcRunner = createAndUseFakeCodeNarcRunner()
//
//        gMetricsTask.addFileset(fileSet)
//        gMetricsTask.execute()
//
//        assert codeNarcRunner.sourceAnalyzer.class == AntFileSetSourceAnalyzer
//        assert codeNarcRunner.ruleSetFiles == RULESET_FILE
//        assertStandardHtmlReportWriter(codeNarcRunner)
//    }
//
//    void testExecute_TwoRuleSetFiles() {
//        def codeNarcRunner = createAndUseFakeCodeNarcRunner()
//
//        gMetricsTask.ruleSetFiles = RULESET_FILES
//        gMetricsTask.addFileset(fileSet)
//        gMetricsTask.execute()
//
//        assert codeNarcRunner.sourceAnalyzer.class == AntFileSetSourceAnalyzer
//        assert codeNarcRunner.ruleSetFiles == RULESET_FILES
//        assertStandardHtmlReportWriter(codeNarcRunner)
//    }
//
//    void testExecute_TwoFileSets() {
//        def codeNarcRunner = createAndUseFakeCodeNarcRunner()
//        def fileSet2 = new FileSet(dir:new File('/abc'), project:project)
//
//        gMetricsTask.addFileset(fileSet)
//        gMetricsTask.addFileset(fileSet2)
//        gMetricsTask.execute()
//
//        assert codeNarcRunner.sourceAnalyzer.class == AntFileSetSourceAnalyzer
//        assert codeNarcRunner.sourceAnalyzer.fileSets == [fileSet, fileSet2]
//        assert codeNarcRunner.ruleSetFiles == RULESET_FILE
//        assertStandardHtmlReportWriter(codeNarcRunner)
//    }

    void testAddConfiguredReport_AddsToReportWriters() {
        gMetricsTask.addConfiguredReport(createReport(HTML))
        assert gMetricsTask.reportWriters*.class == [HtmlReportWriter]
    }

    void testAddConfiguredReport_Twice_AddsToReportWriters() {
        gMetricsTask.addConfiguredReport(createReport(HTML, [title:'abc']))
        gMetricsTask.addConfiguredReport(createReport(HTML, [title:'def']))
        assert gMetricsTask.reportWriters*.class == [HtmlReportWriter, HtmlReportWriter]
        assert gMetricsTask.reportWriters.title == ['abc', 'def']
    }

    void testAddConfiguredReport_ReportOptionsSetPropertiesOnReportWriter() {
        def report = createReport(HTML, [title:'abc', outputFile:'def'])
        gMetricsTask.addConfiguredReport(report)
        assert gMetricsTask.reportWriters.title == ['abc']
        assert gMetricsTask.reportWriters.outputFile == ['def']
    }

    void testAddConfiguredReport_InvalidReportType() {
        shouldFail(BuildException) { gMetricsTask.addConfiguredReport(new Report(type:'XXX')) }
    }

    void testAddFileSet_ThrowsExceptionIfFileSetIsNull() {
        shouldFailWithMessageContaining('fileSet') { gMetricsTask.addFileset(null) }
    }

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

//    private createAndUseFakeGMetricsRunner() {
//        def gMetricsRunner = [execute: { return RESULTS }]
//        gMetricsTask.createGMetricsRunner = { return gMetricsRunner }
//        return gMetricsRunner
//    }
//
//    private void assertStandardHtmlReportWriter(codeNarcRunner) {
//        assert codeNarcRunner.reportWriters.size == 1
//        def reportWriter = codeNarcRunner.reportWriters[0]
//        assert reportWriter.class == HtmlReportWriter
//        assert reportWriter.outputFile == REPORT_FILE
//    }
}