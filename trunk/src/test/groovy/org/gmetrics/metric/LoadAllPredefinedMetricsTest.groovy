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
 package org.gmetrics.metric

import org.gmetrics.metricregistry.DefaultMetricRegistry
import org.gmetrics.test.AbstractTestCase
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Test that loads all predefined Metrics and verifies metric descriptions
 *
 * @author Chris Mair
 */
class LoadAllPredefinedMetricsTest extends AbstractTestCase {

    private static final BASE_MESSAGES_BUNDLE = 'gmetrics-base-messages'
    private messages

    void testPredefinedMetricsHaveRequiredProperties() {

        DefaultMetricRegistry.METRIC_CLASSES.each { metricClass ->
            def metric = metricClass.newInstance()
            assert messages.getString(metric.name + '.description')
            assert messages.getString(metric.name + '.description.html')
            assert messages.getString(metric.name + '.total')
            assert messages.getString(metric.name + '.average')
        }
    }

    @SuppressWarnings('CatchThrowable')
    void testPredefinedMetricsHaveValidHtmlDescriptions() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        factory.validating = false
        factory.namespaceAware = true
        DocumentBuilder builder = factory.newDocumentBuilder()

        def errors = []
        DefaultMetricRegistry.METRIC_CLASSES.each { metricClass ->
            def metric = metricClass.newInstance()
            String propertyName = metric.name + '.description.html'

            def htmlSnippet = messages.getString(propertyName)

            //builder.setErrorHandler(new SimpleErrorHandler());
            ByteArrayInputStream bs = new ByteArrayInputStream(('<root>' + htmlSnippet + '</root>').bytes)
            try {
                builder.parse(bs)
            } catch (Throwable t) {
                errors.add("""An error occurred parsing the property $propertyName
Value: $htmlSnippet
Error: $t.message

""")
            }
        }
        if (errors) {
            fail(errors.join('\n'))
        }
    }

    void setUp() {
        super.setUp()
        messages = ResourceBundle.getBundle(BASE_MESSAGES_BUNDLE)
    }
}
