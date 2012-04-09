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
 * Tests for applying the EfferentCouplingMetric at the package level
 *
 * @see EfferentCouplingMetric_ClassTest
 *
 * @author Chris Mair
 */
class EfferentCouplingMetric_PackageTest extends AbstractMetricTestCase {

    static metricClass = EfferentCouplingMetric

    private static final PACKAGE_NAME = 'com.example'
    private static final Set PACKAGE_SET1 = ['com.example', 'org.example']
    private static final Set PACKAGE_SET2 = ['com.other.util', 'org.example', 'com.acme.anvil']
    private static final VALUE = 'value'
    private static final REFERENCED_PACKAGES = 'referencedPackages'

    // Tests for applyToPackage()

    void testApplyToPackage_NoChildren() {
        assertApplyToPackage(PACKAGE_NAME, [], [referencedPackages:null, count:0, total:0, average:0])
    }

    void testApplyToPackage_OneChildClass() {
        assertApplyToPackage(PACKAGE_NAME, [classMetricResult(PACKAGE_SET1)],
            [referencedPackages:PACKAGE_SET1, value:2, count:1, total:2, average:2])
    }

    void testApplyToPackage_OneChildPackage() {
        assertApplyToPackage(PACKAGE_NAME, [packageMetricResult(PACKAGE_SET1, 3, 6)],
            [count:3, total:6, average:2])
    }

    void testApplyToPackage_MultipleChildClasses() {
        assertApplyToPackage(PACKAGE_NAME, [classMetricResult(PACKAGE_SET1), classMetricResult(PACKAGE_SET2)],
            [referencedPackages:PACKAGE_SET1 + PACKAGE_SET2, value:4, count:1, total:4, average:4])
    }

    void testApplyToPackage_MultipleChildPackages() {
        assertApplyToPackage(PACKAGE_NAME, [packageMetricResult(PACKAGE_SET1, 1, 4), packageMetricResult(PACKAGE_SET2, 2, 8)],
            [count:3, total:12, average:4])
    }

    void testApplyToPackage_ChildPackagesAndClasses() {
        assertApplyToPackage(PACKAGE_NAME, [packageMetricResult(PACKAGE_SET1, 2, 3), classMetricResult(PACKAGE_SET2)],
            [referencedPackages:PACKAGE_SET2, value:3, count:3, total:6, average:2])
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    protected void assertApplyToPackage(String packageName, Collection childMetricResults, Map expectedResultValues) {
        def metricResult = metric.applyToPackage(packageName, childMetricResults)
        assert metricResult, "No MetricResult for package [$packageName]"
        assert metricResult[REFERENCED_PACKAGES] == expectedResultValues[REFERENCED_PACKAGES] as Set
        assert metricResult[VALUE] == expectedResultValues[VALUE]
        assert metricResult[TOTAL] == expectedResultValues[TOTAL]
        assert metricResult[AVERAGE] == expectedResultValues[AVERAGE]
    }

    private MetricResult packageMetricResult(Collection<String> referencedPackages, int count=1, Integer total=null) {
        new MapMetricResult(metric, MetricLevel.PACKAGE, [referencedPackages:referencedPackages as Set, total:total], count)
    }

    private MetricResult classMetricResult(Collection<String> referencedPackages) {
        new MapMetricResult(metric, MetricLevel.CLASS, [referencedPackages:referencedPackages as Set])
    }
}