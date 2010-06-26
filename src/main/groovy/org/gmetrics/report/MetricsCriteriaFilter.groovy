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
 * Provides data and behavior for enabling reports to filter the set of metrics included in a report.
 * This class is intended to be used as a Groovy @Mixin for ReportWriter classes.
 *
 * @author Chris Mair
 * @version $Revision: 91 $ - $Date: 2010-03-05 20:21:49 -0500 (Fri, 05 Mar 2010) $
 */

class MetricsCriteriaFilter {

    private Collection metricNames

    void setMetrics(String metricNames) {
        this.metricNames = metricNames.tokenize(',').collect { it.trim() }
    }

    boolean includesMetric(Metric metric) {
        return metricNames == null ? true : metric.name in metricNames
    }
}