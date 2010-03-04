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

import java.text.DateFormat
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.metric.StubMetric

/**
 * Tests for XmlReportWriter
 *
 * @author Chris Mair
 * @version $Revision: 81 $ - $Date: 2010-02-23 21:36:09 -0500 (Tue, 23 Feb 2010) $
 */
class XmlReportWriterTest extends AbstractReportWriterTestCase {

    private static final VERSION_FILE = 'src/main/resources/gmetrics-version.txt'
    private static final VERSION = new File(VERSION_FILE).text
    private static final TIMESTAMP_DATE = new Date(1262361072497)
    private static final FORMATTED_TIMESTAMP = DateFormat.getDateTimeInstance().format(TIMESTAMP_DATE)
    private static final XML_DECLARATION = '<?xml version="1.0"?>'
    private static final GMETRICS_ROOT = """<GMetrics url='http://www.gmetrics.org' version='${VERSION}'>"""
    private static final GMETRICS_END_TAG = "</GMetrics>"
    private static final REPORT_TIMESTAMP = "<Report timestamp='${FORMATTED_TIMESTAMP}'/>"
    private static final METRIC_DESCRIPTIONS_1 = """
        <Metrics>
            <Metric name='Metric1'>
                <Description><![CDATA[Description for Metric1]]></Description>
            </Metric>
        </Metrics>
    """
    private static final METRIC_DESCRIPTIONS_2 = """
        <Metrics>
            <Metric name='Metric1'>
                <Description><![CDATA[Description for Metric1]]></Description>
            </Metric>
            <Metric name='Metric2'>
                <Description><![CDATA[Description for Metric2]]></Description>
            </Metric>
        </Metrics>
    """

    private static final PACKAGE_SUMMARY_1 = """
        <PackageSummary>
            <Metric name='Metric1' total='10' average='10'/>
        </PackageSummary>
    """

    private static final PACKAGE_SUMMARY_2 = """
        <PackageSummary>
            <Metric name='Metric1' total='10' average='10'/>
            <Metric name='Metric2' total='20' average='20'/>
        </PackageSummary>
    """

    private static final PACKAGE_NO_CLASSES = """
        <Package path='Dir1'>
            <Metric name='Metric1' total='11' average='11'/>
            <Metric name='Metric2' total='21' average='21'/>
        </Package>
    """

    private static final PACKAGE_2_CLASSES = """
        <Package path='org'>
            <Metric name='Metric1' total='11' average='11'/>
            <Metric name='Metric2' total='21' average='21'/>
            <Class name='MyDao'>
                <Metric name='Metric1' total='101' average='101'/>
            </Class>
            <Class name='MyController'>
                <Metric name='Metric1' total='102' average='102'/>
            </Class>
        </Package>
    """
    
//        <Project title='My Cool Project'>
//            <SourceDirectory>c:/MyProject/src/main/groovy</SourceDirectory>
//            <SourceDirectory>c:/MyProject/src/test/groovy</SourceDirectory>
//        </Project>

    static reportFilename = "GMetricsXmlReport.xml" 
    private localizedMessages
    private metric1, metric2

    void testThatDefaultOutputFile_IsGmetricsReportHtml() {
        assert reportWriter.defaultOutputFile == 'GMetricsXmlReport.xml'
    }

    void testWriteReport_SingleResultsNodeWithSingleMetric() {
        final XML = XML_DECLARATION + GMETRICS_ROOT + REPORT_TIMESTAMP + PACKAGE_SUMMARY_1 + METRIC_DESCRIPTIONS_1 + GMETRICS_END_TAG
        metricSet = new ListMetricSet([metric1])
        def resultsNode = packageResultsNode(metricResults:[metric1Result(10)])
        assertReportXml(resultsNode, XML)
    }

    void testWriteReport_SingleResultsNodeWithTwoMetrics() {
        final XML = XML_DECLARATION + GMETRICS_ROOT + REPORT_TIMESTAMP + PACKAGE_SUMMARY_2 + METRIC_DESCRIPTIONS_2 + GMETRICS_END_TAG
        metricSet = new ListMetricSet([metric2, metric1])
        def resultsNode = packageResultsNode(metricResults:[metric1Result(10), metric2Result(20)])
        assertReportXml(resultsNode, XML)
    }

    void testWriteReport_Package_TwoMetrics() {
        final XML = XML_DECLARATION + GMETRICS_ROOT + REPORT_TIMESTAMP + PACKAGE_SUMMARY_2 + PACKAGE_NO_CLASSES + METRIC_DESCRIPTIONS_2 + GMETRICS_END_TAG
        metricSet = new ListMetricSet([metric1, metric2])
        def rootNode = packageResultsNode(metricResults:[metric1Result(10), metric2Result(20)])
        def childPackageNode = packageResultsNode(path:'Dir1', metricResults:[metric1Result(11), metric2Result(21)])
        rootNode.children['Dir1'] = childPackageNode
        assertReportXml(rootNode, XML)
    }

