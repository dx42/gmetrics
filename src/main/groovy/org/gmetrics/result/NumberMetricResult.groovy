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

/**
 * A MetricResult for numbers (integers, BigDecimals, etc.)
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class NumberMetricResult implements MetricResult {

    final Metric metric
    final number
    final Integer lineNumber

    NumberMetricResult(Metric metric, number, Integer lineNumber=null) {
        assert metric != null
        assert number != null
        this.metric = metric
        this.number = number
        this.lineNumber = lineNumber
    }

    int getCount() {
        return 1
    }

    Object getAt(String name) {
        return name in metric.functions ? number : null
    }

    String toString() {
        "NumberMetricResult[metric=${metric.name}, $number]"
    }

}