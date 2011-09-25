/*
 * Copyright 2011 the original author or authors.
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
 package org.gmetrics.tool

/**
 * Runs GMetrics against the Grails source code.
 *
 * You must set the "grails.home" system property to the Grails installation directory, containing the Grails source.
 *
 * @author Chris Mair
 */
class RunGMetricsAgainstGrails {

    private static final HTML_REPORT_WRITER = 'org.gmetrics.report.BasicHtmlReportWriter'
    private static final METRICSET_FILE = 'RunGMetricsAgainstGrails.metricset'

    static void main(String[] args) {
        runGMetrics()
    }

    private static void runGMetrics() {
        def baseDir = System.getProperty('grails.home')
        assert baseDir, 'The "grails.home" system property must be set to the location of the Grails installation.'

        def ant = new AntBuilder()

        ant.taskdef(name:'gmetrics', classname:'org.gmetrics.ant.GMetricsTask')

        ant.gmetrics(metricSetFile:METRICSET_FILE) {

            fileset(dir:baseDir) {
                include(name:'src/**/*.groovy')
                include(name:'scripts/**/*.groovy')
                exclude(name:'**/templates/**')
            }

            report(type:HTML_REPORT_WRITER){
               option(name:'title', value:'Grails')
               option(name:'outputFile', value:'GMetrics-Grails-Report.html')
            }
        }
    }

}
