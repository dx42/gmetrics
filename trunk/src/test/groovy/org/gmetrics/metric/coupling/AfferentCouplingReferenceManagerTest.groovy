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

import static AfferentCouplingReferenceManager.REFERENCED_FROM_PACKAGES
import static org.gmetrics.result.FunctionNames.*

import org.gmetrics.metric.MetricLevel
import org.gmetrics.metric.StubMetric
import org.gmetrics.result.MetricResult
import org.gmetrics.test.AbstractTestCase

/**
 * Tests for AfferentCouplingReferenceManager
 *
 * @author Chris Mair
 */
class AfferentCouplingReferenceManagerTest extends AbstractTestCase {

    private static final METRIC = new StubMetric()
    private static final PACKAGE1 = 'a.b.package1'
    private static final PACKAGE2 = 'c.d.package2'
    private static final PACKAGE3 = 'e.f.package3'
    private static final PACKAGE4 = 'g.h.package4'

    private AfferentCouplingReferenceManager manager = new AfferentCouplingReferenceManager(METRIC)

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    void testConstructor_NullMetric_ThrowsException() {
        shouldFailWithMessageContaining('metric') { new AfferentCouplingReferenceManager(null) }
    }

    void testConstructor_AssignsMetric() {
        assert new AfferentCouplingReferenceManager(METRIC).metric == METRIC
    }

    // Tests for addReferencesFromPackage()

