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

import org.gmetrics.metric.Metric

/**
 * Provides common static helper methods for classes that provides data and behavior for enabling reports
 * to filter the results included within a report based a filter map keyed on the metric name.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class MetricCriteriaFilterHelper {

    protected static boolean includesName(Map criteriaMap, Metric metric, String name) {
        if (criteriaMap == null) {
            return true
        }
        def matchingNames = criteriaMap[metric.name]
        return matchingNames == null || name in matchingNames
    }

    /**
     * Parse the criteria string
     * @param criteria - the String of the form <metric1-name>=<value1a>,<value1b>; <metric2-name>=<value2a>,<value2b>
     */
    protected static Map parseCriteria(String criteria) {
        assert criteria
        def criteriaMap = [:]
        def metricCriteriaStrings = criteria.tokenize(';')
        metricCriteriaStrings.each { metricCriteria -> parseCriteriaForSingleMetric(criteriaMap, metricCriteria) }
        return criteriaMap
    }

    protected static void parseCriteriaForSingleMetric(Map criteriaMap, String metricCriteria) {
        def tokens = metricCriteria.tokenize('=')
        assert tokens.size() == 2, "Each metric criteria must be of the form: <metric1-name>=<value1a>,<value1b> but was [$metricCriteria]"
        def name = tokens[0].trim()
        criteriaMap[name] = parseCommaSeparatedList(tokens[1])
    }

    private static List parseCommaSeparatedList(String values) {
        values.tokenize(',').collect { it.trim().toLowerCase() }
    }
}
