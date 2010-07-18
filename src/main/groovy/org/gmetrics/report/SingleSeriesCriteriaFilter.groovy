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
 * <p/>
 * The <code>metric</code> property must specify the name (case-sensitive) of a single Metric (for example
 * "CyclomaticComplexity") included in the analysis results.
 * <p/>
 * The <code>level</code> property must be set to one of: "package", "class" or "method".
 * <p/>
 * The <code>function</code> property must be set to the name of a function supported by the
 * metric, typically one of: "total", "average", "minimum" or "maximum". 
 * <p/>
 * The <code>sort</code> property is optional, and if not <code>null</code>, must either have the value
 * of "ascending" or "descending", and causes the results to be sorted numerically in either ascending
 * or descending order.
 * <p/>
 * The <code>maxResults</code> property is optional. A value of <code>null</code> or <code>0</code> means
 * no limit. Otherwise, the value must be positive, and limits the number of results returned.
 * <p/>
 * The <code>greaterThan</code> property is optional. The value specifies a threshold -- only results
 * with a larger value are returned. A value of <code>null</code> means no threshold. 
 * <p/>
 * The <code>lessThan</code> property is optional. The value specifies a threshold -- only results
 * with a smaller value are returned. A value of <code>null</code> means no threshold.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class SingleSeriesCriteriaFilter {

    private static final VALID_SORT_VALUES = ['ascending', 'descending']

    String metric
    String level
    String function
    String sort
    String maxResults
    String greaterThan
    String lessThan

    List<SeriesValue> buildSeriesData(ResultsNode resultsNode, MetricSet metricSet) {
        assert metric
        assert level
        assert function

        assertMetricExists(metricSet)
        assertLevelExists()
        assertFunctionExists(metricSet)
        assertValidSortValue()
        assertValidMaxResultsValue()
        assertValidGreaterThanValue()
        assertValidLessThanValue()

        List matchingValues = []
        findMatchingValuesForChildren(resultsNode, null, matchingValues)
        def seriesValues = sortValuesIfApplicable(matchingValues)
        seriesValues = limitToGreaterThanIfApplicable(seriesValues)
        seriesValues = limitToLessThanIfApplicable(seriesValues)
        return limitToMaxResultsIfApplicable(seriesValues)
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

    private List sortValuesIfApplicable(List seriesValues) {
        if (sort == 'ascending') {
            return seriesValues.sort { v1, v2 -> v1.value <=> v2.value }
        }
        if (sort == 'descending') {
            return seriesValues.sort { v1, v2 -> v2.value <=> v1.value }
        }
        return seriesValues
    }

    private List limitToGreaterThanIfApplicable(List seriesValues) {
        if (greaterThan) {
            def greaterThanValue = greaterThan as BigDecimal
            return seriesValues.findAll { seriesValue -> seriesValue.value > greaterThanValue }
        }
        return seriesValues
    }

    private List limitToLessThanIfApplicable(List seriesValues) {
        if (lessThan) {
            def lessThanValue = lessThan as BigDecimal
            return seriesValues.findAll { seriesValue -> seriesValue.value < lessThanValue }
        }
        return seriesValues
    }

    private List limitToMaxResultsIfApplicable(List seriesValues) {
        if (maxResults) {
            def maxResultsInt = maxResults as int  
            if (maxResultsInt < seriesValues.size()) {
                return seriesValues[0..maxResultsInt-1]
            }
        }
        return seriesValues
    }

    private void assertMetricExists(MetricSet metricSet) {
        Metric m = findMetric(metricSet)
        assert m != null, "The metric named [$metric] does not exist"
    }

    private void assertLevelExists() {
        assert level in MetricLevel.names, "The level named [$level] does not exist"
    }

    private void assertFunctionExists(MetricSet metricSet) {
        Metric m = findMetric(metricSet)
        assert function in m.getFunctions(), "The function named [$function] does not exist for metric [$metric]"
    }

    private void assertValidSortValue() {
        assert sort == null || sort in VALID_SORT_VALUES, "The sort value named [$sort] is not one of $VALID_SORT_VALUES"
    }

    private void assertValidMaxResultsValue() {
        if (maxResults != null) {
            try {
                def value = Integer.parseInt(maxResults)
                assert value >= 0, "The maxResults value [$maxResults] must be null or greater than or equal to zero"
            }
            catch(NumberFormatException e) {
                throw new AssertionError("The maxResults value [$maxResults] must be null or greater than or equal to zero")
            }
        }
    }

    private void assertValidGreaterThanValue() {
        assertValidNumberValue(greaterThan, 'greaterThan')
    }

    private void assertValidLessThanValue() {
        assertValidNumberValue(lessThan, 'lessThan')
    }

    private void assertValidNumberValue(value, String name) {
        if (value != null) {
            try {
                new BigDecimal(value)
            }
            catch(NumberFormatException e) {
                throw new AssertionError("The $name value [$value] must be a valid number")
            }
        }
    }

    private Metric findMetric(MetricSet metricSet) {
        metricSet.metrics.find { m -> m.name == metric }
    }
}