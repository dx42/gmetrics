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

/**
 * A <code>MetricSet</code> implementation that returns a static List of Metrics passed into its constructor.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ListMetricSet implements MetricSet {

    private metrics

    /**
     * Construct a new instance from the specified List of metrics.
     * @param metrics - the List of List of <code>Metric</code> objects; must not be null, but may be empty.
     */
    ListMetricSet(List metrics) {
        assert metrics != null
        assert metrics.every { it instanceof Metric }
        def copy = []
        copy.addAll(metrics)
        this.metrics = Collections.unmodifiableList(copy)
    }

    /**
     * @return a List of Metric objects
     */
    List getMetrics() {
        metrics
    }
}