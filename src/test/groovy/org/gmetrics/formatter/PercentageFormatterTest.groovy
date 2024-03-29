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

import org.gmetrics.test.AbstractTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for PercentageFormatter
 *
 * @author Chris Mair
 */
class PercentageFormatterTest extends AbstractTestCase {

    private PercentageFormatter formatter = new PercentageFormatter()

    @Test
	void testImplementsFormatter() {
        assert formatter instanceof Formatter
    }

    @Test
	void testFormat_NotANumber_ThrowsException() {
        shouldFailWithMessageContaining('Number') { formatter.format('abc') }
    }

    @Test
	void testFormat_Zero() {
        assert formatter.format(0.0) == '0%'
    }

    @Test
	void testFormat_ZeroInteger() {
        assert formatter.format(0) == '0%'
    }

    @Test
	void testFormat_Decimal() {
        assert formatter.format(0.67) == '67%'
    }

    @Test
	void testFormat_SetsDefaultScaleToConvertToIntegerPercentage() {
        assert formatter.format(0.671234) == '67%'
    }

    @Test
	void testFormat_Integer() {
        assert formatter.format(123) == '12300%'
    }

    @Test
	void testFormat_Null_ReturnsNull() {
        assert formatter.format(null) == null
    }

}
