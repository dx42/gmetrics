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
 package org.gmetrics.formatter

/**
 * Formatter that formats a number as a percentage, and appends a '%'
 *
 * @author Chris Mair
 */
class PercentageFormatter implements Formatter {

    String format(Object value) {
        if (value == null) {
            return null
        }
        assert value instanceof Number, "The value must be a Number, but was a ${value.getClass().name}"

        def percentage = value * 100 as BigDecimal
        def integerPercentage = percentage.setScale(0, BigDecimal.ROUND_HALF_UP)
        return integerPercentage.toString() + '%'
    }
}
