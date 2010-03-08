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
package org.gmetrics.report

import org.gmetrics.resultsnode.ResultsNode
import org.gmetrics.util.io.ClassPathResource
import groovy.xml.StreamingMarkupBuilder
import org.gmetrics.metricset.MetricSet
import org.gmetrics.metric.MetricLevel
import org.gmetrics.analyzer.AnalysisContext

/**
 * ReportWriter that generates a basic HTML report. The HTML includes a table containing
 * a row for each package, class and method, and the metric values for each Metric
 * within the passed-in MetricSet.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class BasicHtmlReportWriter extends AbstractReportWriter {

    public static final DEFAULT_OUTPUT_FILE = 'GMetricsReport.html'

    private static final CSS_FILE = 'gmetrics-basic-html-report.css'
    private static final ROOT_PACKAGE_NAME = 'All packages'
    private static final MAX_INDENT_LEVEL = 10

    static defaultOutputFile = DEFAULT_OUTPUT_FILE
    String title

    void writeReport(Writer writer, ResultsNode resultsNode, AnalysisContext analysisContext) {
        assert resultsNode
        assert analysisContext
        assert analysisContext.metricSet
        assert writer

        initializeResourceBundle()
        def metricResultColumns = buildMetricResultColumns(analysisContext.metricSet)

        def builder = new StreamingMarkupBuilder()
        def html = builder.bind() {
            html {
                out << buildHeaderSection()
                out << buildBodySection(resultsNode, metricResultColumns, analysisContext)
            }
        }
        writer << html
        LOG.info("Report created")
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private def buildMetricResultColumns(MetricSet metricSet) {
        def metricResultColumns = []
        metricSet.getMetrics().each {metric ->
            metricResultColumns << [metric: metric, property: 'total']
            metricResultColumns << [metric: metric, property: 'average']
        }
        return metricResultColumns
    }

    private buildCSS() {
        return {
            def cssInputStream = ClassPathResource.getInputStream(CSS_FILE)
            assert cssInputStream, "CSS File [$CSS_FILE] not found"
            def css = cssInputStream.text
            unescaped << css
        }
    }

    private buildHeaderSection() {
        return {
            head {
                title(buildTitle())
                out << buildCSS()
            }
        }
    }

    private buildBodySection(ResultsNode resultsNode, List metricResultColumns, AnalysisContext analysisContext) {
        return {
            body {
                h1(buildTitle())
                out << buildReportTimestamp()
                out << buildResultsTable(resultsNode, metricResultColumns)
                out << buildMetricDescriptions(analysisContext.metricSet)
                out << buildVersionFooter()
            }
        }
    }

    private buildReportTimestamp() {
        return {
            def timestamp = getFormattedTimestamp()
            p(getResourceBundleString('basicHtmlReport.reportTimestamp.label') + " $timestamp", class:'reportInfo')
        }
    }

    private buildResultsTable(ResultsNode resultsNode, List metricResultColumns) {
        return {
            h2(getResourceBundleString('basicHtmlReport.metricResults.title'))
            table() {
                tr(class:'tableHeader') {
                    th(getResourceBundleString('basicHtmlReport.metricResults.nameHeading'))
                    metricResultColumns.each { columnDef ->
                        def columnHeading = getMetricResultColumnHeading(columnDef.metric.name, columnDef.property)
                        th(columnHeading, class:'metricColumnHeader')
                    }
                }
                out << buildResultsTableRowRecursively(resultsNode, metricResultColumns, null)
            }
        }
    }

    private String getMetricResultColumnHeading(String metricName, String metricProperty) {
        def resourceKey = metricName + '.' + metricProperty
        return getResourceBundleString(resourceKey, "$metricName ($metricProperty)")
    }

    private buildResultsTableRowRecursively(ResultsNode resultsNode, List metricResultColumns, String name) {
        return {
            def level = resultsNode.level
            def rowCssClass = level.name
            tr(class:rowCssClass) {
                def prefix = prefixForResultsNodeLevel(resultsNode)
                def nodeName = level == MetricLevel.PACKAGE ? resultsNode.path : name
                def pathName = nodeName ?: ROOT_PACKAGE_NAME
                def cssClass = name ? 'name' : 'allPackages'

                td(class:"${level.name}Indent") {
                    span(prefix, class:'rowTypePrefix')
                    span(pathName, class:cssClass)
                }

                metricResultColumns.each { columnDef ->
                    def metric = columnDef.metric
                    def metricResult = resultsNode.getMetricResult(metric)
                    def value = metricResult ?
                        metricResult[columnDef.property] :
                        getResourceBundleString('basicHtmlReport.metricResults.notApplicable')
                    td(value, class:'metricValue')
                }
            }
            out << buildResultsRowForLevel(resultsNode, metricResultColumns, MetricLevel.METHOD)
            out << buildResultsRowForLevel(resultsNode, metricResultColumns, MetricLevel.CLASS)
            out << buildResultsRowForLevel(resultsNode, metricResultColumns, MetricLevel.PACKAGE)
        }
    }

    private buildResultsRowForLevel(ResultsNode resultsNode, List metricResultColumns, MetricLevel metricLevel) {
        return {
            resultsNode.children.each { childName, childNode ->
                if (childNode.level == metricLevel) {
                    out << buildResultsTableRowRecursively(childNode, metricResultColumns, childName)
                }
            }
        }
    }

    private String prefixForResultsNodeLevel(ResultsNode resultsNode) {
        def prefixes = [
            (MetricLevel.PACKAGE):'[p] ',
            (MetricLevel.CLASS):'[c] ',
            (MetricLevel.METHOD):'[m] '
        ]
        return prefixes[resultsNode.level]
    }

    private buildMetricDescriptions(MetricSet metricSet) {
        def metrics = new ArrayList(metricSet.metrics).findAll { metric -> metric.enabled }
        def sortedMetrics = metrics.sort { metric -> metric.name }

        return {
            h2(getResourceBundleString('basicHtmlReport.metricDescriptions.title'))
            table(border:'1') {
                tr(class:'tableHeader') {
                    th('#', class:'metricDescriptions')
                    th(getResourceBundleString('basicHtmlReport.metricDescriptions.nameHeading'), class:'metricDescriptions')
                    th(getResourceBundleString('basicHtmlReport.metricDescriptions.descriptionHeading'), class:'metricDescriptions')
                }

                sortedMetrics.eachWithIndex { metric, index ->
                    tr(class:'metricDescriptions') {
                        a((metric.name):metric.name)
                        td(index+1)
                        td(metric.name, class:'metricName')
                        td { unescaped << getDescriptionForMetricName(metric.name) }
                    }
                }
            }
        }
    }

    protected String getDescriptionForMetricName(String metricName) {
        def resourceKey = metricName + '.description.html'
        return getResourceBundleString(resourceKey, "No description provided for metric named [$metricName]")
    }

    private buildVersionFooter() {
        def versionText = getGMetricsVersion()
        return {
            p(class:'version') {
                a(versionText, href:GMETRICS_URL)
            }
        }
    }

    private String buildTitle() {
        return getResourceBundleString('basicHtmlReport.titlePrefix') + (title ? ": $title": '')
    }

}