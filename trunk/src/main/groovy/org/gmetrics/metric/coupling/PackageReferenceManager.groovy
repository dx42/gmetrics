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
import org.gmetrics.util.ClassNameUtil
import org.gmetrics.util.Calculator

/**
 * Maintains a mapping of packageName -> packages that reference it, as well as a reference
 * to a MetricResult for the associated PackageResultsNode. As this object is updated with
 * subsequent package references, the MetricResult is updated as well. So, the MetricResult
 * for a PackageResultNode is not "complete" until all of the packages have been processed.
 *
 * @author Chris Mair
 */
class PackageReferenceManager {

    protected static final String REFERENCED_FROM_PACKAGES = 'referencedFromPackages'

    final Metric metric
    private Map<String, Set<String>> referencesToPackage = [:].withDefault { [] as Set<String> }
    private Map<String, MutableMapMetricResult> metricResultMap = [:].withDefault { createEmptyMetricResult() }
    private Set<String> packagesContainingClasses = []

    PackageReferenceManager(Metric metric) {
        assert metric
        this.metric = metric
    }

    void registerPackageContainingClasses(String rawPackageName) {
        def packageName = normalizePackageName(rawPackageName)
        if (!packagesContainingClasses.contains(packageName)) {
            def metricResult = metricResultMap[packageName]
            metricResult.count += 1
            metricResult.map[AVERAGE] = Calculator.calculateAverage(metricResult.map[TOTAL], metricResult.count, 2)
            packagesContainingClasses << packageName
            incrementTotalsForAncestorPackages(packageName, 0, 1)
        }
    }

    void addReferencesFromPackage(String rawPackageName, Set<String> packages) {
        def packageName = normalizePackageName(rawPackageName)
        registerPackageContainingClasses(packageName)
        packages.each { rawOtherPackage ->
            def otherPackage = normalizePackageName(rawOtherPackage)
            registerPackageContainingClasses(otherPackage)
            def refs = referencesToPackage[otherPackage]
            def originalNumReferences = refs.size()
            refs << packageName
            def metricResult = metricResultMap[otherPackage]
            metricResult.map[REFERENCED_FROM_PACKAGES] = refs
            metricResult.map[VALUE] = refs.size()
            metricResult.map[TOTAL] = refs.size()
            metricResult.map[AVERAGE] = Calculator.calculateAverage(metricResult.map[TOTAL], metricResult.count, 2)

            def addToTotal = refs.size() - originalNumReferences
            incrementTotalsForAncestorPackages(otherPackage, addToTotal, 0)
        }
    }

    private void incrementTotalsForAncestorPackages(String packageName, int addToTotal, int addToCount) {
        def parentPackageName = ClassNameUtil.parentPackageName(packageName)
        if (parentPackageName) {
            def metricResult = metricResultMap[parentPackageName]
            metricResult.count += addToCount
            metricResult.map[TOTAL] += addToTotal
            metricResult.map[AVERAGE] = Calculator.calculateAverage(metricResult.map[TOTAL], metricResult.count, 2)
            incrementTotalsForAncestorPackages(parentPackageName, addToTotal, addToCount)
        }
    }

    Set<String> getReferencesToPackage(String rawPackageName) {
        def packageName = normalizePackageName(rawPackageName)
        return referencesToPackage[packageName]
    }

    MetricResult getPackageMetricResult(String rawPackageName) {
        def packageName = normalizePackageName(rawPackageName)
        return metricResultMap[packageName]
    }

    protected String normalizePackageName(String name) {
        return name ? name.replace('/', '.') : name
    }

    private MutableMapMetricResult createEmptyMetricResult() {
        new MutableMapMetricResult(metric, MetricLevel.PACKAGE, [(VALUE):0, (TOTAL):0, (AVERAGE):0])
    }

}