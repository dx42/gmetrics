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
package org.gmetrics.metric.abc.result

import org.gmetrics.result.MetricResult
import org.gmetrics.metric.Metric
import org.gmetrics.metric.abc.AbcVector

/**
 * An aggregate MetricResult implementation specifically for the ABC Metric.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AggregateAbcMetricResult implements MetricResult {

    private count
    final Metric metric
    private assignmentSum = 0
    private branchSum = 0
    private conditionSum = 0
    final Object value = null
    private functionValues = [:]

    AggregateAbcMetricResult(Metric metric, Collection children) {
        assert metric != null
        assert children != null
        this.metric = metric
        children.each { child ->
            def abcVector = child.abcVector
            assignmentSum += abcVector.assignments
            branchSum += abcVector.branches
            conditionSum += abcVector.conditions
        }
        count = children.inject(0) { value, child -> value + child.count }
        functionValues['total'] = getTotalAbcVector().getMagnitude()
        functionValues['average'] = getAverageAbcVector().getMagnitude()
    }

    int getCount() {
        return count
    }

    /**
     * Return the sum of this set of ABC vectors. Each component (A,B,C) of the result
     * is summed separately. The formula for each component is:
     *      A1 + A2 + .. AN
     * and likewise for B and C values.
     */
    Object getTotalAbcVector() {
        return new AbcVector(assignmentSum, branchSum, conditionSum)
    }

    Object getAbcVector() {
        return getTotalAbcVector()
    }

    /**
     * Return the average of this set of ABC vectors. Each component (A,B,C) of the result
     * is calculated and averaged separately. The formula for each component is:
     *      (A1 + A2 + .. AN) / N
     * and likewise for B and C values. Each component of the result vector is rounded down to an integer.
     */
    Object getAverageAbcVector() {
        def a = average(assignmentSum, count)
        def b = average(branchSum, count)
        def c = average(conditionSum, count)
        return new AbcVector(a, b, c)
    }

    Object getAt(String name) {
        return functionValues[name]
    }

    List getFunctionNames() {
        ['total', 'average']
    }
    
    String toString() {
        "AggregateAbcMetricResult[count=$count, A=$assignmentSum, B=$branchSum, C=$conditionSum]"
    }

    private average(int sum, int count) {
        if (sum && count) {
            def rawAverage = sum / count
            def rounded = rawAverage.setScale(0, BigDecimal.ROUND_HALF_UP)
            return rounded as Integer
        }
        return 0
    }

}