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
package org.gmetrics.metric.coverage

import groovy.ant.AntBuilder
import org.gmetrics.test.AbstractTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for Cobertura coverage metrics that use the Groovy AntBuilder.
 *
 * @author Chris Mair
 */
class CoberturaCoverage_AntBuilderTest extends AbstractTestCase {

    private static final String HTML_REPORT_WRITER = 'org.gmetrics.report.BasicHtmlReportWriter'
    private static final String HTML_REPORT_FILE = "$REPORTS_DIR/CoberturaCoverage_AntBuilderTest-GMetricsReport.html"

    private static final String XML_REPORT_WRITER = 'org.gmetrics.report.XmlReportWriter'
    private static final String XML_REPORT_FILE = "$REPORTS_DIR/CoberturaCoverage_AntBuilderTest-GMetricsReport.xml"

    private ant

    @Test
	void testAntTask_AgainstProjectSourceCode() {
        ant.gmetrics(metricSetFile: 'coverage/CoberturaMetricSet.txt') {
            fileset(dir:'src/main') {
//            fileset(dir:'src/main/groovy') {
                include(name:"**/*.groovy")
            }
           report(type:HTML_REPORT_WRITER){
               option(name:'title', value:'CoberturaCoverage_AntBuilderTest')
               option(name:'outputFile', value:HTML_REPORT_FILE)
           }
           report(type:XML_REPORT_WRITER){
               option(name:'title', value:'CoberturaCoverage_AntBuilderTest')
               option(name:'outputFile', value:XML_REPORT_FILE)
           }
        }
    }

    @BeforeEach
    void setUp() {
        ant = new AntBuilder()
        ant.taskdef(name:'gmetrics', classname:'org.gmetrics.ant.GMetricsTask')
    }

}