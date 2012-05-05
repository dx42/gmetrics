/*
 * Copyright 2012 the original author or authors.
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
 package org.gmetrics.metric.coupling

/**
 * Java application that generates a report for the coupling metrics against the GMetrics source code
 *
 * @author Chris Mair
 */
class CouplingReportTestMain {

    private static final TITLE = 'Coupling Report'
    private static final HTML_REPORT_FILE = 'CouplingTestReport.html'
    private static final HTML_REPORT_WRITER = 'org.gmetrics.report.BasicHtmlReportWriter'
    private static final XML_REPORT_FILE = 'CouplingTestReport.xml'
    private static final XML_REPORT_WRITER = 'org.gmetrics.report.XmlReportWriter'

    static void main(String[] args) {

        def ant = new AntBuilder()
        ant.taskdef(name:'gmetrics', classname:'org.gmetrics.ant.GMetricsTask')

        ant.gmetrics(metricSetFile: 'file:src/test/resources/metricsets/CouplingMetricSet.txt') {
           fileset(dir:'src/main/groovy') {
               include(name:"**/*.groovy")
           }
            fileset(dir:'src/test/groovy') {
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

    }

}
