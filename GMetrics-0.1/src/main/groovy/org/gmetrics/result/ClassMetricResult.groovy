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

import org.gmetrics.result.MetricResult

/**
 * Represents the results for a single metric for a single class
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ClassMetricResult {
    MetricResult classMetricResult
    Map methodMetricResults = [:]

    ClassMetricResult(MetricResult metricResult, Map methodMetricResults=null) {
        this.classMetricResult = metricResult
        this.methodMetricResults = methodMetricResults
    }

    String toString() {
        return "ClassMetricResult[classMetricResult=$classMetricResult, methodMetricResults=$methodMetricResults]"
    }
}