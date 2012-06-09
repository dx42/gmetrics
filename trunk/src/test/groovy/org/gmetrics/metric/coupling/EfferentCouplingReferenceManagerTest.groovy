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

import static EfferentCouplingReferenceManager.REFERENCED_PACKAGES

/**
 * Tests for EfferentCouplingReferenceManager
 *
 * @author Chris Mair
 */
class EfferentCouplingReferenceManagerTest extends AbstractCouplingReferenceManagerTestCase {

    protected createManager() {
        new EfferentCouplingReferenceManager(METRIC)
    }

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    void testConstructor_NullMetric_ThrowsException() {
        shouldFailWithMessageContaining('metric') { new EfferentCouplingReferenceManager(null) }
    }

    void testConstructor_AssignsMetric() {
        assert new EfferentCouplingReferenceManager(METRIC).metric == METRIC
    }

    // Tests for updateStatisticsForAllPackages()

    void testUpdateStatisticsForAllPackages_UpdatesStatisticsForSingleReferencedPackage() {
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2])
        assertMetricResult(manager.getPackageMetricResult(PACKAGE1), [referencedPackages:[], count:1, value:0, total:0, average:0])
        manager.updateStatisticsForAllPackages()
        assertMetricResult(manager.getPackageMetricResult(PACKAGE1), [referencedPackages:[PACKAGE2], count:1, value:1, total:1, average:1])
        assertMetricResult(manager.getPackageMetricResult(null), [referencedPackages:[], count:2, value:0, total:1, average:0.5])
    }

    void testUpdateStatisticsForAllPackages_OnlyIncludesPackagesThatWereProcessed() {
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2, PACKAGE3])
        manager.updateStatisticsForAllPackages()
        assertMetricResult(manager.getPackageMetricResult(PACKAGE1), [referencedPackages:[PACKAGE2], count:1, value:1, total:1, average:1])
    }

    void testUpdateStatisticsForAllPackages_UpdatesForAncestorPackages() {
        manager.addReferencesFromPackage('aa.bb.cc', [PACKAGE1, PACKAGE2])
        manager.updateStatisticsForAllPackages()

        assertMetricResult(manager.getPackageMetricResult('aa.bb.cc'), [count:1, value:2, total:2, average:2, referencedPackages:[PACKAGE1, PACKAGE2]])
        assertMetricResult(manager.getPackageMetricResult('aa.bb'), [count:1, value:0, total:2, average:2, referencedPackages:[]])
        assertMetricResult(manager.getPackageMetricResult('aa'), [count:1, value:0, total:2, average:2, referencedPackages:[]])
        assertMetricResult(manager.getPackageMetricResult(null), [count:3, value:0, total:2, average:0.67, referencedPackages:[]])
    }

    void testAddReferencesFromPackage_IncrementsTotalForSharedAncestorPackages() {
        manager.addReferencesFromPackage('aa.bb.cc', [PACKAGE1])
        manager.addReferencesFromPackage('aa.bb.dd', [])
        manager.addReferencesFromPackage('aa.dd', [PACKAGE1, PACKAGE2])
        manager.updateStatisticsForAllPackages()

        assertMetricResult(manager.getPackageMetricResult('aa.bb.cc'), [count:1, value:1, total:1, average:1, referencedPackages:[PACKAGE1]])
        assertMetricResult(manager.getPackageMetricResult('aa.bb.dd'), [count:1, value:0, total:0, average:0, referencedPackages:[]])
        assertMetricResult(manager.getPackageMetricResult('aa/bb'), [count:2, value:0, total:1, average:0.5, referencedPackages:[]])
        assertMetricResult(manager.getPackageMetricResult('aa.dd'), [count:1, value:2, total:2, average:2, referencedPackages:[PACKAGE1, PACKAGE2]])
        assertMetricResult(manager.getPackageMetricResult('aa'), [count:3, value:0, total:3, average:1, referencedPackages:[]])
    }

    // Tests for getPackageMetricResult()

    void testGetPackageMetricResult_InitializedToEmpty() {
        def metricResult = manager.getPackageMetricResult('some.package')
        assertMetricResult(metricResult, [(REFERENCED_PACKAGES):[], count:0, value:0, total:0, average:0])
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    void setUp() {
        super.setUp()
        manager.addReferencesFromPackage(PACKAGE1, [])
        manager.addReferencesFromPackage(PACKAGE2, [])
    }

}