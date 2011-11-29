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
import org.gmetrics.util.GMetricsVersion

/**
 * ReportWriter that generates an XML report. The XML includes
 * and the metric descriptions for each Metric within the passed-in MetricSet.
 *
 * @author Chris Mair
 */
@Mixin(MetricsCriteriaFilter)
@Mixin(LevelsCriteriaFilter)
@Mixin(FunctionsCriteriaFilter)
class XmlReportWriter extends AbstractReportWriter {

    public static final DEFAULT_OUTPUT_FILE = 'GMetricsXmlReport.xml'
    static defaultOutputFile = DEFAULT_OUTPUT_FILE
    String title

    void writeReport(Writer writer, ResultsNode resultsNode, AnalysisContext analysisContext) {
        assert resultsNode
        assert analysisContext
        assert analysisContext.metricSet
        assert writer

        initializeResourceBundle()
        def builder = new StreamingMarkupBuilder()
        def xml = builder.bind {
            mkp.xmlDeclaration()
            GMetrics(url:GMETRICS_URL, version:GMetricsVersion.getVersion()) {
                out << buildReportElement()
                out << buildProjectElement(analysisContext)
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

    private buildProjectElement(AnalysisContext analysisContext) {
        return {
            Project(title:title) {
                analysisContext.sourceDirectories.each { sourceDirectory ->
                    SourceDirectory(sourceDirectory)
                }
            }
        }
    }

    private buildPackageElements(resultsNode) {
        return buildElement(resultsNode)
    }

    private buildElement(ResultsNode resultsNode) {
        switch(resultsNode.level) {
            case MetricLevel.PACKAGE: return buildPackageElement(resultsNode)
            case MetricLevel.CLASS: return buildChildElement('Class', resultsNode)
            case MetricLevel.METHOD: return buildChildElement('Method', resultsNode)
        }
    }

    private buildPackageElement(resultsNode) {
        def elementName = isRoot(resultsNode) ? 'PackageSummary' : 'Package'
        def attributeMap = isRoot(resultsNode) ? [:] : [path:resultsNode.path]
        return {
            "$elementName"(attributeMap) {
                out << buildMetricElements(resultsNode.metricResults, resultsNode.level)
                resultsNode.children.each { childName, childResultsNode ->
                    if (!isPackage(childResultsNode)) {
                        out << buildElement(childResultsNode)
                    }
                }

            }
            resultsNode.children.each { childName, childResultsNode ->
                if (isPackage(childResultsNode)) {
                    out << buildElement(childResultsNode)
                }
            }
        }
    }

    // Build element for Class or Method
    private buildChildElement(String typeName, resultsNode) {
        return {
            "$typeName"([name:resultsNode.name]) {
                out << buildMetricElements(resultsNode.metricResults, resultsNode.level)
                resultsNode.children.each { childName, childResultsNode ->
                    out << buildElement(childResultsNode)
                }
            }
        }
    }

    private buildMetricElements(metricResults, MetricLevel level) {
        return {
            metricResults.each { metricResult ->
                out << buildMetricElement(metricResult, level)
            }
        }
    }

    private buildMetricElement(MetricResult metricResult, MetricLevel level) {
        def metric = metricResult.getMetric()
        return {
            if (includesMetric(metric) && includesLevel(metric, level) && level >= metric.getBaseLevel()) {
                def attributes = [name: metric.name]
                metric.functions.each { functionName ->
                    if (includesFunction(metric, functionName)) {
                        attributes[functionName] = metricResult[functionName]
                    }
                }
                MetricResult(attributes)
            }
        }
    }

    private boolean isRoot(results) {
        results.path == null
    }

    private buildMetricsElement(MetricSet metricSet) {
        def metrics = metricSet.metrics.findAll { metric -> includesMetric(metric) }
        def sortedMetrics = metrics.toList().sort { metric -> metric.name }
        return {
            Metrics {
                sortedMetrics.each { Metric metric ->
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
