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
package org.gmetrics.report

import org.gmetrics.analyzer.AnalysisContext
import org.gmetrics.util.io.ClassPathResource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.text.DateFormat

/**
 * Tests for SingleSeriesHtmlReportWriter
 *
 * @author Chris Mair
 */
class SingleSeriesHtmlReportWriterTest extends AbstractReportWriterTestCase {

    private static final Date TIMESTAMP_DATE = new Date(1262361072497)
    private static final String FORMATTED_TIMESTAMP = DateFormat.getDateTimeInstance().format(TIMESTAMP_DATE)
    private static final String TIMESTAMP_LABEL = 'Timestamp:'
    private static final String HTML_TAG = 'html'
    private static final String PACKAGE_HEADING = 'Package-heading'
    private static final String CLASS_HEADING = 'Class-heading'
    private static final String METHOD_HEADING = 'Method-heading'
    private static final String BOTTOM_LINK = "<a href='http://www.gmetrics.org'>GMetrics"
    private static final String DEFAULT_TITLE = SingleSeriesHtmlReportWriter.DEFAULT_TITLE
    private static final String CSS_FILE_CONTENTS = getCssFileContents()
    private static final List STANDARD_CONTENTS = [ HTML_TAG, DEFAULT_TITLE, CSS_FILE_CONTENTS, DEFAULT_TITLE, TIMESTAMP_LABEL, FORMATTED_TIMESTAMP]
    private static final String REPORT_FILE = "$REPORTS_DIR/GMetricsSingleSeriesReport.html"
    
	static String reportFilename = REPORT_FILE

    private emptyResultsNode = packageResultsNode(path:'test')
    private localizedMessages

    @Test
	void testWriteReport_SingleClass() {
        final CONTENTS = STANDARD_CONTENTS + [
                CLASS_HEADING, 'M1.average',
                'Class1', 776,
                BOTTOM_LINK]

        def resultsNode = packageResultsNode([:],
        [
            Class1: classResultsNode(metricResults:[metric1Result(average:776)])
        ])

        configureReportWriter(metric: 'Metric1', level: 'class', function: 'average')
        assertReportContents(resultsNode, CONTENTS)
    }

    @Test
	void testWriteReport_Methods() {
        final CONTENTS = STANDARD_CONTENTS + [
                METHOD_HEADING, 'M1.total',
                'method1', 123,
                'method2', 789,
                BOTTOM_LINK]

        def resultsNode = packageResultsNode([:],
        [
            Class1: classResultsNode([:],
            [
                    method1: methodResultsNode(name:'method1', metricResults:[metric1Result(total:123)]),
                    method2: methodResultsNode(name:'method2',metricResults:[metric1Result(total:789)]),
            ])
        ])

        configureReportWriter(metric: 'Metric1', level: 'method', function: 'total')
        assertReportContents(resultsNode, CONTENTS)
    }

    @Test
	void testWriteReport_Packages() {
        final CONTENTS = STANDARD_CONTENTS + [
                PACKAGE_HEADING, 'M1.total',
                'src/test/groovy', 123,
                'src/main/groovy', 789,
                'src/main/resources', 992,
                BOTTOM_LINK]

        def resultsNode = packageResultsNode([:],
        [
            'src/test/groovy': packageResultsNode(path:'src/test/groovy', metricResults:[metric1Result(total:123)]),
            'src/main/groovy': packageResultsNode(path:'src/main/groovy', metricResults:[metric1Result(total:789)]),
            'src/main/resources': packageResultsNode(path:'src/main/resources', metricResults:[metric1Result(total:992)]),
        ])

        configureReportWriter(metric: 'Metric1', level: 'package', function: 'total')
        assertReportContents(resultsNode, CONTENTS)
    }

    @Test
	void testWriteReport_MaxResults() {
        final CONTENTS = STANDARD_CONTENTS + [
                PACKAGE_HEADING, 'M1.total',
                'src/test/groovy', 123,
                'src/main/groovy', 789,
                BOTTOM_LINK]
        final NOT_EXPECTED_CONTENTS = ['src/main/resources', 992]

        def resultsNode = packageResultsNode([:],
        [
            'src/test/groovy': packageResultsNode(path:'src/test/groovy', metricResults:[metric1Result(total:123)]),
            'src/main/groovy': packageResultsNode(path:'src/main/groovy', metricResults:[metric1Result(total:789)]),
            'src/main/resources': packageResultsNode(path:'src/main/resources', metricResults:[metric1Result(total:992)]),
        ])

        configureReportWriter(metric: 'Metric1', level: 'package', function: 'total', maxResults:'2')
        assertReportContents(resultsNode, CONTENTS, NOT_EXPECTED_CONTENTS, true)
    }

