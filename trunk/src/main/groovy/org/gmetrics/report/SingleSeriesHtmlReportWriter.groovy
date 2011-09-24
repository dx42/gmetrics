/*
 * Copyright 2010 the original author or authors.
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
import org.gmetrics.analyzer.AnalysisContext
import groovy.xml.StreamingMarkupBuilder
import org.gmetrics.util.io.ClassPathResource

/**
 * ReportWriter that generates a HTML report for a single series of metric values. This single
 * series is specified by a single Metric, a level (package, class or method) and a single
 * function (total, average, minimum, maximum).
 * <p/>
 * The <code>metric</code>, <code>level</code> and <code>function</code> properties are required (must
 * be non-null and non-empty). These three properties uniquely identify a single series of metric values.
 * <p/>
 * The <code>metric</code> property must specify the name (case-sensitive) of a single Metric (for example
 * "CyclomaticComplexity") included in the analysis results.
 * <p/>
 * The <code>level</code> property must be set to one of: "package", "class" or "method".
 * <p/>
 * The <code>function</code> property must be set to the name of a function supported by the
 * metric, typically one of: "total", "average", "minimum" or "maximum".
 * <p/>
 * The <code>sort</code> property is optional, and if not <code>null</code> or empty, must either have
 * the value of "ascending" or "descending", and causes the results to be sorted numerically in either ascending
 * or descending order.
 * <p/>
 * The <code>maxResults</code> property is optional. A value of <code>null</code>, empty or <code>0</code>
 * means no limit. Otherwise, the value must be positive, and limits the number of results returned.
 * <p/>
 * The <code>greaterThan</code> property is optional. The value specifies a threshold -- only results
 * with a larger value are returned. A value of <code>null</code> or empty means no threshold.
 * <p/>
 * The <code>lessThan</code> property is optional. The value specifies a threshold -- only results
 * with a smaller value are returned. A value of <code>null</code> or empty means no threshold.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
@Mixin(SingleSeriesCriteriaFilter)
class SingleSeriesHtmlReportWriter extends AbstractReportWriter {

    public static final DEFAULT_OUTPUT_FILE = 'GMetricsSingleSeriesReport.html'

    protected static final DEFAULT_CSS_FILE = 'gmetrics-single-series-html-report.css'
    protected static final DEFAULT_TITLE = "GMetrics Report"

    static defaultOutputFile = DEFAULT_OUTPUT_FILE

    String title = DEFAULT_TITLE
    String subtitle

    void writeReport(Writer writer, ResultsNode resultsNode, AnalysisContext analysisContext) {
        assert resultsNode
        assert analysisContext
        assert analysisContext.metricSet
        assert writer

        initializeResourceBundle()
        def seriesData = buildSeriesData(resultsNode, analysisContext.metricSet)

        def builder = new StreamingMarkupBuilder()
        def html = builder.bind() {
            html {
                out << buildHeaderSection()
                out << buildBodySection(seriesData)
            }
        }
        writer << html
        LOG.info("Report created")

    }

    private buildHeaderSection() {
        return {
            head {
                title(title)
                out << buildCSS()
            }
        }
    }

    private buildCSS() {
        return {
            def cssInputStream = ClassPathResource.getInputStream(DEFAULT_CSS_FILE)
            assert cssInputStream, "CSS File [$DEFAULT_CSS_FILE] not found"
            def css = cssInputStream.text
            unescaped << css
        }
    }

    private buildBodySection(List<SeriesValue> seriesData) {
        return {
            body {
                h1(title)
                if (subtitle) {
                    h2(subtitle)
                }
                out << buildReportTimestamp()
                out << buildResultsTable(seriesData)
                out << buildVersionFooter()
            }
        }
    }

    private buildReportTimestamp() {
        return {
            def timestamp = getFormattedTimestamp()
            p(getResourceBundleString('singleSeriesHtmlReport.reportTimestamp.label') + " $timestamp", class:'timestamp')
        }
    }

    private buildResultsTable(List<SeriesValue> seriesData) {
        return {
            table() {
                tr(class:'tableHeader') {
                    th(getSeriesValueNameHeading())
                    th(getMetricResultColumnHeading(metric, function))
                }
                seriesData.each { seriesValue ->
                    out << buildSeriesValueRow(seriesValue)
                }
            }
        }
    }

    private String getSeriesValueNameHeading() {
        final KEYS = [
            'package':'singleSeriesHtmlReport.packageHeading',
            'class':'singleSeriesHtmlReport.classHeading',
            'method':'singleSeriesHtmlReport.methodHeading'
        ]
        def key = KEYS[level.toLowerCase()]
        return getResourceBundleString(key)
    }

    private buildSeriesValueRow(SeriesValue seriesValue) {
        return {
            tr {
                td(seriesValue.name, class:'seriesDataName')
                td(seriesValue.value, class:'seriesDataValue')
            }
        }
    }

    private String getMetricResultColumnHeading(String metricName, String functionName) {
        def resourceKey = metricName + '.' + functionName
        return getResourceBundleString(resourceKey, "$metricName ($functionName)")
    }

    private buildVersionFooter() {
        def versionText = getGMetricsVersion()
        return {
            p(class:'version') {
                a(versionText, href:GMETRICS_URL)
            }
        }
    }
}
