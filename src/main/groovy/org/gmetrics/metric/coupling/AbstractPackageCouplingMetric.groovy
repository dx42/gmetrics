/*
 * Copyright 2012 the original author or authors.
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
package org.gmetrics.metric.coupling

import org.codehaus.groovy.ast.ClassNode
import org.gmetrics.metric.AbstractMetric
import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.result.MapMetricResult
import org.gmetrics.source.SourceCode
import org.gmetrics.result.FunctionNames

/**
 * Abstract superclass for Metrics that measure package-level coupling.
 *
 * @author Chris Mair
 */
abstract class AbstractPackageCouplingMetric extends AbstractMetric {

    protected static final String REFERENCED_PACKAGES = 'referencedPackages'

    final MetricLevel baseLevel = MetricLevel.PACKAGE
    String ignorePackageNames

    AbstractPackageCouplingMetric() {
        this.functions = [FunctionNames.VALUE, FunctionNames.AVERAGE]
    }

    @Override
    protected ClassMetricResult calculateForClass(ClassNode classNode, SourceCode sourceCode) {
        def visitor = new PackageReferenceAstVisitor(ignorePackageNames)
        visitor.setSourceCode(sourceCode)
        visitor.visitClass(classNode)
        def otherPackages = visitor.otherPackages

        def metricResult = new MapMetricResult(this, MetricLevel.CLASS, [(REFERENCED_PACKAGES):otherPackages])
        return new ClassMetricResult(metricResult)
    }

}