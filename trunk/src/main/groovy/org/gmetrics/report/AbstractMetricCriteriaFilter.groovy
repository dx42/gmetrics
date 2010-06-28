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
 * Abstract superclass for filter classes that provides data and behavior for enabling reports
 * to filter the set of functions included in a report.
 *
 * @author Chris Mair
 * @version $Revision: 91 $ - $Date: 2010-03-05 20:21:49 -0500 (Fri, 05 Mar 2010) $
 */

abstract class AbstractMetricCriteriaFilter {

    private Map criteriaMap

    protected boolean includesName(Metric metric, String name) {
        if (criteriaMap == null) {
            return true
        }
        def matchingNames = criteriaMap[metric.name]
        return matchingNames && name in matchingNames
    }

    /**
     * Parse the criteria string
     * @param criteria - the String of the form <metric1-name>=<value1a>,<value1b>; <metric2-name>=<value2a>,<value2b>
     */
    protected void parseCriteria(String criteria) {
        assert criteria
        criteriaMap = [:]
        def metricCriteriaStrings = criteria.tokenize(';')
        metricCriteriaStrings.each { metricCriteria -> parseCriteriaForSingleMetric(metricCriteria) }
    }

    protected void parseCriteriaForSingleMetric(String metricCriteria) {
        def tokens = metricCriteria.tokenize('=')
        assert tokens.size() == 2, "Each metric criteria must be of the form: <metric1-name>=<value1a>,<value1b>"
        def name = tokens[0].trim()
        criteriaMap[name] = parseCommaSeparatedList(tokens[1])
    }

    private List parseCommaSeparatedList(String values) {
        values.tokenize(',').collect { it.trim().toLowerCase() }
    }
}