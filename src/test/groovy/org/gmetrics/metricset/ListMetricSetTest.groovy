/*
 * Copyright 2008 the original author or authors.
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
package org.gmetrics.metricset

import org.gmetrics.metric.Metric
import org.gmetrics.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for ListMetricSet
 *
 * @author Chris Mair
 */
class ListMetricSetTest extends AbstractTestCase {

    private static final METRIC = [:] as Metric

    @Test
	void testWithMetrics() {
        def metricSet = new ListMetricSet([METRIC])
        assert metricSet.getMetrics() == [METRIC]
    }

    @Test
	void testMetricsListIsImmutable() {
        def list = [METRIC]
        def metricSet = new ListMetricSet(list)
        list.clear()
        def m = metricSet.getMetrics()
        assert m == [METRIC]
        shouldFail(UnsupportedOperationException) { m.clear() }
    }

    @Test
	void testConstructorThrowsExceptionForNull() {
        shouldFailWithMessageContaining('metrics') { new ListMetricSet(null) }
    }

    @Test
	void testConstructorThrowsExceptionIfNotAMetric() {
        shouldFail { new ListMetricSet([METRIC, 23]) }
    }
}