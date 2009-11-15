package org.gmetrics.report

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.StubMetric
import org.gmetrics.result.NumberMetricResult
import org.gmetrics.resultsnode.StubResultsNode
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.metric.MetricLevel

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

/**
 * Tests for AbstractReportWriter
 *
 * @author Chris Mair
 * @version $Revision: 60 $ - $Date: 2009-02-22 14:46:41 -0500 (Sun, 22 Feb 2009) $
 */
class AbstractReportWriterTest extends AbstractTestCase {
    private static final DEFAULT_STRING = '?'
    private reportWriter

    void testInitializeResourceBundle_CustomMessagesFileExists() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('htmlReport.titlePrefix')   // in "gmetrics-base-messages.properties"
        assert reportWriter.getResourceBundleString('abc')                      // in "gmetrics-messages.properties"
    }

    void testInitializeResourceBundle_CustomMessagesFileDoesNotExist() {
        reportWriter.customMessagesBundleName = 'DoesNotExist'
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('htmlReport.titlePrefix')   // in "gmetrics-base-messages.properties"
        assert reportWriter.getResourceBundleString('abc') == DEFAULT_STRING 
    }

    void testGetResourceBundleString() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('abc') == '123'
    }

    void testGetResourceBundleString_ReturnsDefaultStringIfKeyNotFound() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('DoesNotExist') == DEFAULT_STRING
    }

    void testGetGMetricsVersion() {
        assert reportWriter.getGMetricsVersion() == new File('src/main/resources/gmetrics-version.txt').text
    }

    void setUp() {
        super.setUp()
        reportWriter = new TestAbstractReportWriter()
    }
}

/**
 * Concrete subclass of AbstractReportWriter for testing
 */
protected class TestAbstractReportWriter extends AbstractReportWriter {
}