    void testAddReferencesFromPackage_StoresReferences() {
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2, PACKAGE3])
        manager.addReferencesFromPackage(PACKAGE2, [PACKAGE1])
        assert manager.getReferencesFromPackage(PACKAGE1) == [PACKAGE2, PACKAGE3] as Set
        assert manager.getReferencesFromPackage(PACKAGE2) == [PACKAGE1] as Set
    }

    void testAddReferencesFromPackage_AggregatesReferences() {
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2, PACKAGE3])
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2, PACKAGE4])
        assert manager.getReferencesFromPackage(PACKAGE1) == [PACKAGE2, PACKAGE3, PACKAGE4] as Set
    }

    void testAddReferencesFromPackage_UpdatesCountForFromPackage() {
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2, PACKAGE3])
        assert manager.getPackageMetricResult(PACKAGE1).count == 1
    }

    void testAddReferencesFromPackage_NormalizesPackageNames() {
        manager.addReferencesFromPackage('aa/bb', ['bb/cc', 'cc/dd'])
        assert manager.getReferencesFromPackage('aa.bb') == ['bb.cc', 'cc.dd'] as Set
    }

    void testAddReferencesFromPackage_HandlesNullPackageName() {
        manager.addReferencesFromPackage(null, ['aa'])
        assert manager.getReferencesFromPackage(null) == ['aa'] as Set
    }

    void testAddReferencesFromPackage_UnknownPackage_ReturnsEmptySet() {
        assert manager.getReferencesFromPackage('aa.bb') == [] as Set
    }

    void testGetReferencesFromPackage_NormalizesPackageName() {
        manager.addReferencesFromPackage('aa.bb', ['bb.cc', 'cc/dd'])
        assert manager.getReferencesFromPackage('aa/bb') == ['bb.cc', 'cc.dd'] as Set
    }

    // Tests for updateStatisticsForAllPackages()

    void testUpdateStatisticsForAllPackages_UpdatesStatisticsForSingleReferencedPackage() {
        manager.addReferencesFromPackage('bb', [])
        manager.addReferencesFromPackage('aa', ['bb'])
        assertMetricResult(manager.getPackageMetricResult('bb'), [referencedFromPackages:[], count:1, value:0, total:0, average:0])
        manager.updateStatisticsForAllPackages()
        assertMetricResult(manager.getPackageMetricResult('bb'), [referencedFromPackages:['aa'], count:1, value:1, total:1, average:1])
        assertMetricResult(manager.getPackageMetricResult(null), [referencedFromPackages:[], count:2, value:0, total:1, average:0.5])
    }

    void testUpdateStatisticsForAllPackages_UpdatesStatisticsForReferencedPackages() {
        manager.addReferencesFromPackage('dd.ee', [])
        manager.addReferencesFromPackage('aa.bb', ['bb.cc', 'cc/dd'])
        manager.addReferencesFromPackage('aa.bb', ['bb.cc', 'dd.ee'])
        manager.addReferencesFromPackage('bb.cc', ['dd.ee'])
        manager.updateStatisticsForAllPackages()
        assertMetricResult(manager.getPackageMetricResult('aa.bb'), [referencedFromPackages:[], count:1, value:0, total:0, average:0])
        assertMetricResult(manager.getPackageMetricResult('bb.cc'), [referencedFromPackages:['aa.bb'], count:1, value:1, total:1, average:1])
        assertMetricResult(manager.getPackageMetricResult('dd.ee'), [referencedFromPackages:['aa.bb', 'bb.cc'], count:1, value:2, total:2, average:2])
    }

    void testUpdateStatisticsForAllPackages_UpdatesForAncestorPackages() {
        manager.addReferencesFromPackage('aa.bb.cc', [])
        manager.addReferencesFromPackage(PACKAGE1, ['aa.bb.cc'])
        manager.updateStatisticsForAllPackages()

        assertMetricResult(manager.getPackageMetricResult('aa.bb.cc'), [count:1, value:1, total:1, average:1, referencedFromPackages:[PACKAGE1]])
        assertMetricResult(manager.getPackageMetricResult('aa.bb'), [count:1, value:0, total:1, average:1, referencedFromPackages:[]])
        assertMetricResult(manager.getPackageMetricResult('aa'), [count:1, value:0, total:1, average:1, referencedFromPackages:[]])
    }

    void testAddReferencesFromPackage_IncrementsTotalForSharedAncestorPackages() {
        manager.addReferencesFromPackage('aa.bb.cc', [])
        manager.addReferencesFromPackage('aa.dd', [])
        manager.addReferencesFromPackage(PACKAGE1, ['aa.bb.cc'])
        manager.addReferencesFromPackage(PACKAGE2, ['aa/bb/cc'])
        manager.addReferencesFromPackage(PACKAGE3, ['aa/dd'])

        manager.updateStatisticsForAllPackages()
        assertMetricResult(manager.getPackageMetricResult('aa.bb.cc'), [count:1, value:2, total:2, average:2, referencedFromPackages:[PACKAGE1, PACKAGE2]])
        assertMetricResult(manager.getPackageMetricResult('aa/bb'), [count:1, value:0, total:2, average:2, referencedFromPackages:[]])
        assertMetricResult(manager.getPackageMetricResult('aa.dd'), [count:1, value:1, total:1, average:1, referencedFromPackages:[PACKAGE3]])
        assertMetricResult(manager.getPackageMetricResult('aa'), [count:2, value:0, total:3, average:1.5, referencedFromPackages:[]])
    }

    void testAddReferencesFromPackage_DoesNotDoubleCountChildPackages() {
        manager.addReferencesFromPackage('aa.bb.cc.dd', [])
        manager.addReferencesFromPackage(PACKAGE1, ['aa.bb'])
        manager.addReferencesFromPackage(PACKAGE1, ['aa.bb.cc'])
        manager.addReferencesFromPackage(PACKAGE1, ['aa.bb.cc.dd'])

        manager.updateStatisticsForAllPackages()
        assertMetricResult(manager.getPackageMetricResult('aa.bb.cc.dd'), [count:1, value:1, total:1, average:1, referencedFromPackages:[PACKAGE1]])
        assertMetricResult(manager.getPackageMetricResult('aa.bb.cc'), [count:2, value:1, total:2, average:1, referencedFromPackages:[PACKAGE1]])
        assertMetricResult(manager.getPackageMetricResult('aa.bb'), [count:3, value:1, total:3, average:1, referencedFromPackages:[PACKAGE1]])
        assertMetricResult(manager.getPackageMetricResult('aa'), [count:3, value:0, total:3, average:1, referencedFromPackages:[]])
    }

    // Tests for getPackageMetricResult()

    void testGetPackageMetricResult_InitializedToEmpty() {
        def metricResult = manager.getPackageMetricResult(PACKAGE1)
        assertMetricResult(metricResult, [(REFERENCED_FROM_PACKAGES):[], count:0, value:0, total:0, average:0])
    }

    void testGetPackageMetricResult_AlwaysReturnsSameInstanceForPackage() {
        def metricResult = manager.getPackageMetricResult('aa/bb')
        manager.addReferencesFromPackage(PACKAGE1, ['aa/bb'])
        assert manager.getPackageMetricResult('aa.bb') == metricResult
    }

    void testNormalizePackageName() {
        assert manager.normalizePackageName(' ') == ' '
        assert manager.normalizePackageName('a.b') == 'a.b'
        assert manager.normalizePackageName('a/b') == 'a.b'
        assert manager.normalizePackageName('/a/b/c/d') == '.a.b.c.d'
        assert manager.normalizePackageName('/') == '.'
        assert manager.normalizePackageName(null) == AfferentCouplingReferenceManager.ROOT
        assert manager.normalizePackageName('') == AfferentCouplingReferenceManager.ROOT
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