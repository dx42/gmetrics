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
package org.gmetrics.metric.methodcount

import org.codehaus.groovy.ast.ClassNode
import org.gmetrics.metric.AbstractAstVisitor
import org.gmetrics.util.AstUtil

/**
 * AstVisitor for the MethodCountMetric.
 *
 * @author Chris Mair
 */
class MethodCountAstVisitor extends AbstractAstVisitor {

    private int numberOfMethods

    int getNumberOfMethods() {
        return numberOfMethods
    }

    void visitClass(ClassNode classNode) {
        numberOfMethods = 0
        if (classNode.lineNumber >= 0) {
            numberOfMethods += classNode.methods?.size() ?: 0
            numberOfMethods += classNode.declaredConstructors?.size() ?: 0
            numberOfMethods += classNode.fields.findAll { AstUtil.isClosureField(it) }.size()
        }
        super.visitClass(classNode)
    }

}