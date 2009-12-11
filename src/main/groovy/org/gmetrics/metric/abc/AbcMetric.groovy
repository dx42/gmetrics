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
package org.gmetrics.metric.abc

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.gmetrics.metric.AbstractMethodMetric
import org.gmetrics.source.SourceCode
import org.gmetrics.metric.abc.result.AggregateAbcMetricResult
import org.gmetrics.metric.abc.result.AbcMetricResult

/**
 * Calculate the ABC Metric for a class/method.
 *
 * The ABC Counting Rules for Groovy:
 * <pre>
 *   1. Add one to the assignment count for each occurrence of an assignment operator, excluding constant declarations:
 *      = *= /= %= += <<= >>= &= |= ^= >>>=
 *   2. Add one to the assignment count for each occurrence of an increment or decrement operator (prefix or postfix):
 *      ++ --
 *   3. Add one to the branch count for each function call or class method call.
 *   4. Add one to the branch count for each occurrence of the new operator.
 *   5. Add one to the condition count for each use of a conditional operator:
 *      == != <= >= < > <=> =~ ==~
 *   6. Add one to the condition count for each use of the following keywords:
 *      else case default try catch ?
 *   7. Add one to the condition count for each unary conditional expression.
 * </pre>
 *
 * Additional notes:
 * <ul>
 *   <li>A property access is treated like a method call (and thus increments the branch count).</li>
 *   <li>If a class field is initialized to a Closure (ClosureExpression), then that Closure is
 *       analyzed just like a method.</li>
 * </ul>
 *
 * See http://www.softwarerenovation.com/ABCMetric.pdf
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbcMetric extends AbstractMethodMetric {
    final String name = "ABC"

    def calculate(MethodNode methodNode, SourceCode sourceCode) {
        def visitor = new AbcAstVisitor(sourceCode:sourceCode)
        visitor.visitMethod(methodNode)
        def abcVector = new AbcVector(visitor.numberOfAssignments, visitor.numberOfBranches, visitor.numberOfConditions)
        return new AbcMetricResult(this, abcVector)
    }

    def calculate(ClosureExpression closureExpression, SourceCode sourceCode) {
        def visitor = new AbcAstVisitor(sourceCode:sourceCode)
        visitor.visitClosureExpression(closureExpression) 
        def abcVector = new AbcVector(visitor.numberOfAssignments, visitor.numberOfBranches, visitor.numberOfConditions)
        return new AbcMetricResult(this, abcVector)
    }

    protected createAggregateMetricResult(Collection childMetricResults) {
        new AggregateAbcMetricResult(this, childMetricResults)
    }

}
