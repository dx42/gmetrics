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
import org.gmetrics.report.BasicHtmlReportWriter
import org.gmetrics.analyzer.SourceAnalyzer

/**
 * Ant Task for GMetrics. It uses the set of <code>Metric</code>s defined by <code>DefaultMetricSet</code>.
 * <p/>
 * At least one nested <code>fileset</code> element is required, and is used to specify the source files
 * to be analyzed. This is the standard Ant <i>FileSet</i>, and is quite powerful and flexible.
 * See the <i>Apache Ant Manual</i> for more information on <i>FileSets</i>.
 * <p/>
 * The <ode>report</code> nested element defines the format and output file for the analysis report.
 * Currently, HTML ("html") is the only supported format. It includes a <code>type</code> attribute and
 * can contain nested <code>option</code> elements --  each must include a <code>name</code> and
 * <code>value</code> attribue.
 *
 * @see "http://ant.apache.org/manual/index.html"
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

        def sourceAnalyzer = createSourceAnalyzer()
        def gMetricsRunner = createGMetricsRunner()
        gMetricsRunner.metricSet = createMetricSet()
        gMetricsRunner.reportWriters = reportWriters
        gMetricsRunner.sourceAnalyzer = sourceAnalyzer
        gMetricsRunner.execute()
    }

    /**
     * Ant-defined method (by convention), called with each instance of a nested <fileset>
     * element within this task.
     */
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
        def reportWriter = new BasicHtmlReportWriter()
        report.options.each { name, value -> reportWriter[name] = value }
        LOG.debug("Adding report: $reportWriter")
        reportWriters << reportWriter
    }

    private MetricSet createMetricSet() {
        return new DefaultMetricSet()
    }

    private SourceAnalyzer createSourceAnalyzer() {
        return new AntFileSetSourceAnalyzer(getProject(), fileSets)
    }

}