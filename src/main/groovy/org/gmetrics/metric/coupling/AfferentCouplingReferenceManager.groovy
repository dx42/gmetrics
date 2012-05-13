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
import org.gmetrics.result.MetricResult
import org.gmetrics.result.MutableMapMetricResult
import org.gmetrics.util.Calculator
import org.gmetrics.util.ClassNameUtil

/**
 * Maintains a mapping of packageName -> packages that reference it, as well as a reference
 * to a MetricResult for the associated PackageResultsNode. As this object is updated with
 * subsequent package references, the MetricResult is updated as well. So, the MetricResult
 * for a PackageResultNode is not "complete" until all of the packages have been processed.
 *
 * @author Chris Mair
 */
class AfferentCouplingReferenceManager {

    protected static final String REFERENCED_FROM_PACKAGES = 'referencedFromPackages'

    final Metric metric
    private Map<String, Set<String>> referencesFromPackage = [:].withDefault { [] as Set<String> }
    private Map<String, MutableMapMetricResult> metricResultMap = [:].withDefault { createEmptyMetricResult() }

    AfferentCouplingReferenceManager(Metric metric) {
        assert metric
        this.metric = metric
    }

    void addReferencesFromPackage(String rawPackageName, Collection<String> rawPackages) {
        def packageName = normalizePackageName(rawPackageName)
        def packages = rawPackages.collect { pkg -> normalizePackageName(pkg) }
        referencesFromPackage[packageName].addAll(packages)
        metricResultMap[packageName].count = 1
    }

    void updateStatisticsForAllPackages() {
        referencesFromPackage.each { packageName, referencedPackages ->
            applyReverseReferencesForPackage(packageName, referencedPackages)
        }
        updateStatisticsForAllAncestorPackages()
    }

    private void applyReverseReferencesForPackage(packageName, Set<String> referencedPackages) {
        referencedPackages.each { referencedPackage ->
            def metricResult = metricResultMap[referencedPackage]
            def refs = metricResult[REFERENCED_FROM_PACKAGES]
            refs << packageName
            metricResult[VALUE] = refs.size()
            metricResult[TOTAL] = refs.size()
            metricResult[AVERAGE] = refs.size()
            metricResult.count = 1
        }
    }

    private void updateStatisticsForAllAncestorPackages() {
        def packagesWithReferences = new HashSet(metricResultMap.keySet())
        packagesWithReferences.each { packageName ->
            def metricResult = metricResultMap[packageName]
            updateStatisticsForAncestorPackage(packageName, metricResult[TOTAL], metricResult.count)
        }
    }

    private void updateStatisticsForAncestorPackage(String packageName, int addToTotal, int addToCount) {
        def parentPackageName = ClassNameUtil.parentPackageName(packageName)
        if (parentPackageName) {
            def metricResult = metricResultMap[parentPackageName]
            metricResult.count += addToCount
            metricResult.map[TOTAL] += addToTotal
            metricResult.map[AVERAGE] = Calculator.calculateAverage(metricResult.map[TOTAL], metricResult.count, 2)
            updateStatisticsForAncestorPackage(parentPackageName, addToTotal, addToCount)
        }
    }

    Set<String> getReferencesFromPackage(String rawPackageName) {
        def packageName = normalizePackageName(rawPackageName)
        return referencesFromPackage[packageName]
    }

    MetricResult getPackageMetricResult(String rawPackageName) {
        def packageName = normalizePackageName(rawPackageName)
        return metricResultMap[packageName]
    }

    protected String normalizePackageName(String name) {
        return name ? name.replace('/', '.') : name
    }

    private MutableMapMetricResult createEmptyMetricResult() {
        new MutableMapMetricResult(metric, MetricLevel.PACKAGE, [(VALUE):0, (TOTAL):0, (AVERAGE):0, (REFERENCED_FROM_PACKAGES):[] as Set])
    }

}