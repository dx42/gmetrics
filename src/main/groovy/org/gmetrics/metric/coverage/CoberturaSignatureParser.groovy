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

    static int numberOfParameters(String coberturaSignature) {
        return parseCoberturaSignatureParameterTypes(coberturaSignature).size()
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    private static List<String> parseSignatureParameterTypes(String signature) {
        def parameterString = extractParameters(signature)
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


    private static class ParseContext {
        final parameters = []
        private typeName = null
        private withinArray = false

        void startFullyQualifiedTypeName() {
            typeName = new StringBuilder()
        }

        boolean withinFullyQualifiedTypeName() {
            return typeName != null
        }

        void appendToFullyQualifiedTypeName(String c) {
            typeName.append(c)
        }

        void terminateFullyQualifiedTypeName() {
            def suffix = withinArray ? '[]' : ''
            parameters << PathUtil.getName(typeName.toString()) + suffix
            typeName = null
            withinArray = false
        }

        void startNewArrayType() {
            withinArray = true
        }

        void processPrimitiveTypeCode(String c) {
            def suffix = withinArray ? '[]' : ''
            parameters << PRIMITIVES[c] + suffix
            withinArray = false
        }
    }

    private static List parseParameterTypes(String parameterString) {
        def parseContext = new ParseContext()
        parameterString.each { c ->
            if (parseContext.withinFullyQualifiedTypeName()) {
                processCharacterWithinFullyQualifiedTypeName(c, parseContext)
            }
            else {
                processStandaloneCharacter(c, parseContext)
            }
        }
        return parseContext.parameters
    }

    private static void processStandaloneCharacter(String c, ParseContext parseContext) {
        if (c == '[') {
            parseContext.startNewArrayType()
        }
        else if (c == 'L') {
            parseContext.startFullyQualifiedTypeName()
        }
        else if (c in PRIMITIVE_CODES) {
            parseContext.processPrimitiveTypeCode(c)
        }
    }

    private static void processCharacterWithinFullyQualifiedTypeName(String c, ParseContext parseContext) {
        if (c == ';') {
            parseContext.terminateFullyQualifiedTypeName()
        }
        else {
            parseContext.appendToFullyQualifiedTypeName(c)
        }
    }

    private static String classNameNoPackage(String name) {
        def index = name.lastIndexOf('.')
        return index > -1 ? name.substring(index+1) : name
    }

    // Private constructor to prevent instantiation. All members are static.
    private CoberturaSignatureParser() { }
}
