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
 package org.gmetrics.result

import org.codehaus.groovy.ast.MethodNode

/**
 * Serves as a key in the Map of method --> MetricResult. This encapsulates the unique method information
 * that enables distinguishing between multiple overridden methods (same name, different parameters).
 *
 * @author Chris Mair
 */
class MethodKey {

    final methodName
    final signature
    private final comparableString

    MethodKey(String methodName) {
        assert methodName
        this.methodName = methodName
        this.comparableString = methodName
    }

    MethodKey(MethodNode methodNode) {
        assert methodNode
        this.methodName = methodNode.name
        this.signature = methodNode.typeDescriptor
        this.comparableString = methodNode.typeDescriptor
    }

    @Override
    boolean equals(Object object) {
        return object instanceof MethodKey && object.comparableString == comparableString
    }

    @Override
    int hashCode() {
        return comparableString.hashCode()
    }

    @Override
    String toString() {
        return '{' + comparableString + '}'
    }


}
