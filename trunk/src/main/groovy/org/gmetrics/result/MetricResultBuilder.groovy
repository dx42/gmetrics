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

import static FunctionNames.*

import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel

/**
 * A Builder for MetricResult objects.
 *
 * @author Chris Mair
 */
class MetricResultBuilder {

    Metric metric
    MetricLevel metricLevel
    int scale = 1

    /**
     * Calculate the aggregate metric results for the specified child metric results. The metric and
     * metricLevel properties of this object must be set before calling this method.
     *
     * @param children - the optional collection of results from children
     * @param lineNumber - the line number for the source element (AST) that triggered this metric result; may be null
     * @param overrideValues - the optional Map of functionName:resultValue to specify override values
     *          for the specified functions; may be null or empty to specify no predefined values
     * @return a MetricResult for the calculated values
     */
    MetricResult createAggregateMetricResult(
            Collection<MetricResult> children,
            Integer lineNumber,
            Map<String,Object> overrideValues=null) {

        assert metric
        assert metricLevel
        assert children != null

        def count = calculateCount(children)
        Map<String,Number> functionValues = calculateFunctions(children, count, overrideValues)
        return new NumberMetricResult(metric, metricLevel, functionValues, lineNumber, count)
    }

    String toString() {
        "MetricResultBuilder[metric=$metric, level=$metricLevel]"
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    private Map<String,Number> calculateFunctions(Collection<MetricResult> children, int count, Map<String,Object> overrideValues) {
        Map<String,Number> functionValues = [:]

        def sum = calculateTotal(children)
        functionValues[TOTAL] = total(sum, overrideValues)
        functionValues[AVERAGE] = average(sum, count, overrideValues)
        functionValues[MINIMUM] = minimum(children, overrideValues)
        functionValues[MAXIMUM] = maximum(children, overrideValues)
        return functionValues
    }

    private int calculateCount(Collection<MetricResult> children) {
        children.inject(0) { value, child -> value + child.count }
    }

    private Object total(sum, Map<String,Object> overrideValues) {
        return shouldCalculateFunction(TOTAL, overrideValues) ? sum : overrideValues?.get(TOTAL)
    }

    private Object calculateTotal(Collection<MetricResult> children) {
        return children.inject(0) { value, child ->
            return child[TOTAL] ? value + child[TOTAL] : value
        }
    }

    private Object minimum(Collection<MetricResult> children, Map<String,Object> overrideValues) {
        return shouldCalculateFunction(MINIMUM, overrideValues) ? calculateMinimum(children) : overrideValues?.get(MINIMUM)
    }

    private Object calculateMinimum(Collection<MetricResult> children) {
        def minChild = children.min { child -> child[MINIMUM] }
        return minChild != null ? minChild[MINIMUM] : 0
    }

    private Object maximum(Collection<MetricResult> children, Map<String,Object> overrideValues) {
        return shouldCalculateFunction(MAXIMUM, overrideValues) ? calculateMaximum(children) : overrideValues?.get(MAXIMUM)
    }

    private Object calculateMaximum(Collection<MetricResult> children) {
        def maxChild = children.max { child -> child[MAXIMUM] }
        return maxChild != null ? maxChild[MAXIMUM] : 0
    }

    private Object average(sum, int count, Map<String,Object> overrideValues) {
        return shouldCalculateFunction(AVERAGE, overrideValues) ? calculateAverage(sum, count) : overrideValues?.get(AVERAGE)
    }

    private Object calculateAverage(sum, count) {
        if(sum && count) {
            def result = sum / count
            return result.setScale(scale, BigDecimal.ROUND_HALF_UP)
        }
        return 0
    }

    private boolean shouldCalculateFunction(String functionName, Map<String,Number> functionValues) {
        return isFunctionSpecifiedOrImplied(functionName) && !functionValues?.containsKey(functionName)
    }

    private boolean isFunctionSpecifiedOrImplied(String functionName) {
        // Ensure TOTAL is included if AVERAGE is included
        return functionName in metric.functions || (functionName == FunctionNames.TOTAL && metric.functions.contains(FunctionNames.AVERAGE))
    }

}