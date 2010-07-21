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

import org.codehaus.groovy.ast.ClassNode
import org.gmetrics.source.SourceCode
import org.gmetrics.result.*

/**
 * Abstract superclass for metrics.
 *
 * Subclasses must implement the <code>calculateForClass(ClassNode, SourceCode)</code> method.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */

abstract class AbstractMetric implements Metric {

    boolean enabled = true
    List<String> functions = ['total', 'average']

    protected abstract ClassMetricResult calculateForClass(ClassNode classNode, SourceCode sourceCode)

    MetricResult applyToPackage(Collection childMetricResults) {
        if (!enabled) {
            return null
        }
        return calculateForPackage(childMetricResults)
    }

    protected MetricResult calculateForPackage(Collection childMetricResults) {
        return createAggregateMetricResult(childMetricResults)
    }

    ClassMetricResult applyToClass(ClassNode classNode, SourceCode sourceCode) {
        if (!enabled) {
            return null
        }
        return calculateForClass(classNode, sourceCode)
    }

    protected boolean isNotAnInterface(ClassNode classNode) {
        return !(classNode.modifiers & ClassNode.ACC_INTERFACE)
    }

    protected MetricResult createAggregateMetricResult(Collection childMetricResults) {
        new AggregateNumberMetricResult(this, childMetricResults)
    }

}