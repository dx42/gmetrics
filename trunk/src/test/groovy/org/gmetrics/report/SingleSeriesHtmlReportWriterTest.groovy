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

import org.gmetrics.analyzer.AnalysisContext
import org.gmetrics.result.StubMetricResult
import org.gmetrics.util.io.ClassPathResource
import java.text.DateFormat

/**
 * Tests for SingleSeriesHtmlReportWriter
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class SingleSeriesHtmlReportWriterTest extends AbstractReportWriterTestCase {

    private static final TIMESTAMP_DATE = new Date(1262361072497)
    private static final FORMATTED_TIMESTAMP = DateFormat.getDateTimeInstance().format(TIMESTAMP_DATE)
    private static final TIMESTAMP_LABEL = 'Timestamp:'
    private static final HTML_TAG = 'html'
    private static final PACKAGE_HEADING = 'Package-heading'
    private static final CLASS_HEADING = 'Class-heading'
    private static final METHOD_HEADING = 'Method-heading'
    private static final BOTTOM_LINK = "<a href='http://www.gmetrics.org'>GMetrics"
    private static final DEFAULT_TITLE = SingleSeriesHtmlReportWriter.DEFAULT_TITLE
    private static final CSS_FILE_CONTENTS = getCssFileContents()

    static reportFilename = "GMetricsSingleSeriesReport.html"

    private emptyResultsNode = packageResultsNode(path:'test')

    void testWriteReport_SingleClass() {
        final CONTENTS = [
                HTML_TAG,
                DEFAULT_TITLE,
                CSS_FILE_CONTENTS,
                DEFAULT_TITLE,
                TIMESTAMP_LABEL, FORMATTED_TIMESTAMP,
                CLASS_HEADING, 'M1.average',
                'Class1', 776,
                BOTTOM_LINK]

        def metricResult = new StubMetricResult(metric:metric1, average:776)
        def resultsNode = packageResultsNode([:],
        [
            Class1: classResultsNode(metricResults:[metricResult])
        ])

        reportWriter.metric = 'Metric1'
        reportWriter.level = 'class'
        reportWriter.function = 'average'
        assertReportContents(resultsNode, CONTENTS, true)
    }

    void testWriteReport_Methods() {
        final CONTENTS = [
                HTML_TAG,
                DEFAULT_TITLE,
                CSS_FILE_CONTENTS,
                DEFAULT_TITLE,
                TIMESTAMP_LABEL, FORMATTED_TIMESTAMP,
                METHOD_HEADING, 'M1.minimum',
                'method1', 123,
                'method2', 789,
                BOTTOM_LINK]

        def metricResult1 = new StubMetricResult(metric:metric1, minimum:123)
        def metricResult2 = new StubMetricResult(metric:metric1, minimum:789)
        def resultsNode = packageResultsNode([:],
        [
            Class1: classResultsNode([:],
            [
                    method1: methodResultsNode(metricResults:[metricResult1]),
                    method2: methodResultsNode(metricResults:[metricResult2]),
            ])
        ])

        reportWriter.metric = 'Metric1'
        reportWriter.level = 'method'
        reportWriter.function = 'minimum'
        assertReportContents(resultsNode, CONTENTS, true)
    }

    void testWriteReport_Packages() {
        final CONTENTS = [
                HTML_TAG,
                DEFAULT_TITLE,
                CSS_FILE_CONTENTS,
                DEFAULT_TITLE,
                TIMESTAMP_LABEL, FORMATTED_TIMESTAMP,
                PACKAGE_HEADING, 'M1.total',
                'src/test/groovy', 123,
                'src/main/groovy', 789,
                'src/main/resources', 992,
                BOTTOM_LINK]

        def metricResult1 = new StubMetricResult(metric:metric1, total:123)
        def metricResult2 = new StubMetricResult(metric:metric1, total:789)
        def metricResult3 = new StubMetricResult(metric:metric1, total:992)
        def resultsNode = packageResultsNode([:],
        [
            'src/test/groovy': packageResultsNode(metricResults:[metricResult1]),
            'src/main/groovy': packageResultsNode(metricResults:[metricResult2]),
            'src/main/resources': packageResultsNode(metricResults:[metricResult3]),
        ])

        reportWriter.metric = 'Metric1'
        reportWriter.level = 'package'
        reportWriter.function = 'total'
        assertReportContents(resultsNode, CONTENTS, true)
    }

    void testWriteReport_CustomizeTitleAndSubtitle() {
        final TITLE = 'A Custom Title'
        final SUBTITLE = 'Custom Subtitle'
        final CONTENTS = [
                HTML_TAG,
                TITLE,
                TITLE,
                SUBTITLE,
                BOTTOM_LINK]
        final NOT_EXPECTED_CONTENTS = [DEFAULT_TITLE]

        def resultsNode = packageResultsNode([:])
        reportWriter.title = TITLE
        reportWriter.subtitle = SUBTITLE
        reportWriter.metric = 'Metric1'
        reportWriter.level = 'class'
        reportWriter.function = 'average'
        assertReportContents(resultsNode, CONTENTS, NOT_EXPECTED_CONTENTS)
    }

    void testWriteReport_NullOrEmptyLevel_ThrowsException() {
        reportWriter.metric = 'Metric1'
        reportWriter.function = 'average'
        shouldFailWithMessageContaining('level') { reportWriter.writeReport(emptyResultsNode, analysisContext) }

        reportWriter.level = ''
        shouldFailWithMessageContaining('level') { reportWriter.writeReport(emptyResultsNode, analysisContext) }
    }

    void testWriteReport_NullOrEmptyMetric_ThrowsException() {
        reportWriter.function = 'average'
        reportWriter.level = 'package'
        shouldFailWithMessageContaining('metric') { reportWriter.writeReport(emptyResultsNode, analysisContext) }

        reportWriter.metric = ''
        shouldFailWithMessageContaining('metric') { reportWriter.writeReport(emptyResultsNode, analysisContext) }
    }

    void testWriteReport_NullOrEmptyFunction_ThrowsException() {
        reportWriter.level = 'package'
        reportWriter.metric = 'ABC'
        shouldFailWithMessageContaining('function') { reportWriter.writeReport(emptyResultsNode, analysisContext) }

        reportWriter.function = ''
        shouldFailWithMessageContaining('function') { reportWriter.writeReport(emptyResultsNode, analysisContext) }
    }


    void setUp() {
        super.setUp()
        analysisContext = new AnalysisContext(metricSet:metricSet1)

        def localizedMessages = [
            'singleSeriesHtmlReport.reportTimestamp.label': TIMESTAMP_LABEL,
            'singleSeriesHtmlReport.packageHeading': PACKAGE_HEADING,
            'singleSeriesHtmlReport.classHeading': CLASS_HEADING,
            'singleSeriesHtmlReport.methodHeading': METHOD_HEADING,
            'Metric1.total':'M1.total',
            'Metric1.average':'M1.average',
            'Metric1.minimum':'M1.minimum',
        ]
        reportWriter.initializeResourceBundle = { reportWriter.resourceBundle = [getString:{key -> localizedMessages[key] ?: 'NOT FOUND'}] }
    }

    protected ReportWriter createReportWriter() {
        def rw = new SingleSeriesHtmlReportWriter()
        rw.getTimestamp = { TIMESTAMP_DATE }
        return rw
    }

    private static String getCssFileContents() {
        return ClassPathResource.getInputStream(SingleSeriesHtmlReportWriter.DEFAULT_CSS_FILE).text
    }
}
