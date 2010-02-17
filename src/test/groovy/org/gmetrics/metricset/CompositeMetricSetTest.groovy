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
package org.gmetrics.metricset

import org.gmetrics.metric.Metric
import org.gmetrics.test.AbstractTestCase

/**
 * Tests for CompositeMetricSet
 *
 * @author Chris Mair
 * @version $Revision: 24 $ - $Date: 2009-12-10 21:17:05 -0500 (Thu, 10 Dec 2009) $
 */
class CompositeMetricSetTest extends AbstractTestCase {
    static final METRIC1 = [:] as Metric
    static final METRIC2 = [:] as Metric
    private compositeMetricSet
                                                      
    void testImplementsMetricSetInterface() {
        assert compositeMetricSet instanceof MetricSet
    }

    void testDefaultsToEmptyMetricSet() {
        assert compositeMetricSet.getMetrics() == []
    }

    void testAddMetricSet_Null() {
        shouldFailWithMessageContaining('metricSet') { compositeMetricSet.addMetricSet((MetricSet)null) }
    }

    void testAddMetricSet_OneMetricSet() {
        def metricSet = new ListMetricSet([METRIC1])
        compositeMetricSet.addMetricSet(metricSet)
        assert compositeMetricSet.getMetrics() == [METRIC1]
    }

    void testAddMetricSet_TwoMetricSets() {
        def metricSet1 = new ListMetricSet([METRIC1])
        def metricSet2 = new ListMetricSet([METRIC2])
        compositeMetricSet.addMetricSet(metricSet1)
        compositeMetricSet.addMetricSet(metricSet2)
        assert compositeMetricSet.getMetrics() == [METRIC1, METRIC2]
    }

    void testAddMetric_Null() {
        shouldFailWithMessageContaining('metric') { compositeMetricSet.addMetric((Metric)null) }
    }

    void testAddMetric() {
        compositeMetricSet.addMetric(METRIC1)
        compositeMetricSet.addMetric(METRIC2)
        assert compositeMetricSet.getMetrics() == [METRIC1, METRIC2]
    }

    void testInternalMetricsListIsImmutable() {
        def metrics = compositeMetricSet.metrics
        shouldFail(UnsupportedOperationException) { metrics.add(123) }
    }

    void setUp() {
        super.setUp()
        compositeMetricSet = new CompositeMetricSet()
    }
}
