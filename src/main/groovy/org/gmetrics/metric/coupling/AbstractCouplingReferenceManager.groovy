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

import static org.gmetrics.result.FunctionNames.AVERAGE
import static org.gmetrics.result.FunctionNames.TOTAL

import org.gmetrics.metric.Metric
import org.gmetrics.result.MetricResult
import org.gmetrics.result.MutableMapMetricResult
import org.gmetrics.util.Calculator
import org.gmetrics.util.ClassNameUtil

/**
 * Abstract superclass for Afferent/efferent coupling reference manager classes.
 *
 * @author Chris Mair
 */
abstract class AbstractCouplingReferenceManager {

    protected static final String ROOT = '<ROOT>'

    final Metric metric
    protected Map<String, Set<String>> referencesFromPackage = [:].withDefault { [] as Set<String> }
    protected Map<String, MutableMapMetricResult> metricResultMap = [:].withDefault { createEmptyMetricResult() }

    AbstractCouplingReferenceManager(Metric metric) {
        assert metric
        this.metric = metric
    }

    protected abstract MutableMapMetricResult createEmptyMetricResult()

    void addReferencesFromPackage(String rawPackageName, Collection<String> rawPackages) {
        def packageName = normalizePackageName(rawPackageName)
        def packages = rawPackages.collect { pkg -> normalizePackageName(pkg) }
        referencesFromPackage[packageName].addAll(packages)
        metricResultMap[packageName].count = 1
    }

    MetricResult getPackageMetricResult(String rawPackageName) {
        def packageName = normalizePackageName(rawPackageName)
        return metricResultMap[packageName]
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    protected boolean isSourcePackageOrAncestor(String packageName) {
        if (referencesFromPackage.containsKey(packageName) || packageName == ROOT) {
            return true
        }
        return referencesFromPackage.keySet().find { fromPackageName ->
            fromPackageName.startsWith(packageName + '.')
        }
    }

    protected SortedSet sortPackagesWithReferencesWithParentFirst() {
        return new TreeSet(metricResultMap.keySet())
    }

    protected void updateStatisticsForAncestorPackage(String packageName, int addToTotal, int addToCount) {
        def parentPackageName = parentPackageName(packageName)
        if (parentPackageName && isSourcePackageOrAncestor(parentPackageName)) {
            def metricResult = metricResultMap[parentPackageName]
            metricResult.count += addToCount
            metricResult.map[TOTAL] += addToTotal
            metricResult.map[AVERAGE] = Calculator.calculateAverage(metricResult.map[TOTAL], metricResult.count, 2)
            if (parentPackageName != ROOT) {
                updateStatisticsForAncestorPackage(parentPackageName, addToTotal, addToCount)
            }
        }
    }

    protected String parentPackageName(String packageName) {
        return ClassNameUtil.parentPackageName(packageName) ?: ROOT
    }

    protected Set<String> getReferencesFromPackage(String rawPackageName) {
        def packageName = normalizePackageName(rawPackageName)
        return referencesFromPackage[packageName]
    }

    protected String normalizePackageName(String name) {
        return name ? name.replace('/', '.') : ROOT
    }

}