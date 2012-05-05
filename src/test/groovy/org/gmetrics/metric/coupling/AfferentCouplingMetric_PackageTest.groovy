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
 * Tests for applying the AfferentCouplingMetric at the package level
 *
 * @see AfferentCouplingMetric_ClassTest
 *
 * @author Chris Mair
 */
class AfferentCouplingMetric_PackageTest extends AbstractPackageCouplingMetric_PackageTestCase {

    static metricClass = AfferentCouplingMetric

    private static final String PACKAGE1 = 'com.example'
    private static final String PACKAGE2 = 'com.stuff'
    private static final String PACKAGE3 = 'org.example'

    // Tests for applyToPackage()

    void testApplyToPackage_NoChildren() {
        assertApplyToPackage(PACKAGE1, [], [referencedFromPackages:null, value:0, count:0])
    }

    void testApplyToPackage_ChildClassesOnly() {
        assertApplyToPackage(PACKAGE1, [classMetricResult([PACKAGE2, PACKAGE3] as Set)], [referencedFromPackages:null, value:0])
        assertApplyToPackage(PACKAGE2, [classMetricResult([PACKAGE1, PACKAGE3])], [referencedFromPackages:[PACKAGE1], value:1])
        assertApplyToPackage(PACKAGE3, [classMetricResult([PACKAGE1])], [referencedFromPackages:[PACKAGE1, PACKAGE2], value:2])
        assertApplyToPackage(PACKAGE1, [classMetricResult([] as Set)], [referencedFromPackages:[PACKAGE2, PACKAGE3], value:2])
    }

    void testApplyToPackage_ChildPackagesOnly() {
        assertApplyToPackage(PACKAGE1, [packageMetricResult([PACKAGE2, PACKAGE3] as Set)], [referencedFromPackages:null, value:0])
        assertApplyToPackage(PACKAGE2, [packageMetricResult([PACKAGE2, PACKAGE3] as Set)], [referencedFromPackages:null, value:0])
        assertApplyToPackage(PACKAGE1, [packageMetricResult([PACKAGE2, PACKAGE3] as Set)], [referencedFromPackages:null, value:0])
    }

    void testApplyToPackage_NormalizesPackageNames() {
        def package1WithSlashes = PACKAGE1.replace('.', '/')
        assertApplyToPackage(package1WithSlashes, [classMetricResult([PACKAGE2, PACKAGE3] as Set)], [referencedFromPackages:null, value:0])
        assertApplyToPackage(PACKAGE2, [], [referencedFromPackages:[PACKAGE1], value:1])
    }


}