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
package org.gmetrics.metric.classcount

import org.codehaus.groovy.ast.ClassNode
import org.gmetrics.metric.AbstractMetric
import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.result.MetricResult
import org.gmetrics.result.SingleNumberMetricResult
import org.gmetrics.source.SourceCode

/**
 * Metric for counting the number of classes within each package.
 *
 * @author Chris Mair
 */
class ClassCountMetric extends AbstractMetric {

    final String name = 'ClassCount'
    final MetricLevel baseLevel = MetricLevel.PACKAGE

    @SuppressWarnings('UnusedMethodParameter')
    protected ClassMetricResult calculateForClass(ClassNode classNode, SourceCode sourceCode) {
        def metricResult = new SingleNumberMetricResult(this, MetricLevel.CLASS, 1, classNode.lineNumber)
        return new ClassMetricResult(metricResult)
    }

    @SuppressWarnings('UnusedMethodParameter')
    protected MetricResult calculateForPackage(String path, String packageName, Collection<MetricResult> childMetricResults) {
        def numClasses = childMetricResults.inject(0) { sum, result -> result.metricLevel == MetricLevel.CLASS ? sum+1 : sum }
        return new SingleNumberMetricResult(this, MetricLevel.PACKAGE, numClasses)
    }

}

