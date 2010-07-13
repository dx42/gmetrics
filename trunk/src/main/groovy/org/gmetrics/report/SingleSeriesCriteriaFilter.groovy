/*
 * Copyright 2010 the original author or authors.
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
package org.gmetrics.report

import org.gmetrics.metricset.MetricSet
import org.gmetrics.resultsnode.ResultsNode
import org.gmetrics.metric.MetricLevel
import org.gmetrics.metric.Metric

/**
 * Provides data and behavior for enabling reports to filter the results based on a single
 * metric, single level and single function to provide a single series of data.
 * This class is intended to be used as a Groovy @Mixin for ReportWriter classes.
 * <p/>
 * The <code>metric</code>, <code>level</code> and <code>function</code> properties are required (must
 * be non-null and non-empty). These three properties uniquely identify a single series of metric values.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class SingleSeriesCriteriaFilter {

    String metric
    String level
    String function

    List<SeriesValue> buildSeriesData(ResultsNode resultsNode, MetricSet metricSet) {
        assert metric
        assert level
        assert function

        assertMetricExists(metricSet)
        assertLevelExists()
        assertFunctionExists(metricSet)

        def matchingValues = []
        findMatchingValuesForChildren(resultsNode, null, matchingValues)
        return matchingValues
    }

    private void findMatchingValuesForChildren(ResultsNode resultsNode, String parentName, List matchingValues) {
        resultsNode.children.each { childName, childResultsNode ->
            String fullChildName = (childResultsNode.level == MetricLevel.METHOD) ? "${parentName}.${childName}" : childName
            findMatchingValues(childResultsNode, fullChildName, matchingValues)
        }
    }

    private void findMatchingValues(ResultsNode resultsNode, String name, List matchingValues) {
        def metricResults = resultsNode.getMetricResults()
        boolean matchesLevel = resultsNode.getLevel().getName().equals(level)
        if (matchesLevel) {
            def metricResult = metricResults.find { metricResult ->
                boolean matchesMetric = metricResult.getMetric().getName().equals(metric)
                boolean hasMatchingFunction = metricResult[function]
                return matchesMetric && hasMatchingFunction
            }
            if (metricResult) {
                matchingValues << new SeriesValue(name, metricResult[function])
            }
        }
        findMatchingValuesForChildren(resultsNode, name, matchingValues)
    }

    // TODO Got a weird StackOverflowError if the following methods were not public. Groovy 1.6.0

    void assertMetricExists(MetricSet metricSet) {
        Metric m = findMetric(metricSet)
        assert m != null, "The metric named [$metric] does not exist"
    }

    void assertLevelExists() {
        assert level in MetricLevel.NAMES, "The level named [$level] does not exist"
    }

    void assertFunctionExists(MetricSet metricSet) {
        Metric m = findMetric(metricSet)
        assert function in m.getFunctions(), "The function named [$function] does not exist for metric [$metric]"
    }

    Metric findMetric(MetricSet metricSet) {
        metricSet.metrics.find { m -> m.name == metric }
    }
}