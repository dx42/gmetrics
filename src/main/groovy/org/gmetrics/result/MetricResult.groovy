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
 * Represents the result from applying a single metric (to a package, class or method)
 *
 * @author Chris Mair
 * @version $Revision: 230 $ - $Date: 2009-10-10 13:16:19 -0400 (Sat, 10 Oct 2009) $
 */
interface MetricResult {

    Metric getMetric()
    int getCount()
    Object getTotal()
    Object getAverage()
}