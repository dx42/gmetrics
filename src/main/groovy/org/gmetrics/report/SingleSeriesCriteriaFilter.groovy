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

/**
 * Provides data and behavior for enabling reports to filter the results based on a single
 * metric, single level and single function to provide a single series of data.
 * This class is intended to be used as a Groovy @Mixin for ReportWriter classes.
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

        def matchingValues = []
        findMatchingValuesForChildren(resultsNode, matchingValues)
        return matchingValues
    }

    private void findMatchingValuesForChildren(ResultsNode resultsNode, List matchingValues) {
        resultsNode.children.each { childName, childResultsNode ->
            findMatchingValues(childResultsNode, childName, matchingValues)
        }
    }

    private void findMatchingValues(ResultsNode resultsNode, String name, List matchingValues) {
        def metricResults = resultsNode.getMetricResults()
        boolean matchesLevel = resultsNode.getLevel().getName().equalsIgnoreCase(level)
        if (matchesLevel) {
            def metricResult = metricResults.find { metricResult ->
                boolean matchesMetric = metricResult.getMetric().getName().equalsIgnoreCase(metric)
                boolean hasMatchingFunction = metricResult[function] 
                return matchesMetric && hasMatchingFunction
            }
            if (metricResult) {
                matchingValues << new SeriesValue(name, metricResult[function])
            }
        }
        findMatchingValuesForChildren(resultsNode, matchingValues)
    }
}