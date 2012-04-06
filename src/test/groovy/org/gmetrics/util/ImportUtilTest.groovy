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

import org.codenarc.source.SourceString
import org.gmetrics.test.AbstractTestCase

/**
 * Tests for ImportUtil
 *
 * @author Chris Mair
 */
class ImportUtilTest extends AbstractTestCase {

    void testPackageNameForImport() {
        final SOURCE = '''
            import aaa.b.MyClass
            import bbb.c.MyClass as Boo
            import ccc.d.*
            import static ddd.e.MyOtherClass.VALUE
            import static eee.f.MyOtherClass.*;
        '''
        def sourceCode = new SourceString(SOURCE)
        def ast = sourceCode.ast
        assert getPackageNameForImport(ast, 'aaa') == 'aaa.b'
        assert getPackageNameForImport(ast, 'bbb') == 'bbb.c'
        assert getPackageNameForImport(ast, 'ccc') == 'ccc.d'
        assert getPackageNameForImport(ast, 'ddd') == 'ddd.e'
        assert getPackageNameForImport(ast, 'eee') == 'eee.f'
    }

    private String getPackageNameForImport(ast, String text) {
        def allImports = ast.imports + ast.starImports + ast.staticImports.values() + ast.staticStarImports.values()
        def importNode = allImports.find { imp -> imp.packageName?.contains(text) || imp.className?.contains(text) }
        log(importNode?.text)
        assert importNode, "for $text"
        return ImportUtil.packageNameForImport(importNode)
    }

}