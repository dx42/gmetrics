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

class StubMetricResult implements MetricResult {

    private static final FUNCTION_NAMES = ['total', 'average', 'minimum', 'maximum']
    Metric metric
    MetricLevel metricLevel
    int count
    Object total, average, minimum, maximum
    Object value
    Integer lineNumber

    Object getAt(String propertyName) {
        return (propertyName in FUNCTION_NAMES) ? super.getAt(propertyName) : null        
    }

    String toString() {
        "StubMetricResult[count=$count, total=$total, average=$average]"
    }
}