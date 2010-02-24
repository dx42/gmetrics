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
package org.gmetrics.metric.linecount

import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.ClassMetricResult
import org.codehaus.groovy.ast.ClassNode
import org.gmetrics.source.SourceCode
import org.gmetrics.result.MetricResult
import org.gmetrics.metric.AbstractAstVisitor
import org.gmetrics.result.NumberMetricResult
import org.gmetrics.result.AggregateNumberMetricResult
import org.gmetrics.metric.AbstractMetric

/**
 * Metric for counting the lines of code for classes and interfaces.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ClassLineCountMetric extends AbstractMetric {

    final String name = 'ClassLineCount'
    final MetricLevel baseLevel = MetricLevel.CLASS

    ClassMetricResult calculateForClass(ClassNode classNode, SourceCode sourceCode) {
        def visitor = new ClassLineCountAstVisitor(sourceCode:sourceCode)
        visitor.visitClass(classNode)
        if (visitor.numberOfLinesInClass == 0) {
            return null
        }
        def metricResult = new NumberMetricResult(this, visitor.numberOfLinesInClass)
        return new ClassMetricResult(metricResult)
    }

    private createAggregateMetricResult(Collection childMetricResults) {
        new AggregateNumberMetricResult(this, childMetricResults)
    }

}

class ClassLineCountAstVisitor extends AbstractAstVisitor {
    int numberOfLinesInClass
    void visitClass(ClassNode classNode) {
        if (classNode.lineNumber >= 0) {
            numberOfLinesInClass = classNode.lastLineNumber - classNode.lineNumber + 1
        }
        super.visitClass(classNode)
    }

}