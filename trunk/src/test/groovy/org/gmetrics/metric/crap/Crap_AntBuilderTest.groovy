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
package org.gmetrics.metric.crap

import org.gmetrics.test.AbstractTestCase

/**
 * Tests for CrapMetric that use the Groovy AntBuilder.
 *
 * @author Chris Mair
 */
class Crap_AntBuilderTest extends AbstractTestCase {

    private static final HTML_REPORT_WRITER = 'org.gmetrics.report.BasicHtmlReportWriter'
    private static final HTML_REPORT_FILE = 'CRAP_AntBuilderTest.html'

    private ant

    void testAntTask_AgainstProjectSourceCode() {
        ant.gmetrics(metricSetFile: 'crap/CrapMetricSet.txt') {
            fileset(dir:'src/main/groovy') {
                include(name:"**/*.groovy")
            }
           report(type:HTML_REPORT_WRITER){
               option(name:'title', value:'Crap_AntBuilderTest')
               option(name:'outputFile', value:HTML_REPORT_FILE)
           }
        }
    }

    void setUp() {
        super.setUp()
        ant = new AntBuilder()
        ant.taskdef(name:'gmetrics', classname:'org.gmetrics.ant.GMetricsTask')
    }

}