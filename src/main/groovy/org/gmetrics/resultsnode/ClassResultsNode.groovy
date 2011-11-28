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
package org.gmetrics.resultsnode

import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.MetricResult
import org.gmetrics.metric.Metric
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.result.MethodKey

/**
 * Represents a node in the hierarchy of metric result nodes
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ClassResultsNode implements ResultsNode {

    final String name
    final MetricLevel level = MetricLevel.CLASS
    final Map<String, ResultsNode> children = [:]
    final List<MetricResult> metricResults = []

    ClassResultsNode(String name) {
        this.name = name
    }

    boolean containsClassResults() {
        return true
    }

    MetricResult getMetricResult(Metric metric) {
        assert metric
        return metricResults.find { metricResult -> metricResult.metric == metric }
    }

    void addClassMetricResult(ClassMetricResult classMetricResult) {
        if (classMetricResult) {
            metricResults << classMetricResult.classMetricResult

            def methodMetricResults = classMetricResult.getMethodMetricResults()
            methodMetricResults.each { k, v ->
                addMethodMetricResult(k, v)
            }
        }
    }

    String toString() {
        return "ClassResultsNode[$level: metricResults=$metricResults, children=$children]"
    }

    private void addMethodMetricResult(MethodKey methodKey, MetricResult metricResult) {
        if (children[methodKey] == null) {
            children[methodKey] = new MethodResultsNode(methodKey.methodName)
        }
        children[methodKey].addMetricResult(metricResult)
    }

}