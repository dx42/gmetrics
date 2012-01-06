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
 * A NumberMetricResult that aggregates multiple values. This MetricResult supports the
 * 'total', 'average', 'minimum' and 'maximum' functions.
 *
 * @author Chris Mair
 */
class AggregateNumberMetricResult implements MetricResult {

    private static final TOTAL = 'total'
    private static final AVERAGE = 'average'
    private static final MINIMUM = 'minimum'
    private static final MAXIMUM = 'maximum'

    int scale = 1
    final Metric metric
    final MetricLevel metricLevel
    final Integer lineNumber

    private count
    private final functionValues = [:]

    /**
     * Construct a new instance
     * @param metric - the Metric to which this result applies
     * @param metricLevel - the metric level for this result
     * @param children - the optional collection of results from children
     * @param lineNumber - the line number for the source element (AST) that triggered this metric result; may be null
     * @param predefinedValues - the optional Map of functionName:resultValue to specify override values
     *          for the specified functions; may be null or empty to specify no predefined values
     */
    AggregateNumberMetricResult(
                Metric metric,
                MetricLevel metricLevel,
                Collection<MetricResult> children,
                Integer lineNumber,
                Map<String,Object> predefinedValues=null) {
        assert metric
        assert metricLevel
        assert children != null
        this.metric = metric
        this.metricLevel = metricLevel
        this.lineNumber = lineNumber
        applyPredefinedValues(predefinedValues)
        calculateFunctions(metric, children)
    }

    private void applyPredefinedValues(Map<String,Object> predefinedValues) {
        if (predefinedValues) {
            functionValues.putAll(predefinedValues)
        }
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
        count = calculateCount(children)

        def sum = calculateTotal(children)
        if (shouldCalculateFunction(TOTAL)) {
            functionValues[TOTAL] = sum
        }
        if (shouldCalculateFunction(AVERAGE)) {
            functionValues[AVERAGE] = calculateAverage(sum, count)
        }
        if (shouldCalculateFunction(MINIMUM)) {
            functionValues[MINIMUM] = calculateMinimum(children)
        }
        if (shouldCalculateFunction(MAXIMUM)) {
            functionValues[MAXIMUM] = calculateMaximum(children)
        }
    }

    private int calculateCount(Collection<MetricResult> children) {
        children.inject(0) { value, child -> value + child.count }
    }

    private Object calculateTotal(Collection<MetricResult> children) {
        return children.inject(0) { value, child ->
            return child[TOTAL] ? value + child[TOTAL] : value
        }
    }

    private Object calculateMinimum(Collection<MetricResult> children) {
        def minChild = children.min { child -> child[MINIMUM] }
        return minChild != null ? minChild[MINIMUM] : 0
    }

    private Object calculateMaximum(Collection<MetricResult> children) {
        def maxChild = children.max { child -> child[MAXIMUM] }
        return maxChild != null ? maxChild[MAXIMUM] : 0
    }

    private Object calculateAverage(sum, count) {
        if(sum && count) {
            def result = sum / count
            return result.setScale(scale, BigDecimal.ROUND_HALF_UP)
        }
        return 0
    }

    private boolean shouldCalculateFunction(String functionName) {
        return functionName in metric.functions && !functionValues.containsKey(functionName)
    }

}