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
package org.gmetrics.metric.abc

import org.gmetrics.metric.AbstractMetricTest
import org.gmetrics.result.MetricResult
import org.gmetrics.result.MetricResult

/**
 * Abstract superclass for AbcMetric tests
 *
 * @author Chris Mair
 * @version $Revision: 234 $ - $Date: 2009-10-24 15:11:13 -0400 (Sat, 24 Oct 2009) $
 */
abstract class AbstractAbcMetricTest extends AbstractMetricTest {
    protected static final ZERO_VECTOR = [0, 0, 0]

    protected valueFromMetricResult(MetricResult metricResult) {
        return metricResult.abcVector
    }

}