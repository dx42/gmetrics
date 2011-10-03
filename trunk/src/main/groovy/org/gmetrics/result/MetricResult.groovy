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
package org.gmetrics.result

import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel

/**
 * Represents the result from applying a single metric (to a package, class or method)
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
interface MetricResult {

    /**
     * @return the Metric for which this object represents results.
     */

    Metric getMetric()

    /**
     * @return the MetricLevel for this metric result
     */
    MetricLevel getMetricLevel()

    /**
     * Return the count of the nodes/results that are descendants. For instance, if this result
     * represents a class, then the count includes all of the method-level result children
     * (if applicable for the metric).
     *
     * @return the count of metric result children
     */
    int getCount()

    /**
     * Return the metric result value for the named function (e.g. "average", "total")
     * @param propertyName - the function name
     * @return the named function value or null if that function is not supported
     */
    Object getAt(String propertyName)


    /**
     * Return the line number associated with this metric result (i.e., method or class)
     * @return an Integer; may be null
     */
    Integer getLineNumber()
}