    @Test
	void testWriteReport_GreaterThanAndLessThan() {
        final CONTENTS = STANDARD_CONTENTS + [
                PACKAGE_HEADING, 'M1.total',
                'src/main/groovy', 789,
                BOTTOM_LINK]
        final NOT_EXPECTED_CONTENTS = ['src/test/groovy', 123, 'src/main/resources', 992]

        def resultsNode = packageResultsNode([:],
        [
            'src/test/groovy': packageResultsNode(path:'src/test/groovy', metricResults:[metric1Result(total:123)]),
            'src/main/groovy': packageResultsNode(path:'src/main/groovy', metricResults:[metric1Result(total:789)]),
            'src/main/resources': packageResultsNode(path:'src/main/resources', metricResults:[metric1Result(total:992)]),
        ])

        configureReportWriter(metric: 'Metric1', level: 'package', function: 'total', greaterThan:'500', lessThan:'800.50')
        assertReportContents(resultsNode, CONTENTS, NOT_EXPECTED_CONTENTS, true)
    }

    @Test
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
        configureReportWriter(title: TITLE, subtitle: SUBTITLE, metric: 'Metric1', level: 'class', function: 'average')
        assertReportContents(resultsNode, CONTENTS, NOT_EXPECTED_CONTENTS)
    }

    @Test
	void testWriteReport_FormatsValuesUsingConfiguredFormatter() {
        final CONTENTS = ['65%']
        def resultsNode = packageResultsNode([:],
        [
            Class1: classResultsNode(metricResults:[metric1Result(average:0.65)])
        ])

        configureReportWriter(metric: 'Metric1', level: 'class', function: 'average')
        localizedMessages[metric1.name + '.formatter'] = 'org.gmetrics.formatter.PercentageFormatter'
        reportWriter.initializeFormatters(metricSet1)
        assertReportContents(resultsNode, CONTENTS)
    }

    @Test
	void testWriteReport_NullOrEmptyLevel_ThrowsException() {
        reportWriter.metric = 'Metric1'
        reportWriter.function = 'average'
        shouldFailWithMessageContaining('level') { reportWriter.writeReport(emptyResultsNode, analysisContext) }

        reportWriter.level = ''
        shouldFailWithMessageContaining('level') { reportWriter.writeReport(emptyResultsNode, analysisContext) }
    }

    @Test
	void testWriteReport_NullOrEmptyMetric_ThrowsException() {
        reportWriter.function = 'average'
        reportWriter.level = 'package'
        shouldFailWithMessageContaining('metric') { reportWriter.writeReport(emptyResultsNode, analysisContext) }

        reportWriter.metric = ''
        shouldFailWithMessageContaining('metric') { reportWriter.writeReport(emptyResultsNode, analysisContext) }
    }

    @Test
	void testWriteReport_NullOrEmptyFunction_ThrowsException() {
        reportWriter.level = 'package'
        reportWriter.metric = 'ABC'
        shouldFailWithMessageContaining('function') { reportWriter.writeReport(emptyResultsNode, analysisContext) }

        reportWriter.function = ''
        shouldFailWithMessageContaining('function') { reportWriter.writeReport(emptyResultsNode, analysisContext) }
    }

    //------------------------------------------------------------------------------------
    // Setup and Helper Methods
    //------------------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        analysisContext = new AnalysisContext(metricSet:metricSet1)

        localizedMessages = [
            'singleSeriesHtmlReport.reportTimestamp.label': TIMESTAMP_LABEL,
            'singleSeriesHtmlReport.packageHeading': PACKAGE_HEADING,
            'singleSeriesHtmlReport.classHeading': CLASS_HEADING,
            'singleSeriesHtmlReport.methodHeading': METHOD_HEADING,
            'Metric1.total':'M1.total',
            'Metric1.average':'M1.average',
        ]
        reportWriter.resourceBundle = [getString:{ key -> localizedMessages[key] }]
    }

    protected ReportWriter createReportWriter() {
        def rw = new SingleSeriesHtmlReportWriter()
		rw.outputFile = REPORT_FILE
        rw.getTimestamp = { TIMESTAMP_DATE }
        return rw
    }

    private void configureReportWriter(Map properties) {
        properties.each { name, value -> reportWriter[name] = value }
    }

    private static String getCssFileContents() {
        return ClassPathResource.getInputStream(SingleSeriesHtmlReportWriter.DEFAULT_CSS_FILE).text
    }
}
