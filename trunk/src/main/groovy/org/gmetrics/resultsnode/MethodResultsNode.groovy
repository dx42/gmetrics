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

/**
 * Represents a method result node in the hierarchy of metric result nodes
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class MethodResultsNode implements ResultsNode {
    final MetricLevel level = MetricLevel.METHOD
    final List metricResults = []

    boolean containsClassResults() {
        return false
    }
    
    MetricResult getMetricResult(Metric metric) {
        assert metric
        return metricResults.find { metricResult -> metricResult.metric == metric }
    }

    void addMetricResult(MetricResult metricResult) {
        assert metricResult
        metricResults << metricResult
    }

    Map getChildren() {
        return Collections.EMPTY_MAP
    }

    String toString() {
        return "MethodResultsNode[metricResults=$metricResults]"
    }
}