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
package org.gmetrics.metric

import org.codehaus.groovy.ast.ClassNode
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.source.SourceCode
import org.gmetrics.result.MetricResult

/**
 * Represents a metric
 *
 * @author Chris Mair
 */
interface Metric {

    String getName()

    MetricLevel getBaseLevel()

    ClassMetricResult applyToClass(ClassNode classNode, SourceCode sourceCode)
    
    MetricResult applyToPackage(String path, String packageName, Collection<MetricResult> childMetricResults)

    List<String> getFunctions()

    boolean isEnabled()
}