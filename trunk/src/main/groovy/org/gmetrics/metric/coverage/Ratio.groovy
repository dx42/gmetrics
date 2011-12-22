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

/**
 * Represents a simple ratio of X / Y
 *
 * @author Chris Mair
 */
class Ratio {

    final int numerator
    final int denominator

    Ratio(int numerator, int denominator) {
        this.numerator = numerator
        this.denominator = denominator
    }

    Ratio plus(Ratio ratio) {
        assert ratio
        new Ratio(numerator + ratio.numerator, denominator + ratio.denominator)
    }

    BigDecimal toBigDecimal(int scale, int roundingMode) {
        def bd = new BigDecimal(numerator) / new BigDecimal(denominator)
        return bd.setScale(scale, roundingMode)
    }

    Object asType(Class theClass) {
        assert theClass == BigDecimal
        return new BigDecimal(numerator) / new BigDecimal(denominator)
    }

    @Override
    String toString() {
        return numerator + '/' + denominator
    }


}
