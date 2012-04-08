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
 * Static utility methods for dealing with class and package names
 *
 * @author Chris Mair
 */
class ClassNameUtil {

    private static final UPPER_CASE = ~/[A-Z].*/

    static String parentPackageName(String typeName) {
        if (typeName?.contains('.')) {
            def lastPeriod = typeName.lastIndexOf('.')
            return typeName[0..lastPeriod-1]
        }
        null
    }

    static boolean isPackageName(String name) {
        def lastPart = getNameOnly(name)
        return lastPart ? !isCapitalized(lastPart) : false
    }

    static boolean isClassName(String fullName) {
        def name = getNameOnly(fullName)
        return isCapitalized(name) && isPackageName(parentPackageName(fullName))
    }

    private static String getNameOnly(String packageName) {
        if (packageName?.contains('.')) {
            def lastPeriod = packageName.lastIndexOf('.')
            return packageName[lastPeriod+1..-1]
        }
        return packageName ?: null
    }

    private static boolean isCapitalized(String name) {
        return name ? UPPER_CASE.matcher(name).matches() : false
    }

    /**
     * Private constructor. All Methods are static.
     */
    private ClassNameUtil() { }
}
