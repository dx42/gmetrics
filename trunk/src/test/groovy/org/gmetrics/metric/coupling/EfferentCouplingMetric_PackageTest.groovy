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

    private static final String PACKAGE1 = 'com.example'
    private static final String PACKAGE2 = 'com.stuff'
    private static final String PACKAGE3 = 'org.example'

    void testStatistics_NoChildren() {
        def metricResult = metric.applyToPackage(PACKAGE1, [])
        metric.afterAllSourceCodeProcessed()
        assertMetricResult(metricResult, PACKAGE1, [referencedPackages:[], value:0, total:0, average:0, count:0])
    }

    void testStatistics_ChildClassesOnly() {
        def metricResult1 = metric.applyToPackage(PACKAGE1, [classMetricResult([PACKAGE2, PACKAGE3])])
        def metricResult2 = metric.applyToPackage(PACKAGE2, [classMetricResult([PACKAGE3])])
        def metricResult3 = metric.applyToPackage(PACKAGE3, [classMetricResult([])])
        metric.afterAllSourceCodeProcessed()
        assertMetricResult(metricResult1, PACKAGE1, [referencedPackages:[PACKAGE2, PACKAGE3], value:2, total:2, average:2, count:1])
        assertMetricResult(metricResult2, PACKAGE2, [referencedPackages:[PACKAGE3], value:1, total:1, average:1, count:1])
        assertMetricResult(metricResult3, PACKAGE3, [referencedPackages:[], value:0, total:0, average:0, count:1])
    }

    void testStatistics_ChildPackagesOnly() {
        def metricResult1 = metric.applyToPackage(PACKAGE1, [packageMetricResult([PACKAGE2, PACKAGE3])])
        def metricResult2 = metric.applyToPackage(PACKAGE2, [packageMetricResult([PACKAGE2, PACKAGE3])])
        metric.afterAllSourceCodeProcessed()
        assertMetricResult(metricResult1, PACKAGE1, [referencedPackages:[], value:0, total:0, average:0, count:0])
        assertMetricResult(metricResult2, PACKAGE2, [referencedPackages:[], value:0, total:0, average:0, count:0])
    }

    void testStatistics_AggregatesTotalsAndAveragesUpThroughParentPackages() {
        def metricResult1 = metric.applyToPackage(PACKAGE1, [classMetricResult(['aa.bb.cc'])])
        metric.applyToPackage('aa.bb.cc', [classMetricResult([PACKAGE1])])
        metric.afterAllSourceCodeProcessed()
        def metricResultABC = metric.getMetricResult('aa.bb.cc')
        def metricResultAB = metric.getMetricResult('aa.bb')
        def metricResultA = metric.getMetricResult('aa')
        assertMetricResult(metricResult1, PACKAGE1, [referencedPackages:['aa.bb.cc'], value:1, total:1, average:1, count:1])
        assertMetricResult(metricResultABC, 'aa.bb.cc', [referencedPackages:[PACKAGE1], value:1, total:1, average:1, count:1])
        assertMetricResult(metricResultAB, 'aa.bb', [referencedPackages:[], value:0, total:1, average:1, count:1])
        assertMetricResult(metricResultA, 'aa', [referencedPackages:[], value:0, total:1, average:1, count:1])
    }

    void testStatistics_NormalizesPackageNames() {
        def package1WithSlashes = PACKAGE1.replace('.', '/')
        def metricResult1 = metric.applyToPackage(package1WithSlashes, [classMetricResult([PACKAGE2])])
        metric.applyToPackage(PACKAGE2, [classMetricResult([])])
        metric.afterAllSourceCodeProcessed()
        assertMetricResult(metricResult1, 'PACKAGE1', [referencedPackages:[PACKAGE2], value:1, total:1, average:1, count:1])
        assertMetricResult(metric.getMetricResult(PACKAGE2), PACKAGE2, [referencedPackages:[], value:0, total:0, average:0, count:1])
    }

}