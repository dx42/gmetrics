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

import static org.gmetrics.result.FunctionNames.*

import org.gmetrics.metric.AbstractMetricTestCase
import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.MapMetricResult
import org.gmetrics.result.MetricResult

/**
 * Abstract superclass for package-level tests for package-level coupling metrics
 *
 * @author Chris Mair
 */
abstract class AbstractPackageCouplingMetric_PackageTestCase extends AbstractMetricTestCase {

    protected static final REFERENCED_PACKAGES = 'referencedPackages'
    protected static final REFERENCED_FROM_PACKAGES = 'referencedFromPackages'


    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    protected void assertMetricResult(metricResult, String packageName, Map expectedResultValues) {
        assert metricResult, "No MetricResult for package [$packageName]"
        assert metricResult[REFERENCED_PACKAGES] == expectedResultValues[REFERENCED_PACKAGES] as Set
        assert metricResult[REFERENCED_FROM_PACKAGES] == expectedResultValues[REFERENCED_FROM_PACKAGES] as Set
        assert metricResult[VALUE] == expectedResultValues[VALUE]
        assert metricResult[TOTAL] == expectedResultValues[TOTAL]
        assert metricResult[AVERAGE] == expectedResultValues[AVERAGE]
        assert metricResult.getCount() == expectedResultValues['count']
    }

    protected MetricResult packageMetricResult(Collection<String> referencedPackages, int count=1, Integer total=null) {
        new MapMetricResult(metric, MetricLevel.PACKAGE, [referencedPackages:referencedPackages as Set, total:total], count)
    }

    protected MetricResult classMetricResult(Collection<String> referencedPackages) {
        new MapMetricResult(metric, MetricLevel.CLASS, [referencedPackages:referencedPackages as Set])
    }
}