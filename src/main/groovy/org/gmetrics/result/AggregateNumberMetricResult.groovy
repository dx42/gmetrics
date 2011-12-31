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
package org.gmetrics.result

import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel

/**
 * A NumberMetricResult that aggregates multiple values. This MetricResult supports the
 * 'total', 'average', 'minimum' and 'maximum' functions.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AggregateNumberMetricResult implements MetricResult {

    private count
    int scale = 1
    private functionValues = [:]
    final Metric metric
    final MetricLevel metricLevel
    final Integer lineNumber

    AggregateNumberMetricResult(Metric metric, MetricLevel metricLevel, Collection<MetricResult> children, Integer lineNumber=null) {
        assert metric
        assert metricLevel
        assert children != null
        this.metric = metric
        this.metricLevel = metricLevel
        calculateFunctions(metric, children)
        this.lineNumber = lineNumber
    }

    int getCount() {
        return count
    }

    Object getAt(String name) {
        return functionValues[name]
    }

    String toString() {
        "AggregateNumberMetricResult[count=$count, $functionValues}]"
    }

    @SuppressWarnings('UnusedMethodParameter')
    protected void calculateFunctions(Metric metric, Collection<MetricResult> children) {
        def sum = children.inject(0) { value, child ->
            return child['total'] ? value + child['total'] : value
        }
        count = children.inject(0) { value, child -> value + child.count }
        if (includesFunction('total')) {
            functionValues['total'] = sum
        }
        if (includesFunction('average')) {
            functionValues['average'] = calculateAverage(sum, count)
        }
        if (includesFunction('minimum')) {
            functionValues['minimum'] = calculateMinimum(children)
        }
        if (includesFunction('maximum')) {
            functionValues['maximum'] = calculateMaximum(children)
        }
    }

    private Object calculateMinimum(Collection<MetricResult> children) {
        def minChild = children.min { child -> child['minimum'] }
        return minChild != null ? minChild['minimum'] : 0
    }

    private Object calculateMaximum(Collection<MetricResult> children) {
        def maxChild = children.max { child -> child['maximum'] }
        return maxChild != null ? maxChild['maximum'] : 0
    }

    private Object calculateAverage(sum, count) {
        if(sum && count) {
            def result = sum / count
            return result.setScale(scale, BigDecimal.ROUND_HALF_UP)
        }
        return 0
    }

    private boolean includesFunction(String functionName) {
        return functionName in metric.functions
    }

}