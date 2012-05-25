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

    void testStatistics_NoChildren() {
        def metricResult = metric.applyToPackage(PACKAGE1, [])
        metric.afterAllSourceCodeProcessed()
        assertMetricResult(metricResult, PACKAGE1, [referencedFromPackages:[], value:0, total:0, average:0, count:0])
    }

    void testStatistics_ChildClassesOnly() {
        def metricResult1 = metric.applyToPackage(PACKAGE1, [classMetricResult([PACKAGE2, PACKAGE3] as Set)])
        def metricResult2 = metric.applyToPackage(PACKAGE2, [classMetricResult([PACKAGE1, PACKAGE3])])
        def metricResult3 = metric.applyToPackage(PACKAGE3, [classMetricResult([PACKAGE1])])
        metric.afterAllSourceCodeProcessed()
        assertMetricResult(metricResult1, PACKAGE1, [referencedFromPackages:[PACKAGE2, PACKAGE3], value:2, total:2, average:2, count:1])
        assertMetricResult(metricResult2, PACKAGE2, [referencedFromPackages:[PACKAGE1], value:1, total:1, average:1, count:1])
        assertMetricResult(metricResult3, PACKAGE1, [referencedFromPackages:[PACKAGE1, PACKAGE2], value:2, total:2, average:2, count:1])
    }

    void testStatistics_ChildPackagesOnly() {
        def metricResult1 = metric.applyToPackage(PACKAGE1, [packageMetricResult([PACKAGE2, PACKAGE3] as Set)])
        def metricResult2 = metric.applyToPackage(PACKAGE2, [packageMetricResult([PACKAGE2, PACKAGE3] as Set)])
        metric.afterAllSourceCodeProcessed()
        assertMetricResult(metricResult1, PACKAGE1, [referencedFromPackages:[], value:0, total:0, average:0, count:0])
        assertMetricResult(metricResult2, PACKAGE2, [referencedFromPackages:[], value:0, total:0, average:0, count:0])
    }

    void testStatistics_AggregatesTotalsAndAveragesUpThroughParentPackages() {
        def metricResult1 = metric.applyToPackage(PACKAGE1, [classMetricResult(['aaa.bbb.ccc'] as Set)])
        metric.applyToPackage('aaa.bbb.ccc', [classMetricResult([] as Set)])
        metric.afterAllSourceCodeProcessed()
        def metricResultABC = metric.getMetricResult('aaa.bbb.ccc')
        def metricResultAB = metric.getMetricResult('aaa.bbb')
        def metricResultA = metric.getMetricResult('aaa')
        assertMetricResult(metricResult1, PACKAGE1, [referencedFromPackages:[], value:0, total:0, average:0, count:1])
        assertMetricResult(metricResultABC, 'aaa.bbb.ccc', [referencedFromPackages:[PACKAGE1], value:1, total:1, average:1, count:1])
        assertMetricResult(metricResultAB, 'aaa.bbb', [referencedFromPackages:[], value:0, total:1, average:1, count:1])
        assertMetricResult(metricResultA, 'aaa', [referencedFromPackages:[], value:0, total:1, average:1, count:1])
    }

    void testStatistics_NormalizesPackageNames() {
        def package1WithSlashes = PACKAGE1.replace('.', '/')
        def metricResult1 = metric.applyToPackage(package1WithSlashes, [classMetricResult([PACKAGE2, PACKAGE3])])
        metric.applyToPackage(PACKAGE2, [classMetricResult([] as Set)])
        metric.afterAllSourceCodeProcessed()
        assertMetricResult(metricResult1, 'PACKAGE1', [referencedFromPackages:[], value:0, total:0, average:0, count:1])
        assertMetricResult(metric.getMetricResult(PACKAGE2), PACKAGE2, [referencedFromPackages:[PACKAGE1], value:1, total:1, average:1, count:1])
    }

}