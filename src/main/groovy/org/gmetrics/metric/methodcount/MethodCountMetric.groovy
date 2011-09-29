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
import org.gmetrics.metric.AbstractMetric
import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.result.NumberMetricResult
import org.gmetrics.source.SourceCode
import org.gmetrics.util.AstUtil

/**
 * Metric for counting the number of methods within classes and interfaces.
 *
 * @author Chris Mair
 */
class MethodCountMetric extends AbstractMetric {

    final String name = 'MethodCount'
    final MetricLevel baseLevel = MetricLevel.CLASS

    protected ClassMetricResult calculateForClass(ClassNode classNode, SourceCode sourceCode) {
        def visitor = new MethodCountAstVisitor(sourceCode:sourceCode)
        if (AstUtil.isFromGeneratedSourceCode(classNode)) {
            return null
        }
        visitor.visitClass(classNode)
        def metricResult = new NumberMetricResult(this, visitor.numberOfMethods, classNode.lineNumber)
        return new ClassMetricResult(metricResult)
    }

}

