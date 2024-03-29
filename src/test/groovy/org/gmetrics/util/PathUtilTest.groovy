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
import org.junit.jupiter.api.Test

/**
 * Tests for PathUtil
 *
 * @author Chris Mair
 */
class PathUtilTest extends AbstractTestCase {

    @Test
	void testNormalize() {
        assert PathUtil.normalize(null) == null
        assert PathUtil.normalize('') == ''
        assert PathUtil.normalize('abc') == 'abc'
        assert PathUtil.normalize('abc/def') == 'abc/def'
        assert PathUtil.normalize('/abc/def/ghi') == '/abc/def/ghi'
        assert PathUtil.normalize('c:\\abc\\def') == 'c:/abc/def'
        assert PathUtil.normalize('/abc\\def/ghi\\') == '/abc/def/ghi/'
    }

    @Test
	void testGetName() {
        assert PathUtil.getName(null) == null
        assert PathUtil.getName('') == ''
        assert PathUtil.getName('abc') == 'abc'
        assert PathUtil.getName('/abc') == 'abc'
        assert PathUtil.getName('abc/def') == 'def'
        assert PathUtil.getName('abc\\def\\ghi') == 'ghi'
    }

    @Test
	void testGetParent() {
        assert PathUtil.getParent(null) == null
        assert PathUtil.getParent('') == null
        assert PathUtil.getParent('abc') == null
        assert PathUtil.getParent('/abc') == null
        assert PathUtil.getParent('abc/def') == 'abc'
        assert PathUtil.getParent('abc\\def\\ghi') == 'abc/def'
    }

    @Test
	void testToPackageName() {
        assert PathUtil.toPackageName(null) == null
        assert PathUtil.toPackageName('') == null
        assert PathUtil.toPackageName('abc') == 'abc'
        assert PathUtil.toPackageName('/abc') == 'abc'
        assert PathUtil.toPackageName('abc/def') == 'abc.def'
        assert PathUtil.toPackageName('abc/def/') == 'abc.def'
        assert PathUtil.toPackageName('/abc/def') == 'abc.def'
        assert PathUtil.toPackageName('\\abc\\def') == 'abc.def'
        assert PathUtil.toPackageName('abc\\def\\ghi') == 'abc.def.ghi'
    }

}