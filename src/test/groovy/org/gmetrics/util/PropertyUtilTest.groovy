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
package org.gmetrics.util

import org.gmetrics.test.AbstractTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for PropertyUtil
 *
 * @author Chris Mair
 */
class PropertyUtilTest extends AbstractTestCase {

    private object

    @Test
	void testSetPropertyFromString_String() {
        PropertyUtil.setPropertyFromString(object, 'stringField', 'AbcdefGHI')
        assert object.getStringField() == 'AbcdefGHI'
    }

    @Test
	void testSetPropertyFromString_int() {
        PropertyUtil.setPropertyFromString(object, 'intField', '23456')
        assert object.getIntField() == 23456
    }

    @Test
	void testSetPropertyFromString_long() {
        PropertyUtil.setPropertyFromString(object, 'longField', '9999999999')
        assert object.getLongField() == 9999999999
    }

    @Test
	void testSetPropertyFromString_boolean() {
        PropertyUtil.setPropertyFromString(object, 'booleanField', 'true')
        assert object.getBooleanField()

        PropertyUtil.setPropertyFromString(object, 'booleanField', 'false')
        assert !object.getBooleanField()
    }

    @Test
	void testSetPropertyFromString_intFromSuperclass() {
        PropertyUtil.setPropertyFromString(object, 'superclassIntField', '23456')
        assert object.getSuperclassIntField() == 23456
    }

    @Test
	void testSetPropertyFromString_NoSuchField() {
        shouldFail(NoSuchFieldException) { PropertyUtil.setPropertyFromString(object, 'XXX', '23456') }
    }

    @BeforeEach
    void setUp() {
        object = new SamplePropertyUtilClass()
    }
}

class SamplePropertyUtilClass extends SamplePropertyUtilSuperclass {
    String stringField
    int intField
    long longField
    boolean booleanField
}

class SamplePropertyUtilSuperclass {
    int superclassIntField
}