    void testWriteReport_Package_TwoClasses_TwoMetrics() {
        final XML = XML_DECLARATION + GMETRICS_ROOT + REPORT_TIMESTAMP + PACKAGE_SUMMARY_2 + PACKAGE_2_CLASSES + METRIC_DESCRIPTIONS_2 + GMETRICS_END_TAG
        metricSet = new ListMetricSet([metric1, metric2])
        def rootNode = packageResultsNode(metricResults:[metric1Result(10), metric2Result(20)])
        def packageNode = packageResultsNode(path:'org', metricResults:[metric1Result(11), metric2Result(21)])
        rootNode.children['org'] = packageNode
        def class1Node = classResultsNode(metricResults:[metric1Result(101)])
        def class2Node = classResultsNode(metricResults:[metric1Result(102)])
        packageNode.children = [MyDao:class1Node, MyController:class2Node]
        assertReportXml(rootNode, XML)
    }

    private static final PACKAGE_2CLASSES_3METHODS = """
        <Package path='test'>
            <Metric name='Metric1' total='11' average='11'/>
            <Metric name='Metric2' total='21' average='21'/>
            <Class name='MyDao'>
                <Metric name='Metric1' total='101' average='101'/>
                <Method name='process'>
                    <Metric name='Metric1' total='1001' average='1001'/>
                </Method>
            </Class>
            <Class name='MyController'>
                <Metric name='Metric1' total='102' average='102'/>
                <Method name='initialize'>
                    <Metric name='Metric1' total='1002' average='1002'/>
                </Method>
                <Method name='cleanup'>
                    <Metric name='Metric1' total='1003' average='1003'/>
                </Method>
            </Class>
        </Package>
    """

    void testWriteReport_Package_TwoClasses_ThreeMethods_TwoMetrics() {
        final XML = XML_DECLARATION + GMETRICS_ROOT + REPORT_TIMESTAMP + PACKAGE_SUMMARY_2 + PACKAGE_2CLASSES_3METHODS + METRIC_DESCRIPTIONS_2 + GMETRICS_END_TAG
        metricSet = new ListMetricSet([metric1, metric2])
        def rootNode = packageResultsNode(metricResults:[metric1Result(10), metric2Result(20)])
        def packageNode = packageResultsNode(path:'test', metricResults:[metric1Result(11), metric2Result(21)])
        rootNode.children['org'] = packageNode
        def class1Node = classResultsNode(metricResults:[metric1Result(101)])
        def class2Node = classResultsNode(metricResults:[metric1Result(102)])
        packageNode.children = [MyDao:class1Node, MyController:class2Node]
        def method1Node = methodResultsNode(metricResults:[metric1Result(1001)])
        def method2Node = methodResultsNode(metricResults:[metric1Result(1002)])
        def method3Node = methodResultsNode(metricResults:[metric1Result(1003)])
        class1Node.children = [process:method1Node]
        class2Node.children = [initialize:method2Node, cleanup:method3Node]
        assertReportXml(rootNode, XML, true)
    }

    void setUp() {
        super.setUp()
        metric1 = new StubMetric(name:'Metric1')
        metric2 = new StubMetric(name:'Metric2')

        localizedMessages = [
            'Metric1.description':metricDescription(metric1),
            'Metric1.total':'Metric1.total',
            'Metric1.average':'Metric1.average',
            'Metric2.description':metricDescription(metric2),
            'Metric2.total':'M2.total',
            'Metric2.average':'M2.average',
        ]
        reportWriter.initializeResourceBundle = { reportWriter.resourceBundle = [getString:{key -> localizedMessages[key] ?: 'NOT FOUND'}] }
    }

    protected ReportWriter createReportWriter() {
        def rw = new XmlReportWriter()
        rw.getTimestamp = { TIMESTAMP_DATE }
        return rw
    }

    private void assertReportXml(resultsNode, expectedContents, boolean writeToFile=false) {
        def reportText = writeReport(resultsNode, writeToFile)
        assertXml(reportText, expectedContents)
    }

    private void assertXml(String actualXml, String expectedXml) {
        assertEquals(normalizeXml(expectedXml), normalizeXml(actualXml))
    }

    private String normalizeXml(String xml) {
        return xml.replaceAll(/\>\s*\</, '><').trim()
    }

    private String metricDescription(metric) {
        "Description for " + metric.name
    }
}
