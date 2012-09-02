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
package org.gmetrics.metric

import org.gmetrics.result.ClassMetricResult
import org.codehaus.groovy.ast.ClassNode
import org.gmetrics.source.SourceCode
import org.gmetrics.result.MetricResult
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression

/**
 * Stub implementation of the Metric interface, for testing.
 *
 * @author Chris Mair
 */
@SuppressWarnings('UnusedMethodParameter')
class StubMetric implements MethodMetric {

    boolean enabled = true
    String name = 'Stub'
    MetricLevel baseLevel = MetricLevel.METHOD
    ClassMetricResult classMetricResult
    MetricResult packageMetricResult
    MetricResult methodMetricResult
    MetricResult closureMetricResult
    String path
    String packageName
    def otherProperty
    List<String> functions = ['total', 'average']

    @Override
    ClassMetricResult applyToClass(ClassNode classNode, SourceCode sourceCode) {
        return classMetricResult
    }

    @Override
    MetricResult applyToPackage(String path, String packageName, Collection childMetricResults) {
        this.path = path
        this.packageName = packageName
        return packageMetricResult
    }

    @Override
    MetricResult applyToMethod(MethodNode methodNode, SourceCode sourceCode) {
        return methodMetricResult
    }

    @Override
    MetricResult applyToClosure(ClosureExpression closureExpression, SourceCode sourceCode) {
        return closureMetricResult
    }

    @Override
    String toString() {
        "StubMetric[name=$name, baseLevel=$baseLevel, otherProperty=$otherProperty]"
    }
}