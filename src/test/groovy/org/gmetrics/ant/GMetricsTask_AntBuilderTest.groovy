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
import org.gmetrics.metricset.MetricSetTestFiles
import org.gmetrics.metricset.DefaultMetricSet

/**
 * Tests for GMetricsTask that use the Groovy AntBuilder.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class GMetricsTask_AntBuilderTest extends AbstractTestCase {

    private static final HTML_REPORT_WRITER = 'org.gmetrics.report.BasicHtmlReportWriter'
    private static final XML_REPORT_WRITER = 'org.gmetrics.report.XmlReportWriter'
    private static final HTML_REPORT_FILE = 'AntBuilderTestReport.html'
    private static final HTML_TEMP_REPORT_FILE = 'AntBuilderTestReport_Temp.html'
    private static final XML_REPORT_FILE = 'AntBuilderTestXmlReport.xml'
    private static final TITLE = 'Sample'
    private ant

    void testAntTask_Execute_UsingDefaultMetricSet() {
        ant.gmetrics() {
           fileset(dir:'src/main/groovy') {
               include(name:"**/*.groovy")
           }
           report(type:HTML_REPORT_WRITER){
               option(name:'title', value:TITLE)
               option(name:'outputFile', value:HTML_REPORT_FILE)
           }
           report(type:XML_REPORT_WRITER){
               option(name:'title', value:TITLE)
               option(name:'outputFile', value:XML_REPORT_FILE)
           }
        }
        def metricNames = new DefaultMetricSet().metrics*.name 
        verifyReportFile(HTML_REPORT_FILE, metricNames.sort())
    }

    void testAntTask_Execute_SpecifyMetricSetFile() {
        ant.gmetrics(metricSetFile: MetricSetTestFiles.METRICSET1) {
           fileset(dir:'src/main/groovy') {
               include(name:"**/GMetricsRunner.groovy")
           }
           report(type:HTML_REPORT_WRITER){
               option(name:'title', value:TITLE)
               option(name:'outputFile', value:HTML_TEMP_REPORT_FILE)
           }
        }
        verifyReportFile(HTML_TEMP_REPORT_FILE, ['Stub','XXX'])
    }

    void setUp() {
        super.setUp()
        ant = new AntBuilder()
        ant.taskdef(name:'gmetrics', classname:'org.gmetrics.ant.GMetricsTask')
    }

    void tearDown() {
        super.tearDown()
        new File(HTML_TEMP_REPORT_FILE).delete()
    }
    
    private void verifyReportFile(String reportFile, List metricNames) {
        def file = new File(reportFile)
        assert file.exists()
        assertContainsAllInOrder(file.text, [TITLE, 'org/gmetrics', 'Metric Descriptions'] + metricNames)
    }
}