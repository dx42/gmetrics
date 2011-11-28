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

/**
 * Stub implementation of the Metric interface, for testing.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
@SuppressWarnings('UnusedMethodParameter')
class StubMetric implements Metric {

    boolean enabled = true
    String name = 'Stub'
    MetricLevel baseLevel = MetricLevel.METHOD
    ClassMetricResult classMetricResult
    MetricResult packageMetricResult
    String otherProperty
    List<String> functions = ['total', 'average']

    ClassMetricResult applyToClass(ClassNode classNode, SourceCode sourceCode) {
        return classMetricResult
    }

    MetricResult applyToPackage(Collection childMetricResults) {
        return packageMetricResult
    }

    String toString() {
        "StubMetric[name=$name, baseLevel=$baseLevel, otherProperty=$otherProperty]"
    }
}