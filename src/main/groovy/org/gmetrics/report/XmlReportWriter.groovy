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

import org.gmetrics.metricset.MetricSet
import org.gmetrics.resultsnode.ResultsNode
import groovy.xml.StreamingMarkupBuilder
import org.gmetrics.metric.Metric
import org.gmetrics.result.MetricResult
import org.gmetrics.metric.MetricLevel
import org.gmetrics.analyzer.AnalysisContext

/**
 * ReportWriter that generates an XML report. The XML includes
 * and the metric descriptions for each Metric within the passed-in MetricSet.
 *
 * @author Chris Mair
 * @version $Revision: 88 $ - $Date: 2010-02-27 15:11:55 -0500 (Sat, 27 Feb 2010) $
 */
class XmlReportWriter extends AbstractReportWriter {
    public static final DEFAULT_OUTPUT_FILE = 'GMetricsXmlReport.xml'

    static defaultOutputFile = DEFAULT_OUTPUT_FILE

    void writeReport(Writer writer, ResultsNode resultsNode, AnalysisContext analysisContext) {
        assert resultsNode
        assert analysisContext
        assert analysisContext.metricSet
        assert writer

        initializeResourceBundle()
        def builder = new StreamingMarkupBuilder()
        def xml = builder.bind {
            mkp.xmlDeclaration()
            GMetrics(url:GMETRICS_URL, version:getGMetricsVersion()) {
                out << buildReportElement()
//                out << buildProjectElement(analysisContext)
                out << buildPackageElements(resultsNode)
                out << buildMetricsElement(analysisContext.metricSet)
            }
        }
        writer << xml
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private buildReportElement() {
        return {
            Report(timestamp:getFormattedTimestamp())
        }
    }

//    private buildProjectElement(AnalysisContext analysisContext) {
//        return {
//            Project(title:title) {
//                analysisContext.sourceDirectories.each { sourceDirectory ->
//                    SourceDirectory(sourceDirectory)
//                }
//            }
//        }
//    }
//
    private buildPackageElements(resultsNode) {
        return buildElement(resultsNode, null)
    }

    private buildElement(ResultsNode resultsNode, String name) {
        switch(resultsNode.level) {
            case MetricLevel.PACKAGE: return buildPackageElement(resultsNode, name)
            case MetricLevel.CLASS: return buildChildElement('Class', resultsNode, name)
            case MetricLevel.METHOD: return buildChildElement('Method', resultsNode, name)
        }
    }

//    private buildPackageSummaryElement(resultsNode) {
//        return {
//            'PackageSummary' {
//                out << buildMetricElements(resultsNode.metricResults)
//            }
//        }
//    }

    private buildPackageElement(resultsNode, String name) {
        def elementName = isRoot(resultsNode) ? 'PackageSummary' : 'Package'
        def attributeMap = isRoot(resultsNode) ? [:] : [path:resultsNode.path]
        return {
            "$elementName"(attributeMap) {
                out << buildMetricElements(resultsNode.metricResults)
                resultsNode.children.each { childName, childResultsNode ->
                    if (!isPackage(childResultsNode)) {
                        out << buildElement(childResultsNode, childName)
                    }
                }

            }
            resultsNode.children.each { childName, childResultsNode ->
                if (isPackage(childResultsNode)) {
                    out << buildElement(childResultsNode, childName)
                }
            }
        }
    }

    // Build element for Class or Method
    private buildChildElement(String typeName, resultsNode, String name) {
        return {
            "$typeName"([name:name]) {
                out << buildMetricElements(resultsNode.metricResults)
                resultsNode.children.each { childName, childResultsNode ->
                    out << buildElement(childResultsNode, childName)
                }
            }
        }
    }

    private buildMetricElements(metricResults) {
        return {
            metricResults.each { metricResult ->
                out << buildMetricElement(metricResult)
            }
        }
    }

    private buildMetricElement(MetricResult metricResult) {
        def metric = metricResult.getMetric()
        return {
            MetricResult(name: metric.name, total:metricResult.total, average:metricResult.average)
        }
    }

    private boolean isRoot(results) {
        results.path == null
    }

    private buildMetricsElement(MetricSet metricSet) {
        def metrics = metricSet.metrics
        def sortedMetrics = metrics.toList().sort { metric -> metric.name }
        return {
            Metrics() {
                sortedMetrics.each { metric ->
                    def description = getDescriptionForMetric(metric)
                    Metric(name:metric.name) {
                        Description(cdata(description))
                    }
                }
            }
        }
    }

    protected String getDescriptionForMetric(Metric metric) {
        def resourceKey = metric.name + '.description'
        return getResourceBundleString(resourceKey, "No description provided for metric named [$metric.name]")
    }

    private boolean isPackage(resultsNode) {
        return resultsNode.level == MetricLevel.PACKAGE
    }

    private cdata(String text) {
        return { unescaped << "<![CDATA[" + text + "]]>" }
    }
}
