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

import static org.gmetrics.result.FunctionNames.VALUE

import org.gmetrics.result.MetricResult
import org.gmetrics.metric.MetricLevel
import org.gmetrics.metric.Metric
import org.gmetrics.result.MutableMapMetricResult

/**
 * Maintains a mapping of packageName -> packages that reference it, as well as a reference
 * to a MetricResult for the associated PackageResultsNode. As this object is updated with
 * subsequent package references, the MetricResult is updated as well. So, the MetricResult
 * for a PackageResultNode is not "complete" until all of the packages have been processed.
 *
 * @author Chris Mair
 */
class PackageReferenceManager {

    protected static final String REFERENCED_BY_PACKAGES = 'referencedByPackages'

    final Metric metric
    private Map<String, Set<String>> referencesToPackage = [:].withDefault { [] as Set<String> }
    private Map<String, MutableMapMetricResult> metricResultMap = [:].withDefault { createEmptyMetricResult() }

    PackageReferenceManager(Metric metric) {
        assert metric
        this.metric = metric
    }

    void addReferencesFromPackage(String packageName, Set<String> packages) {
        packages.each { otherPackage ->
            def refs = referencesToPackage[otherPackage]
            refs << packageName
            def metricResult = metricResultMap[otherPackage]
            metricResult.map[REFERENCED_BY_PACKAGES] = refs
            metricResult.map[VALUE] = refs.size()
        }
    }

    Set<String> getReferencesToPackage(String packageName) {
        return referencesToPackage[packageName]
    }

    MetricResult getPackageMetricResult(String packageName) {
        return metricResultMap[packageName]
    }

    private MutableMapMetricResult createEmptyMetricResult() {
        new MutableMapMetricResult(metric, MetricLevel.PACKAGE, [:])
    }

}
