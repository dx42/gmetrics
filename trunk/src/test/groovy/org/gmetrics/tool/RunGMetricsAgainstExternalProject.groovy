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
 package org.gmetrics.tool

/**
 * Runs GMetrics against the another project's source code.
 *
 * You must set the "basedir" system property to the base directory of the external project.
 * You must set the "title" system property to the title of the external project.
 *
 * @author Chris Mair
 */
class RunGMetricsAgainstExternalProject {

    private static final HTML_REPORT_WRITER = 'org.gmetrics.report.BasicHtmlReportWriter'
    private static final XML_REPORT_WRITER = 'org.gmetrics.report.XmlReportWriter'
    private static final METRICSET_FILE = 'RunGMetricsAgainstExternalProject.metricset'

    static void main(String[] args) {
        runGMetrics()
    }

    @SuppressWarnings('Println')
    private static void runGMetrics() {
        def baseDir = System.getProperty('basedir')
        def title = System.getProperty('title')
        assert baseDir, 'The "basedir" system property must be set to the base directory of the external project.'
        assert title, 'The "title" system property must be set to the title of the external project.'
        println "Analyzing Groovy source code from [$baseDir]"

        def ant = new AntBuilder()

        ant.taskdef(name:'gmetrics', classname:'org.gmetrics.ant.GMetricsTask')

        ant.gmetrics(metricSetFile:METRICSET_FILE) {

            fileset(dir:baseDir) {
                include(name:'src/**/*.groovy')
                include(name:'**/*.groovy')
                include(name:'scripts/**/*.groovy')
                exclude(name:'**/templates/**')
            }

            report(type:HTML_REPORT_WRITER){
               option(name:'title', value:title)
               option(name:'outputFile', value:title + '-GMetrics-Report.html')
               option(name:'metrics', value:'CyclomaticComplexity, ClassLineCount, MethodLineCount')
            }

            report(type:XML_REPORT_WRITER){
                option(name:'title', value:title)
                option(name:'outputFile', value:title + '-GMetrics-Report.xml')
                option(name:'metrics', value:'CyclomaticComplexity, ClassLineCount, MethodLineCount')
            }

            report(type:HTML_REPORT_WRITER){
                option(name:'title', value:title + ' - Package')
                option(name:'outputFile', value:title + '-GMetrics-Package-Report.html')
                option(name:'metrics', value:'AfferentCoupling, EfferentCoupling')
                option(name:'reportLevels', value:'package')
            }
        }
    }

}