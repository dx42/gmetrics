/*
 * Copyright 2010 the original author or authors.
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
package org.gmetrics.metric.cyclomatic

import org.codehaus.groovy.ast.MethodNode
import org.gmetrics.source.SourceCode
import org.gmetrics.metric.AbstractMethodMetric

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.gmetrics.result.MetricResult
import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.SingleNumberMetricResult

/**
 * Metric for counting the (McCabe) Cyclomatic Complexity for methods and closure fields.
 *
 * The counting rules for Groovy:
 * <pre>
 *   1. Each method starts with a complexity count of one.
 *   2. Add one to the complexity count for each occurrence of:
 *      if  while do (while)  for  case  catch  &&  ||  ?: (ternary-operator)  ?: (elvis-operator)  ?= (elvis assignment), var?[] (safe index)  ?. (null-check)
 * </pre>
 *
 * Additional notes:
 * <ul>
 *   <li>If a class field is initialized to a Closure (ClosureExpression), then that Closure is
 *       analyzed just like a method.</li>
 * </ul>
 *
 * See http://en.wikipedia.org/wiki/Cyclomatic_complexity
 *
 * @author Chris Mair
 */
class CyclomaticComplexityMetric extends AbstractMethodMetric {
    final String name = 'CyclomaticComplexity'

    MetricResult calculate(MethodNode methodNode, SourceCode sourceCode) {
        def visitor = new CyclomaticComplexityAstVisitor(sourceCode:sourceCode)
        visitor.visitMethod(methodNode)
        def complexity = visitor.complexity
        return complexity ? new SingleNumberMetricResult(this, MetricLevel.METHOD, complexity, lineNumberForMethod(methodNode, sourceCode)) : null
    }

    MetricResult calculate(ClosureExpression closureExpression, SourceCode sourceCode) {
        def visitor = new CyclomaticComplexityAstVisitor(sourceCode:sourceCode)
        visitor.visitClosureExpression(closureExpression)
        def complexity = visitor.complexity
        return complexity ? new SingleNumberMetricResult(this, MetricLevel.METHOD, complexity, closureExpression.lineNumber) : null
    }
}
