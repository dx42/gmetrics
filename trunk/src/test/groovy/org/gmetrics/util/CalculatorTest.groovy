/*
 * Copyright 2012 the original author or authors.
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
package org.gmetrics.util

import org.gmetrics.test.AbstractTestCase

/**
 * Tests for Calculator
 *
 * @author Chris Mair
 */
class CalculatorTest extends AbstractTestCase {

    void testCalculateAverage() {
        assert Calculator.calculateAverage(null, 0, 2) == 0.00
        assert Calculator.calculateAverage(0.0, 0, 2) == 0.00
        assert Calculator.calculateAverage(10.0, 0, 2) == 0.00
        assert Calculator.calculateAverage(0, 10, 2) == 0.00

        assert Calculator.calculateAverage(10, 5, 2) == 2.00
        assert Calculator.calculateAverage(100.000, 200, 2) == 0.50
        assert Calculator.calculateAverage(10, 1000, 2) == 0.01
        assert Calculator.calculateAverage(1, 200, 2) == 0.01       // round half up
        assert Calculator.calculateAverage(1, 250, 2) == 0.00       // round half down

    }

    void testCalculateAverage_Scale() {
        assert Calculator.calculateAverage(0.0, 10, 2).scale == 2
        assert Calculator.calculateAverage(1.0, 4, 4).scale == 4
    }
}