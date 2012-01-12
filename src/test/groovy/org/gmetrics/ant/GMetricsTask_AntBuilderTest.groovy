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

import org.gmetrics.metricregistry.MetricRegistryHolder
import org.gmetrics.metricset.DefaultMetricSet
import org.gmetrics.metricset.MetricSetTestFiles
import org.gmetrics.test.AbstractTestCase

/**
 * Tests for GMetricsTask that use the Groovy AntBuilder.
 *
 * @author Chris Mair
 */
class GMetricsTask_AntBuilderTest extends AbstractTestCase {

    private static final HTML_REPORT_WRITER = 'org.gmetrics.report.BasicHtmlReportWriter'
    private static final HTML_REPORT_FILE = 'AntBuilderTestReport.html'
    private static final HTML_TEMP_REPORT_FILE = 'AntBuilderTestReport_Temp.html'

    private static final ALL_HTML_REPORT_FILE = 'AllMetricsAntBuilderTestReport.html'
    private static final ALL_XML_REPORT_FILE = 'AllMetricsAntBuilderTestReport.xml'
    private static final ALL_METRICSET_FILE = 'AllMetricSet.txt'

    private static final SERIES_HTML_REPORT_WRITER = 'org.gmetrics.report.SingleSeriesHtmlReportWriter'
    private static final SERIES_HTML_METHOD_REPORT_FILE = 'AntBuilderTestSingleSeriesMethodHtmlReport.html'
    private static final SERIES_HTML_PACKAGE_REPORT_FILE = 'AntBuilderTestSingleSeriesPackageHtmlReport.html'
    private static final SERIES_TITLE = 'Methods With Highest Line Count'

    private static final XML_REPORT_WRITER = 'org.gmetrics.report.XmlReportWriter'
    private static final XML_REPORT_FILE = 'AntBuilderTestXmlReport.xml'
    private static final TITLE = 'Sample'
    private ant

    void testAntTask_Execute_UsingDefaultMetricSet() {
        ant.gmetrics {
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
                option(name:'outputFile', value:SERIES_HTML_PACKAGE_REPORT_FILE)
                option(name:'metric', value:'MethodLineCount')
                option(name:'level', value:'package')
                option(name:'function', value:'total')
                option(name:'sort', value:'descending')
                option(name:'maxResults', value:'20')
            }
            report(type:SERIES_HTML_REPORT_WRITER){
                option(name:'title', value:SERIES_TITLE)
                option(name:'outputFile', value:SERIES_HTML_METHOD_REPORT_FILE)
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

        verifyReportFile(SERIES_HTML_PACKAGE_REPORT_FILE, [SERIES_TITLE, 'Method'])
        verifyReportFile(SERIES_HTML_METHOD_REPORT_FILE, [SERIES_TITLE, 'Method'])
    }

    void testAntTask_Execute_AllMetrics() {
        def allMetricSetFile = new File(ALL_METRICSET_FILE)
        generateAllMetricsFile(allMetricSetFile)

        ant.gmetrics(metricSetFile: 'file:' + ALL_METRICSET_FILE) {
           fileset(dir:'src/main/groovy/org/gmetrics/metric') {
               include(name:"**/*.groovy")
           }
           report(type:HTML_REPORT_WRITER){
               option(name:'title', value:TITLE)
               option(name:'outputFile', value:ALL_HTML_REPORT_FILE)
           }
           report(type:XML_REPORT_WRITER){
               option(name:'title', value:TITLE)
               option(name:'outputFile', value:ALL_XML_REPORT_FILE)
           }
        }
        verifyReportFile(ALL_HTML_REPORT_FILE, [TITLE, 'org.gmetrics.metric', 'Description'])
        allMetricSetFile.delete()
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

    private void generateAllMetricsFile(File allMetricSetFile) {
        final COBERTURA_FILE = '"coverage/GMetrics/coverage.xml"'
        def specialProperties = [
            CoberturaBranchCoverage: "(coberturaFile: $COBERTURA_FILE)",
            CoberturaLineCoverage: "(coberturaFile: $COBERTURA_FILE)",
            CRAP: "{ coverageMetric = CoberturaLineCoverage(coberturaFile: $COBERTURA_FILE)}"
        ]

        allMetricSetFile.withWriter { w ->
            w.println 'metricset {'
            MetricRegistryHolder.metricRegistry.allMetricNames.each { metricName ->
                def props = specialProperties[metricName] ?: ''
                w.println metricName + props
            }
            w.println '}'
        }
    }
}