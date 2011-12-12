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
 package org.gmetrics.metric.coverage

import org.gmetrics.util.PathUtil
import org.codehaus.groovy.ast.MethodNode

/**
 * Provides utility methods to parse and compare Cobertura method signatures
 *
 * @author Chris Mair
 */
class CoberturaSignatureParser {

    private static final PRIMITIVES = [
        B:'byte',
        I:'int',
        C:'char',
        D:'double',
        F:'float',
        J:'long',
        S:'short',
        Z:'boolean' ]
    private static final PRIMITIVE_CODES = PRIMITIVES.keySet()

    static boolean matchesCoberturaMethod(String name, String signature, String coberturaName, String coberturaSignature) {
        if (name != coberturaName) {
            return false
        }
        def astTypes = parseSignatureParameterTypes(signature)
        def coberturaTypes = parseCoberturaSignatureParameterTypes(coberturaSignature)
        return astTypes == coberturaTypes
    }

    static boolean matchesCoberturaMethod(MethodNode methodNode, String coberturaName, String coberturaSignature) {
        return matchesCoberturaMethod(methodNode.name, methodNode.typeDescriptor, coberturaName, coberturaSignature)
    }

    protected static List<String> parseSignatureParameterTypes(String signature) {
        final REGEX = /.*\((.*)\).*/
        def parameterMatcher = signature =~ REGEX
        def parameterString = parameterMatcher[0][1]
        if (!parameterString) {
            return []
        }
        return parameterString.tokenize(',').collect { rawType ->
            def type = rawType.trim()
            if (type.startsWith('[')) {
                def arrayTypeCode = type.substring(1)
                PRIMITIVES[arrayTypeCode] + '[]'
            }
            else {
                classNameNoPackage(type)
            }
        }
    }

    private static String extractParameters(String signature) {
        final REGEX = /.*\((.*)\).*/
        def parameterMatcher = signature =~ REGEX
        return parameterMatcher[0][1]
    }

    protected static List<String> parseCoberturaSignatureParameterTypes(String signature) {
        def parameterString = extractParameters(signature)
        if (!parameterString) {
            return []
        }
        return parseParameterTypes(parameterString)
    }


    private static List parseParameterTypes(String parameterString) {
        def parameters = []
        def typeName = null
        boolean withinArray = false
        parameterString.each { c ->
            if (typeName != null) {
                if (c == ';') {
                    def suffix = withinArray ? '[]' : ''
                    parameters << PathUtil.getName(typeName.toString()) + suffix
                    typeName = null
                    withinArray = false
                }
                else {
                    typeName.append(c)
                }
            }
            else {
                if (c == '[') {
                    withinArray = true
                }

                if (c == 'L') {
                    typeName = new StringBuilder()
                }
                else if (c in PRIMITIVE_CODES) {
                    def suffix = withinArray ? '[]' : ''
                    parameters << PRIMITIVES[c] + suffix
                    withinArray = false
                }
            }
        }
        return parameters
    }

    private static String classNameNoPackage(String name) {
        def index = name.lastIndexOf('.')
        return index > -1 ? name.substring(index+1) : name
    }

    // Private constructor to prevent instantiation. All members are static.
    private CoberturaSignatureParser() { }
}
