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

import org.gmetrics.metric.Metric
import org.gmetrics.metric.abc.result.AbcMetricResult

/**
 * Utility methods for ABC test classes
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbcTestUtil {
    protected static final ZERO_VECTOR = [0, 0, 0]

    static void assertEquals(AbcVector abcVector, List expectedValues) {
        def actualValues = [abcVector.assignments, abcVector.branches, abcVector.conditions]
        assert actualValues == expectedValues
    }

    static AbcMetricResult abcMetricResult(Metric metric, int a, int b, int c) {
        def abcVector = new AbcVector(a, b, c)
        return new AbcMetricResult(metric, abcVector)
    }
}