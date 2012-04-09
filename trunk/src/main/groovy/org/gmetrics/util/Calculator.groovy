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

/**
 * Contains static utility methods related to mathematical calculations
 *
 * @author Chris Mair
 */
class Calculator {

    static BigDecimal calculateAverage(BigDecimal sum, int count, int scale) {
        if(sum && count) {
            def result = sum / count
            return result.setScale(scale, BigDecimal.ROUND_HALF_UP)
        }
        return 0.0.setScale(scale, BigDecimal.ROUND_HALF_UP)
    }

    // Private constructor. All methods are static.
    private Calculator() { }
}
