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

    // Tests for applyToPackage()

    void testApplyToPackage_ResultsForNoChildren() {
        assertApplyToPackage(PACKAGE_NAME, [], [])
    }

    void testApplyToPackage_ResultsForOneChildClass() {
        assertApplyToPackage(PACKAGE_NAME, [metricResult(PACKAGE_SET1)], PACKAGE_SET1)
    }

    void testApplyToPackage_ResultsForMultipleChildClasses() {
        assertApplyToPackage(PACKAGE_NAME, [metricResult(PACKAGE_SET1), metricResult(PACKAGE_SET2)], PACKAGE_SET1 + PACKAGE_SET2)
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    protected void assertApplyToPackage(String packageName, Collection childMetricResults, Collection referencedPackages) {
        def metricResult = metric.applyToPackage(packageName, childMetricResults)
        assert metricResult, "No MetricResult for package [$packageName]"
        assert metricResult['referencedPackages'] == referencedPackages as Set
    }

    private MetricResult metricResult(Collection<String> referencedPackages) {
        new MapMetricResult(metric, MetricLevel.PACKAGE, [referencedPackages:referencedPackages as Set])
    }
}