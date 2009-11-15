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

/**
 * A MetricResult specifically for the ABC metric
 *
 * @author Chris Mair
 * @version $Revision: 232 $ - $Date: 2009-10-20 20:45:35 -0400 (Tue, 20 Oct 2009) $
 */
class AbcMetricResult implements MetricResult {
    final AbcVector abcVector
    final Metric metric

    AbcMetricResult(Metric metric, AbcVector abcVector) {
        assert abcVector
        this.abcVector = abcVector
        this.metric = metric
    }

    /**
     * @return the magnitude of the ABC vector as a BigDecimal with scale of 1
     * @see AbcVector#getMagnitude()
     */
    Object getValue() {
        return abcVector.magnitude
    }

    Object getTotalValue() {
        return getValue()
    }

    int getCount() {
        return 1
    }

    Object getAverageValue() {
        return getValue()
    }

    String toString() {
        "AbcMetricResult[$abcVector, value=${getValue()}]"
    }

}