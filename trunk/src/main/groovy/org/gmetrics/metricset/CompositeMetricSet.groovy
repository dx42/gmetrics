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

/**
 * A <code>MetricSet</code> implementation that aggregates a set of MetricSets and Metrics.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class CompositeMetricSet implements MetricSet {
    private metrics = []

    /**
     * Add a single Metric to this MetricSet
     * @param metric - the Metric to add
     */
    void addMetric(Metric metric) {
        assert metric != null
        metrics << metric
    }

    /**
     * Add all of the Metrics within the specified MetricSet to this MetricSet
     * @param metricSet - the MetricSet whose Metrics are to be included
     */
    void addMetricSet(MetricSet metricSet) {
        assert metricSet != null
        metrics.addAll(metricSet.getMetrics())
    }

    /**
     * @return a List of Metric objects. The returned List is immutable.
     */
    List getMetrics() {
        return metrics.asImmutable()
    }
}
