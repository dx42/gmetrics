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
    private static final HTML_REPORT_FILE = 'AntBuilderTestReport.html'
    private static final HTML_TEMP_REPORT_FILE = 'AntBuilderTestReport_Temp.html'

    private static final SERIES_HTML_REPORT_WRITER = 'org.gmetrics.report.SingleSeriesHtmlReportWriter'
    private static final SERIES_HTML_REPORT_FILE = 'AntBuilderTestSingleSeriesHtmlReport.html'
    private static final SERIES_TITLE = 'Methods With Highest Line Count'

    private static final XML_REPORT_WRITER = 'org.gmetrics.report.XmlReportWriter'
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
               option(name:'metrics', value:'CyclomaticComplexity, MethodLineCount')
           }
           report(type:XML_REPORT_WRITER){
               option(name:'title', value:TITLE)
               option(name:'outputFile', value:XML_REPORT_FILE)
               option(name:'metrics', value:'ClassLineCount, MethodLineCount')
               option(name:'levels', value:'MethodLineCount= method,class')
               option(name:'functions', value:'MethodLineCount = total')
           }
            report(type:SERIES_HTML_REPORT_WRITER){
                option(name:'title', value:SERIES_TITLE)
                option(name:'outputFile', value:SERIES_HTML_REPORT_FILE)
                option(name:'metric', value:'MethodLineCount')
                option(name:'level', value:'method')
                option(name:'function', value:'total')
                option(name:'sort', value:'descending')
                option(name:'maxResults', value:'20')
            }
        }
        def defaultMetricNames = new DefaultMetricSet().metrics*.name
        def htmlMetricNames = (defaultMetricNames - 'ClassLineCount').sort()
        verifyReportFile(HTML_REPORT_FILE, [TITLE, 'org/gmetrics', 'Description'] + htmlMetricNames)

        def xmlMetricNames = (defaultMetricNames - 'CyclomaticComplexity').sort()
        verifyReportFile(XML_REPORT_FILE, [TITLE, 'org/gmetrics', 'Description'] + xmlMetricNames)

        verifyReportFile(SERIES_HTML_REPORT_FILE, [SERIES_TITLE, 'Method'])
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
    
    private void verifyReportFile(String reportFile, List strings) {
        def file = new File(reportFile)
        assert file.exists()
        assertContainsAllInOrder(file.text, strings)
    }
}