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

/**
 * A NumberMetricResult that aggregates multiple values
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AggregateNumberMetricResult implements MetricResult {

    private count
    int scale = 1
    private functionValues = [:]
    final Metric metric

    AggregateNumberMetricResult(Metric metric, Collection children) {
        assert metric != null
        assert children != null
        this.metric = metric
        def sum = children.inject(0) { value, child -> value + child['total'] }
        count = children.inject(0) { value, child -> value + child.count }
        functionValues['total'] = sum
        functionValues['average'] = calculateAverage(sum, count)
        functionValues['minimum'] = calculateMinimum(children)
        functionValues['maximum'] = calculateMaximum(children)
    }

    int getCount() {
        return count
    }

    Object getAt(String name) {
        return functionValues[name]
    }

    private Object calculateMinimum(children) {
        def minChild = children.min { child -> child['minimum'] }
        return minChild != null ? minChild['minimum'] : 0
    }

    private Object calculateMaximum(children) {
        def maxChild = children.max { child -> child['maximum'] }
        return maxChild != null ? maxChild['maximum'] : 0
    }

    private Object calculateAverage(sum, count) {
        if(sum && count) {
            def result = sum / count
            return result.setScale(scale, BigDecimal.ROUND_HALF_UP)
        }
        else {
            return 0
        }
    }

    List getFunctionNames() {
        ['total', 'average']
    }
    
    String toString() {
        "AggregateNumberMetricResult[count=$count, $functionValues}]"
    }

}