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

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.Metric
import org.gmetrics.metricset.ListMetricSet

/**
 * Tests for ListMetricSet
 *
 * @author Chris Mair
 * @version $Revision: 60 $ - $Date: 2009-02-22 14:46:41 -0500 (Sun, 22 Feb 2009) $
 */
class ListMetricSetTest extends AbstractTestCase {

    static final METRIC = [:] as Metric

    void testWithMetrics() {
        def ruleSet = new ListMetricSet([METRIC])
        assert ruleSet.getMetrics() == [METRIC]
    }

    void testMetricsListIsImmutable() {
        def list = [METRIC]
        def metricSet = new ListMetricSet(list)
        list.clear()
        def m = metricSet.getMetrics()
        assert m == [METRIC]
        shouldFail(UnsupportedOperationException) { m.clear() }
    }

    void testConstructorThrowsExceptionForNull() {
        shouldFailWithMessageContaining('metrics') { new ListMetricSet(null) }
    }

    void testConstructorThrowsExceptionIfNotAMetric() {
        shouldFail { new ListMetricSet([METRIC, 23]) }
    }
}