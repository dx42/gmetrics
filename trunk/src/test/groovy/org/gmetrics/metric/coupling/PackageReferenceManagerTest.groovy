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
import static PackageReferenceManager.REFERENCED_FROM_PACKAGES

import org.gmetrics.metric.MetricLevel
import org.gmetrics.metric.StubMetric
import org.gmetrics.result.MetricResult
import org.gmetrics.test.AbstractTestCase

/**
 * Tests for PackageReferenceManager
 *
 * @author Chris Mair
 */
class PackageReferenceManagerTest extends AbstractTestCase {

    private static final METRIC = new StubMetric()
    private static final PACKAGE1 = 'a.b.package1'
    private static final PACKAGE2 = 'c.d.package2'
    private static final PACKAGE3 = 'e.f.package3'
    private static final PACKAGE4 = 'g.h.package4'

    private PackageReferenceManager manager = new PackageReferenceManager(METRIC)

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    void testConstructor_NullMetric_ThrowsException() {
        shouldFailWithMessageContaining('metric') { new PackageReferenceManager(null) }
    }

    void testConstructor_AssignsMetric() {
        assert new PackageReferenceManager(METRIC).metric == METRIC
    }

    void testGetReferencesToPackage_InitializedToEmptySet() {
        assert manager.getReferencesToPackage(PACKAGE1) == [] as Set
    }

    void testAddReferencesFromPackage_AddsReverseReferences() {
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2, PACKAGE3] as Set)
        assert manager.getReferencesToPackage(PACKAGE1) == [] as Set
        assert manager.getReferencesToPackage(PACKAGE2) == [PACKAGE1] as Set
        assert manager.getReferencesToPackage(PACKAGE3) == [PACKAGE1] as Set
    }

    void testAddReferencesFromPackage_IncrementsTotalForAncestorPackages() {
        manager.addReferencesFromPackage(PACKAGE1, ['aaa.bbb.ccc'] as Set)

        assertMetricResult(manager.getPackageMetricResult('aaa.bbb.ccc'), [count:1, value:1, total:1, average:1, referencedFromPackages:[PACKAGE1]])
        assertMetricResult(manager.getPackageMetricResult('aaa.bbb'), [count:1, value:0, total:1, average:1, referencedFromPackages:null])
        assertMetricResult(manager.getPackageMetricResult('aaa'), [count:1, value:0, total:1, average:1, referencedFromPackages:null])
    }

    void testAddReferencesFromPackage_IncrementsTotalForSharedAncestorPackages() {
        manager.addReferencesFromPackage(PACKAGE1, ['aaa.bbb.ccc'] as Set)
        manager.addReferencesFromPackage(PACKAGE2, ['aaa.bbb.ccc'] as Set)
        manager.addReferencesFromPackage(PACKAGE3, ['aaa.ddd'] as Set)

        assertMetricResult(manager.getPackageMetricResult('aaa.bbb.ccc'), [count:1, value:2, total:2, average:2, referencedFromPackages:[PACKAGE1, PACKAGE2]])
        assertMetricResult(manager.getPackageMetricResult('aaa.bbb'), [count:1, value:0, total:2, average:2, referencedFromPackages:null])
        assertMetricResult(manager.getPackageMetricResult('aaa.ddd'), [count:1, value:1, total:1, average:1, referencedFromPackages:[PACKAGE3]])
        assertMetricResult(manager.getPackageMetricResult('aaa'), [count:2, value:0, total:3, average:1.5, referencedFromPackages:null])
    }

    void testAddReferencesFromPackage_OnlyAddsEachPackageOnce() {
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2, PACKAGE3] as Set)
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE3, PACKAGE4] as Set)
        assert manager.getReferencesToPackage(PACKAGE2) == [PACKAGE1] as Set
        assert manager.getReferencesToPackage(PACKAGE3) == [PACKAGE1] as Set
        assert manager.getReferencesToPackage(PACKAGE4) == [PACKAGE1] as Set
    }

    // Tests for getPackageMetricResult()

    void testGetPackageMetricResult_InitializedToEmpty() {
        def metricResult = manager.getPackageMetricResult(PACKAGE1)
        assertMetricResult(metricResult, [(REFERENCED_FROM_PACKAGES):null, count:0, value:0, total:0, average:0])
    }

    void testGetPackageMetricResult_ReflectsAddedPackageReferences() {
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2, PACKAGE3] as Set)
        manager.addReferencesFromPackage(PACKAGE2, [PACKAGE3] as Set)
        assertMetricResult(manager.getPackageMetricResult(PACKAGE1), [(REFERENCED_FROM_PACKAGES):null, count:1, value:0, total:0, average:0])
        assertMetricResult(manager.getPackageMetricResult(PACKAGE2), [(REFERENCED_FROM_PACKAGES):[PACKAGE1] as Set, count:1, value:1, total:1, average:1])
        assertMetricResult(manager.getPackageMetricResult(PACKAGE3), [(REFERENCED_FROM_PACKAGES):[PACKAGE1, PACKAGE2] as Set, count:1, value:2, total:2, average:2])
    }

    void testGetPackageMetricResult_AlwaysReturnsSameInstanceForPackage() {
        def metricResult = manager.getPackageMetricResult(PACKAGE1)
        manager.addReferencesFromPackage(PACKAGE2, [PACKAGE1] as Set)
        assert manager.getPackageMetricResult(PACKAGE1) == metricResult
    }

    // Tests for registerPackageContainingClasses()

    void testRegisterPackageContainingClasses_IncrementsCount_FirstTimeOnly() {
        manager.registerPackageContainingClasses(PACKAGE1)
        assert manager.getPackageMetricResult(PACKAGE1).count == 1

        manager.registerPackageContainingClasses(PACKAGE1)
        assert manager.getPackageMetricResult(PACKAGE1).count == 1
    }

    void testRegisterPackageContainingClasses_IncrementsCountForAncestorPackages() {
        manager.registerPackageContainingClasses('aa.bb.cc')
        assert manager.getPackageMetricResult('aa.bb.cc').count == 1
        assert manager.getPackageMetricResult('aa.bb').count == 1
        assert manager.getPackageMetricResult('aa').count == 1

        manager.registerPackageContainingClasses('aa.dd')
        assert manager.getPackageMetricResult('aa.dd').count == 1
        assert manager.getPackageMetricResult('aa').count == 2
    }

    void testRegisterPackageContainingClasses_IncrementsCountAndRecalculatesAverage() {
        manager.addReferencesFromPackage(PACKAGE1, ['aa.bb'] as Set)
        manager.registerPackageContainingClasses('aa')
        assert manager.getPackageMetricResult('aa').count == 2
        assert manager.getPackageMetricResult('aa').map[AVERAGE] == 0.5
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    private void assertMetricResult(MetricResult metricResult, Map expectedResultValues) {
        log(metricResult)
        assert metricResult.metric == METRIC
        assert metricResult.metricLevel == MetricLevel.PACKAGE
        assert metricResult.count == expectedResultValues['count']
        assert metricResult[VALUE] == expectedResultValues[VALUE]
        assert metricResult[TOTAL] == expectedResultValues[TOTAL]
        assert metricResult[AVERAGE] == expectedResultValues[AVERAGE]
        assert metricResult[REFERENCED_FROM_PACKAGES] == expectedResultValues[REFERENCED_FROM_PACKAGES] as Set
    }

}