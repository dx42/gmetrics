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
 * Tests for ClassNameUtil
 *
 * @author Chris Mair
 */
class ClassNameUtilTest extends AbstractTestCase {

    void testParentPackageName() {
        assert ClassNameUtil.parentPackageName(null) == null
        assert ClassNameUtil.parentPackageName('') == null
        assert ClassNameUtil.parentPackageName('abc') == null
        assert ClassNameUtil.parentPackageName('abc.def') == 'abc'
        assert ClassNameUtil.parentPackageName('abc.def.MyClass') == 'abc.def'
    }

    void testIsPackageName() {
        assert !ClassNameUtil.isPackageName(null)
        assert !ClassNameUtil.isPackageName('')
        assert ClassNameUtil.isPackageName('abc')
        assert ClassNameUtil.isPackageName('abc.def')
        assert !ClassNameUtil.isPackageName('abc.def.MyClass')
        assert !ClassNameUtil.isPackageName('abc.def.MyClass.CONSTANT')
    }

    void testIsClassName() {
        assert !ClassNameUtil.isClassName(null)
        assert !ClassNameUtil.isClassName('')
        assert !ClassNameUtil.isClassName('abc')
        assert !ClassNameUtil.isClassName('abc.def')
        assert !ClassNameUtil.isClassName('abc.someValue')
        assert ClassNameUtil.isClassName('abc.def.MyClass')
        assert !ClassNameUtil.isClassName('abc.def.MyClass.CONSTANT')
        assert !ClassNameUtil.isClassName('abc.def.MyClass.count')
        assert !ClassNameUtil.isClassName('abc.def.MyClass.someValue')
    }


}