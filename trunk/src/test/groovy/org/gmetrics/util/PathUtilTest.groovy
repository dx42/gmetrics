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

/**
 * Tests for PathUtil
 *
 * @author Chris Mair
 * @version $Revision: 113 $ - $Date: 2009-03-31 19:41:25 -0400 (Tue, 31 Mar 2009) $
 */
class PathUtilTest extends AbstractTestCase {

    void testNormalize() {
        assert PathUtil.normalize(null) == null
        assert PathUtil.normalize('') == ''
        assert PathUtil.normalize('abc') == 'abc'
        assert PathUtil.normalize('abc/def') == 'abc/def'
        assert PathUtil.normalize('/abc/def/ghi') == '/abc/def/ghi'
        assert PathUtil.normalize('c:\\abc\\def') == 'c:/abc/def'
        assert PathUtil.normalize('/abc\\def/ghi\\') == '/abc/def/ghi/'
    }

    void testGetName() {
        assert PathUtil.getName(null) == null
        assert PathUtil.getName('') == ''
        assert PathUtil.getName('abc') == 'abc'
        assert PathUtil.getName('/abc') == 'abc'
        assert PathUtil.getName('abc/def') == 'def'
        assert PathUtil.getName('abc\\def\\ghi') == 'ghi'
    }

    void testGetParent() {
        assert PathUtil.getParent(null) == null
        assert PathUtil.getParent('') == null
        assert PathUtil.getParent('abc') == null
        assert PathUtil.getParent('/abc') == null
        assert PathUtil.getParent('abc/def') == 'abc'
        assert PathUtil.getParent('abc\\def\\ghi') == 'abc/def'
    }

}