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
 * A MetricResult for numbers (integers, BigDecimals, etc.), that returns the same, single value for
 * total, average, minimum and maximum. An instance of this class is immutable.
 *
 * @author Chris Mair
 */
class SingleNumberMetricResult implements MetricResult {

    final Metric metric
    final MetricLevel metricLevel
    final number
    final Integer lineNumber
    final int count = 1

    /**
     * Construct a new instance
     * @param metric - the Metric to which this result applies
     * @param metricLevel - the metric level for this result
     * @param number - the single value to use for total, average, minimum, and maximum
     * @param lineNumber - the line number for the source element (AST) that triggered this metric result; may be null
     */
    SingleNumberMetricResult(Metric metric, MetricLevel metricLevel, number, Integer lineNumber=null) {
        assert metric
        assert metricLevel
        assert number != null
        this.metric = metric
        this.metricLevel = metricLevel
        this.number = number
        this.lineNumber = lineNumber
    }

    @Override
    Object getAt(String name) {
        return number
    }

    @Override
    String toString() {
        "SingleNumberMetricResult[metric=${metric.name}, $number]"
    }

}