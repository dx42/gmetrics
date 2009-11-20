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

import org.apache.tools.ant.Task
import org.apache.log4j.Logger
import org.gmetrics.metricset.MetricSet
import org.apache.tools.ant.BuildException
import org.apache.tools.ant.types.FileSet
import org.gmetrics.metricset.DefaultMetricSet
import org.gmetrics.GMetricsRunner
import org.gmetrics.report.HtmlReportWriter

/**
 * Ant Task for GMetrics
 *
 * @author Chris Mair
 * @version $Revision: 219 $ - $Date: 2009-09-07 21:48:47 -0400 (Mon, 07 Sep 2009) $
 */
class GMetricsTask extends Task {
    private static final LOG = Logger.getLogger(GMetricsTaskTest)

    protected MetricSet metricSet = new DefaultMetricSet()

    protected List reportWriters = []
    protected List fileSets = []

    // Abstract creation of the GMetricsNarcRunner instance to allow substitution of test spy for unit tests
    protected createGMetricsRunner = { return new GMetricsRunner() }

    /**
     * Execute this Ant Task
     */
    void execute() throws BuildException {
        assert metricSet
        assert fileSets

//        def sourceAnalyzer = createSourceAnalyzer()
//        def gMetricsRunner = createCodeNarcRunner()
//        gMetricsRunner.ruleSetFiles = ruleSetFiles
//        gMetricsRunner.reportWriters = reportWriters
//        gMetricsRunner.sourceAnalyzer = sourceAnalyzer
//
//        def results = gMetricsRunner.execute()
    }

    void addFileset(FileSet fileSet) {
        assert fileSet
        this.fileSets << fileSet
    }

    /**
     * Ant-defined method (by convention), called with each instance of a nested <report>
     * element within this task.
     */
    void addConfiguredReport(Report report) {
        if (report.type != 'html') {
            throw new BuildException("Invalid type: [$report.type]")
        }
        def reportWriter = new HtmlReportWriter()
        report.options.each { name, value -> reportWriter[name] = value }
        LOG.debug("Adding report: $reportWriter")
        reportWriters << reportWriter
    }

    /**
     * Create and return the SourceAnalyzer
     * @return a configured SourceAnalyzer instance
     */
//    protected SourceAnalyzer createSourceAnalyzer() {
//        return new AntFileSetSourceAnalyzer(getProject(), fileSets)
//    }

}