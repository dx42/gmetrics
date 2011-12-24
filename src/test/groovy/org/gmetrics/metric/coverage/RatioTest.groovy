/*
 * Copyright 2011 the original author or authors.
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
 package org.gmetrics.metric.coverage

import org.gmetrics.test.AbstractTestCase

/**
 * Tests for Ratio
 *
 * @author Chris Mair
 */
class RatioTest extends AbstractTestCase {

    void testConstructor_AssignsFields() {
        def ratio = new Ratio(6, 7)
        assertRatio(ratio, 6, 7)
    }

    void testZERO() {
        assertRatio(Ratio.ZERO, 0, 0)
    }

    void testPlus() {
        def ratio = new Ratio(6, 7)
        def sumRatio = ratio + new Ratio(5, 20)
        assertRatio(sumRatio, 11, 27)
    }

    void testPlus_NullRatio_ThrowsException() {
        def ratio = new Ratio(6, 7)
        shouldFailWithMessageContaining('ratio') { ratio + null  }
    }

    void testAsBigDecimal() {
        assert new Ratio(6, 8) as BigDecimal == 0.75
        assert new Ratio(1, 5) as BigDecimal == 0.2
        assert new Ratio(5, 5) as BigDecimal == 1.0
        assert new Ratio(0, 8) as BigDecimal == 0.0
    }

    void testToBigDecimal() {
        assert new Ratio(6, 8).toBigDecimal(2, BigDecimal.ROUND_HALF_UP) == 0.75
        assert new Ratio(6, 8).toBigDecimal(1, BigDecimal.ROUND_HALF_UP) == 0.8
        assert new Ratio(745, 1000).toBigDecimal(2, BigDecimal.ROUND_HALF_DOWN) == 0.74
        assert new Ratio(1, 5).toBigDecimal(1, BigDecimal.ROUND_HALF_UP) == 0.2
        assert new Ratio(5, 5).toBigDecimal(3, BigDecimal.ROUND_UNNECESSARY) == 1.000
        assert new Ratio(0, 8).toBigDecimal(3, BigDecimal.ROUND_HALF_DOWN) == 0.0
    }

    void testAsType_OtherThanBigDecimal_ThrowsException() {
        shouldFail { new Ratio(6, 8) as Integer }
    }

    private void assertRatio(Ratio ratio, int numerator, int denominator) {
        assert ratio.numerator == numerator
        assert ratio.denominator == denominator
    }

}
