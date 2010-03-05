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
import org.gmetrics.result.MetricResult

/**
 * Stub ResultsNode implementation for testing
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
public class StubResultsNode implements ResultsNode {

    MetricLevel level = MetricLevel.CLASS
    boolean containsClassResults = false
    List metricResults = []
    Map children = [:]
    String path

    boolean containsClassResults() {
        return containsClassResults
    }

    MetricResult getMetricResult(Metric metric) {
        return metricResults.find { metricResult -> metricResult.metric == metric }
    }

    String toString() {
        def pathString = path ? "path=$path, ": ""
        "StubResultsNode[${pathString}level=$level, metricResults=$metricResults, children=$children]"
    }
}