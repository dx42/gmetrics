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

/**
 * Tests for applying the EfferentCouplingMetric at the package level
 *
 * @see EfferentCouplingMetric_ClassTest
 *
 * @author Chris Mair
 */
class EfferentCouplingMetric_PackageTest extends AbstractPackageCouplingMetric_PackageTestCase {

    static metricClass = EfferentCouplingMetric

    private static final PACKAGE_NAME = 'com.example'
    private static final Set PACKAGE_SET1 = ['com.stuff', 'org.example']
    private static final Set PACKAGE_SET2 = ['com.other.util', 'org.example', 'com.acme.anvil']

    // Tests for applyToPackage()

    void testApplyToPackage_NoChildren() {
        assertApplyToPackage(PACKAGE_NAME, [], [referencedPackages:null, value:0, count:0, total:0, average:0])
    }

    void testApplyToPackage_OneChildClass() {
        assertApplyToPackage(PACKAGE_NAME, [classMetricResult(PACKAGE_SET1)],
            [referencedPackages:PACKAGE_SET1, value:2, count:1, total:2, average:2])
    }

    void testApplyToPackage_OneChildPackage() {
        assertApplyToPackage(PACKAGE_NAME, [packageMetricResult(PACKAGE_SET1, 3, 6)],
            [value:0, count:3, total:6, average:2])
    }

    void testApplyToPackage_MultipleChildClasses() {
        assertApplyToPackage(PACKAGE_NAME, [classMetricResult(PACKAGE_SET1), classMetricResult(PACKAGE_SET2)],
            [referencedPackages:PACKAGE_SET1 + PACKAGE_SET2, value:4, count:1, total:4, average:4])
    }

    void testApplyToPackage_MultipleChildPackages() {
        assertApplyToPackage(PACKAGE_NAME, [packageMetricResult(PACKAGE_SET1, 1, 4), packageMetricResult(PACKAGE_SET2, 2, 8)],
            [value:0, count:3, total:12, average:4])
    }

    void testApplyToPackage_ChildPackagesAndClasses() {
        assertApplyToPackage(PACKAGE_NAME, [packageMetricResult(PACKAGE_SET1, 2, 3), classMetricResult(PACKAGE_SET2)],
            [referencedPackages:PACKAGE_SET2, value:3, count:3, total:6, average:2])
    }

}