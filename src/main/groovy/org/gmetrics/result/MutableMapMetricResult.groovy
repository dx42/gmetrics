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
package org.gmetrics.result

import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel

/**
 * A MetricResult for arbitrary values, backed by a mutable Map.
 *
 * @author Chris Mair
 */
class MutableMapMetricResult implements MetricResult {

    final Metric metric
    final MetricLevel metricLevel
    final Integer lineNumber = null
    final map

    int count = 0

    /**
     * Construct a new instance
     * @param metric - the Metric to which this result applies
     * @param metricLevel - the metric level for this result
     * @param map - the Map of metric result values
     */
    MutableMapMetricResult(Metric metric, MetricLevel metricLevel, Map<String,Object> map) {
        assert metric
        assert metricLevel
        assert map != null
        this.metric = metric
        this.metricLevel = metricLevel
        this.map = new HashMap(map)
    }

    @Override
    Object getAt(String name) {
        return map[name]
    }

    @Override
    String toString() {
        "MutableMapMetricResult[metric=${metric.name}, $map]"
    }

}