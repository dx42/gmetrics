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
 * Tests for FormatterFactory
 *
 * @author Chris Mair
 */
class FormatterFactoryTest extends AbstractTestCase {

    private FormatterFactory factory = new FormatterFactory()

    @Test
	void testGetFormatter() {
        assert factory.getFormatter('org.gmetrics.formatter.ToStringFormatter') instanceof ToStringFormatter
    }

    @Test
	void testGetFormatter_Null_ThrowsException() {
        shouldFailWithMessageContaining('formatter') { factory.getFormatter(null) }
    }

    @Test
	void testGetFormatter_NotAFormatter_ThrowsException() {
        shouldFailWithMessageContaining(Formatter.name) { factory.getFormatter('java.lang.Object') }
    }

    @Test
	void testGetFormatter_NotAClassName_ThrowsClassNotFoundException() {
        shouldFail(ClassNotFoundException) { factory.getFormatter('xxx') }
    }

}
