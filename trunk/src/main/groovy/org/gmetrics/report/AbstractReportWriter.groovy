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

import org.apache.log4j.Logger
import org.gmetrics.resultsnode.ResultsNode
import org.gmetrics.analyzer.AnalysisContext
import org.gmetrics.metric.Metric
import org.gmetrics.formatter.FormatterFactory
import org.gmetrics.formatter.Formatter
import org.gmetrics.metricset.MetricSet

/**
 * Abstract superclass for ReportWriter implementation classes.
 * <p/>
 * Subclasses must implement the <code>writeReport(Writer, ResultsNode, AnalysisContext)</code> method
 * and define a <code>defaultOutputFile</code> property.
 *
 * @author Chris Mair
 */
abstract class AbstractReportWriter implements ReportWriter {

    protected static final BASE_MESSAGES_BUNDLE = "gmetrics-base-messages"
    protected static final CUSTOM_MESSAGES_BUNDLE = "gmetrics-messages"
    protected static final GMETRICS_URL = "http://www.gmetrics.org"

    String outputFile
    Object writeToStandardOut

    @SuppressWarnings(['LoggerWithWrongModifiers', 'FieldName'])
    protected final LOG = Logger.getLogger(getClass())

    protected customMessagesBundleName = CUSTOM_MESSAGES_BUNDLE
    protected resourceBundle
    protected Map<Metric,Formatter> formatters = [:]
    protected formatterFactory = new FormatterFactory()

    // Allow tests to override these
    protected initializeResourceBundle = { initializeDefaultResourceBundle() }
    protected getTimestamp = { new Date() }

    abstract void writeReport(Writer writer, ResultsNode resultsNode, AnalysisContext analysisContext)

    void writeReport(ResultsNode resultsNode, AnalysisContext analysisContext) {
        assert analysisContext
        assert analysisContext.metricSet

        initializeResourceBundle()
        initializeFormatters(analysisContext.metricSet)

        if (isWriteToStandardOut()) {
            writeReportToStandardOut(resultsNode, analysisContext)
        }
        else {
            writeReportToFile(resultsNode, analysisContext)
        }
    }

    private void writeReportToStandardOut(ResultsNode resultsNode, AnalysisContext analysisContext) {
        def writer = new OutputStreamWriter(System.out)
        writeReport(writer, resultsNode, analysisContext)
    }

    private void writeReportToFile(ResultsNode resultsNode, AnalysisContext analysisContext) {
        def outputFilename = outputFile ?: getProperty('defaultOutputFile')
        def file = new File(outputFilename)
        file.withWriter { writer ->
            writeReport(writer, resultsNode, analysisContext)
        }
        LOG.info("Report file [$outputFilename] created.")
    }

    protected void initializeDefaultResourceBundle() {
        def baseBundle = ResourceBundle.getBundle(BASE_MESSAGES_BUNDLE)
        resourceBundle = baseBundle
        try {
            resourceBundle = ResourceBundle.getBundle(customMessagesBundleName)
            LOG.info("Using custom message bundle [$customMessagesBundleName]")
            resourceBundle.setParent(baseBundle)
        }
        catch(MissingResourceException) {
            LOG.info("No custom message bundle found for [$customMessagesBundleName]. Using default messages.")
        }
    }

    protected String getResourceBundleString(String resourceKey, String defaultString='?') {
        def string = defaultString
        try {
            string = resourceBundle.getString(resourceKey)
        } catch (MissingResourceException e) {
            LOG.warn("No string found for resourceKey=[$resourceKey]")
        }
        return string
    }

    @SuppressWarnings('ReturnNullFromCatchBlock')
    protected String getResourceBundleStringOrNull(String resourceKey) {
        try {
            return resourceBundle.getString(resourceKey)
        }
        catch (MissingResourceException e) {
            return null
        }
    }

    protected void initializeFormatters(MetricSet metricSet) {
        metricSet.metrics.each { metric ->
            def lookupKey = metric.name + '.formatter'
            def formatterSpec = getResourceBundleStringOrNull(lookupKey)
            formatters[metric] = formatterSpec ? formatterFactory.getFormatter(formatterSpec) : null
        }
    }

    protected String formatMetricResultValue(Metric metric, Object value) {
        def formatter = formatters[metric]
        return formatter ? formatter.format(value) : value
    }

    protected String getFormattedTimestamp() {
        def dateFormat = java.text.DateFormat.getDateTimeInstance()
        return dateFormat.format(getTimestamp())
    }

    private boolean isWriteToStandardOut() {
        writeToStandardOut == true || writeToStandardOut == 'true'
    }
}