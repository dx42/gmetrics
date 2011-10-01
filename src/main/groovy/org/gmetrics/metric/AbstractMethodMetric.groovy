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
package org.gmetrics.metric

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.gmetrics.source.SourceCode
import org.gmetrics.util.AstUtil
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.result.MetricResult

/**
 * Abstract superclass for method-based metrics.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbstractMethodMetric extends AbstractMetric {

    final MetricLevel baseLevel = MetricLevel.METHOD

    abstract MetricResult calculate(MethodNode methodNode, SourceCode sourceCode)
    abstract MetricResult calculate(ClosureExpression closureExpression, SourceCode sourceCode)

    protected ClassMetricResult calculateForClass(ClassNode classNode, SourceCode sourceCode) {
        def childMetricResults = [:]

        if (isNotAnInterface(classNode)) {
            addMethodsToMetricResults(sourceCode, classNode, childMetricResults)
            addClosureFieldsToMetricResults(sourceCode, classNode, childMetricResults)
        }

        if (childMetricResults.isEmpty()) {
            return null
        }

        def aggregateMetricResults = createAggregateMetricResult(childMetricResults.values(), classNode)

        return new ClassMetricResult(aggregateMetricResults, childMetricResults)
    }

    private void addClosureFieldsToMetricResults(SourceCode sourceCode, ClassNode classNode, Map childMetricResults) {
        def closureFields = classNode.fields.findAll {fieldNode -> AstUtil.isClosureField(fieldNode) }
        closureFields.each {fieldNode ->
            def fieldResult = calculate(fieldNode.initialExpression, sourceCode)
            childMetricResults[fieldNode.name] = fieldResult
        }
    }

    private void addMethodsToMetricResults(SourceCode sourceCode, ClassNode classNode, Map childMetricResults) {
        def methodsPlusConstructors = classNode.getMethods() + classNode.getDeclaredConstructors() + classNode.getMethods()

        methodsPlusConstructors.each {methodNode ->
            def methodResult = calculate(methodNode, sourceCode)
            if (methodResult) {
                childMetricResults[methodNode.name] = methodResult
            }
        }
    }
}
