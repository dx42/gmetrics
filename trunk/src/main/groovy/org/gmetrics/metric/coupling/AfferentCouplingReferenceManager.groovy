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

import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.MutableMapMetricResult

/**
 * Maintains a mapping of packageName -> packages that reference it, as well as a reference
 * to a MetricResult for the associated PackageResultsNode. As this object is updated with
 * subsequent package references, the MetricResult is updated as well. So, the MetricResult
 * for a PackageResultNode is not "complete" until all of the packages have been processed.
 *
 * @author Chris Mair
 */
class AfferentCouplingReferenceManager extends AbstractCouplingReferenceManager {

    protected static final String REFERENCED_FROM_PACKAGES = 'referencedFromPackages'

    AfferentCouplingReferenceManager(Metric metric) {
        super(metric)
    }

    void updateStatisticsForAllPackages() {
        referencesFromPackage.each { packageName, referencedPackages ->
            applyReverseReferencesForPackage(packageName, referencedPackages)
        }
        updateStatisticsForAllAncestorPackages()
    }

    @Override
    protected MutableMapMetricResult createEmptyMetricResult() {
        new MutableMapMetricResult(metric, MetricLevel.PACKAGE, [(VALUE):0, (TOTAL):0, (AVERAGE):0, (REFERENCED_FROM_PACKAGES):[] as Set])
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    private void applyReverseReferencesForPackage(packageName, Set<String> referencedPackages) {
        referencedPackages.each { referencedPackage ->
            if (isSourcePackageOrAncestor(referencedPackage)) {
                def metricResult = metricResultMap[referencedPackage]
                metricResult[REFERENCED_FROM_PACKAGES] << packageName
                def numberOfReferences = metricResult[REFERENCED_FROM_PACKAGES].size()
                metricResult[VALUE] = numberOfReferences
                metricResult[TOTAL] = numberOfReferences
                metricResult[AVERAGE] = numberOfReferences
                metricResult.count = 1
            }
        }
    }

    private void updateStatisticsForAllAncestorPackages() {
        def packagesWithReferences = sortPackagesWithReferencesWithParentFirst()
        packagesWithReferences.each { packageName ->
            def metricResult = metricResultMap[packageName]
            updateStatisticsForAncestorPackage(packageName, metricResult[TOTAL], metricResult.count)
        }
    }

}