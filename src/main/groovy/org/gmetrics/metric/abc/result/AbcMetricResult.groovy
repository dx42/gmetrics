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
package org.gmetrics.metric.abc.result

import org.gmetrics.result.MetricResult
import org.gmetrics.metric.Metric
import org.gmetrics.metric.abc.AbcVector
import org.gmetrics.metric.MetricLevel

/**
 * A MetricResult specifically for the ABC metric
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbcMetricResult implements MetricResult {

    final AbcVector abcVector
    final Metric metric
    final MetricLevel metricLevel
    final Integer lineNumber
    final int count = 1
    private magnitude

    AbcMetricResult(Metric metric, MetricLevel metricLevel, AbcVector abcVector, Integer lineNumber=null) {
        assert abcVector
        assert metricLevel
        this.metricLevel = metricLevel
        this.abcVector = abcVector
        this.metric = metric
        this.magnitude = abcVector.magnitude
        this.lineNumber = lineNumber
    }

    Object getAt(String name) {
        return name in metric.functions ? magnitude : null
    }

    String toString() {
        "AbcMetricResult[$abcVector, vector=$abcVector, magnitude=$magnitude]"
    }

}