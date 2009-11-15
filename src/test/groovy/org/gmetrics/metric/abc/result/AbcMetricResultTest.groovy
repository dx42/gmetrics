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

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.abc.result.AbcMetricResult
import org.gmetrics.metric.abc.AbcMetric
import org.gmetrics.metric.abc.AbcVector
import org.gmetrics.metric.abc.AbcTestUtil

/**
 * Tests for AbcMetricResult
 *
 * @author Chris Mair
 * @version $Revision: 237 $ - $Date: 2009-11-09 21:17:54 -0500 (Mon, 09 Nov 2009) $
 */
class AbcMetricResultTest extends AbstractTestCase {
    private static final METRIC = new AbcMetric()

    void testPassingNullAbcVectorIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('abcVector') { new AbcMetricResult(METRIC, null) }
    }

    void testValueForEmptyVectorSetIsZero() {
        assert abcMetricResultValue(0, 0, 0) == 0
    }

    void testVectorWithIntegerResultValue() {
        assert abcMetricResultValue(1, 2, 2) == 3
    }

    void testVectorWithNonIntegerResultValue() {
        assert abcMetricResultValue(7, 1, 2) == 7.3
    }

    void testValueIsSameAsAbcVectorMagnitude() {
        assert abcMetricResultValue(6, 7, 8) == new AbcVector(6, 7, 8).magnitude
    }

    void testAverageAndTotalValueAreTheSameAsTheValue() {
        def abc = AbcTestUtil.abcMetricResult(METRIC, 1, 2, 3)
        assert abc.totalValue == abc.value
        assert abc.averageValue == abc.value
    }

    private abcMetricResultValue(int a, int b, int c) {
        def abcMetricResult = AbcTestUtil.abcMetricResult(METRIC, a, b, c)
        def value = abcMetricResult.value
        log(abcMetricResult)
        return value
    }
    
}