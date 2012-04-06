/*
 * Copyright 2011 the original author or authors.
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

import org.codehaus.groovy.ast.ImportNode

/**
 * Contains static utility methods and constants related to Import statements.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
 */
class ImportUtil {

    /**
     * Return the package name for the specified import statement or else an empty String
     * @param importNode - the ImportNode for the import
     * @return the name package being imported (i.e., the import minus the class name/spec)
     *      or an empty String if the import contains no package component
     */
    static String packageNameForImport(ImportNode importNode) {
        if (importNode.className) {
            def importClassName = importNode.className
            def index = importClassName.lastIndexOf('.')
            (index == -1) ? '' : importClassName[0..index - 1]
        }
        else {
            def packageName = importNode.packageName
            packageName.endsWith('.') ? packageName[0..-2] : packageName
        }
    }

}
