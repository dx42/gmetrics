/*
 * Copyright 2008 the original author or authors.
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
package org.gmetrics.metric

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.control.SourceUnit
import org.gmetrics.source.SourceCode
import org.codehaus.groovy.ast.MethodNode

/**
 * Abstract superclass for Groovy AST Visitors
 *
 * @author Chris Mair
 */
abstract class AbstractAstVisitor extends ClassCodeVisitorSupport implements AstVisitor {
    public static final MAX_SOURCE_LINE_LENGTH = 60
    public static final SOURCE_LINE_LAST_SEGMENT_LENGTH = 12
    SourceCode sourceCode
    private Set visited = [] as Set

    /**
     * Return true if the AST expression has not already been visited. If it is
     * the first visit, register the expression so that the next visit will return false.
     * @param expression - the AST expression to check
     * @return true if the AST expression has NOT already been visited
     */
    protected isFirstVisit(expression) {
        if(visited.contains(expression)) {
            return false
        }
        visited << expression
        return true
    }

    /**
     * Return the source line corresponding to the specified AST node
     * @param node - the Groovy AST node
     */
    protected String sourceLine(ASTNode node) {
        return sourceCode.line(node.lineNumber-1)
    }

    protected SourceUnit getSourceUnit() {
        return source
    }

    protected boolean isSynthetic(ASTNode node) {
        return node.lineNumber == -1
    }

    protected boolean isNotSynthetic(ASTNode node) {
        return node.lineNumber != -1
    }

    protected boolean isSyntheticNonRunMethod(MethodNode methodNode) {
        return methodNode.lineNumber < 0 && methodNode.name != 'run'
    }

}