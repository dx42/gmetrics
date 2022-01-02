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

import static org.gmetrics.metric.coupling.AfferentCouplingReferenceManager.REFERENCED_FROM_PACKAGES
import static org.gmetrics.metric.coupling.EfferentCouplingReferenceManager.REFERENCED_PACKAGES
import static org.gmetrics.result.FunctionNames.*

import org.gmetrics.metric.MetricLevel
import org.gmetrics.metric.StubMetric
import org.gmetrics.result.MetricResult
import org.gmetrics.test.AbstractTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Abstract superclass for Afferent/EfferentCouplingReferenceManager tests
 *
 * @author Chris Mair
 */
abstract class AbstractCouplingReferenceManagerTestCase extends AbstractTestCase {

    protected static final StubMetric METRIC = new StubMetric()
    protected static final String PACKAGE1 = 'a.b.package1'
    protected static final String PACKAGE2 = 'c.d.package2'
    protected static final String PACKAGE3 = 'e.f.package3'
    protected static final String PACKAGE4 = 'g.h.package4'

    protected manager

    //------------------------------------------------------------------------------------
    // Common Tests
    //------------------------------------------------------------------------------------

    // Tests for addReferencesFromPackage()

    @Test
	void testAddReferencesFromPackage_StoresReferences() {
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2, PACKAGE3])
        manager.addReferencesFromPackage(PACKAGE2, [PACKAGE1])
        assert manager.getReferencesFromPackage(PACKAGE1) == [PACKAGE2, PACKAGE3] as Set
        assert manager.getReferencesFromPackage(PACKAGE2) == [PACKAGE1] as Set
    }

    @Test
	void testAddReferencesFromPackage_AggregatesReferences() {
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2, PACKAGE3])
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2, PACKAGE4])
        assert manager.getReferencesFromPackage(PACKAGE1) == [PACKAGE2, PACKAGE3, PACKAGE4] as Set
    }

    @Test
	void testAddReferencesFromPackage_UpdatesCountForFromPackage() {
        manager.addReferencesFromPackage(PACKAGE1, [PACKAGE2, PACKAGE3])
        assert manager.getPackageMetricResult(PACKAGE1).count == 1
    }

    @Test
	void testAddReferencesFromPackage_NormalizesPackageNames() {
        manager.addReferencesFromPackage('aa/bb', ['bb/cc', 'cc/dd'])
        assert manager.getReferencesFromPackage('aa.bb') == ['bb.cc', 'cc.dd'] as Set
    }

    @Test
	void testAddReferencesFromPackage_HandlesNullPackageName() {
        manager.addReferencesFromPackage(null, ['aa'])
        assert manager.getReferencesFromPackage(null) == ['aa'] as Set
    }

    @Test
	void testAddReferencesFromPackage_UnknownPackage_ReturnsEmptySet() {
        assert manager.getReferencesFromPackage('aa.bb') == [] as Set
    }

    @Test
	void testGetReferencesFromPackage_NormalizesPackageName() {
        manager.addReferencesFromPackage('aa.bb', ['bb.cc', 'cc/dd'])
        assert manager.getReferencesFromPackage('aa/bb') == ['bb.cc', 'cc.dd'] as Set
    }

    // Tests for getPackageMetricResult()

    @Test
	void testGetPackageMetricResult_AlwaysReturnsSameInstanceForPackage() {
        def metricResult = manager.getPackageMetricResult('aa/bb')
        manager.addReferencesFromPackage(PACKAGE1, ['aa/bb'])
        assert manager.getPackageMetricResult('aa.bb') == metricResult
    }

    @Test
	void testNormalizePackageName() {
        assert manager.normalizePackageName(' ') == ' '
        assert manager.normalizePackageName('a.b') == 'a.b'
        assert manager.normalizePackageName('a/b') == 'a.b'
        assert manager.normalizePackageName('/a/b/c/d') == '.a.b.c.d'
        assert manager.normalizePackageName('/') == '.'
        assert manager.normalizePackageName(null) == EfferentCouplingReferenceManager.ROOT
        assert manager.normalizePackageName('') == EfferentCouplingReferenceManager.ROOT
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    protected abstract createManager()

    @BeforeEach
    void setUp_AbstractCouplingReferenceManagerTestCase() {
        manager = createManager()
    }

    protected void assertMetricResult(MetricResult metricResult, Map expectedResultValues) {
        log(metricResult)
        assert metricResult.metric == METRIC
        assert metricResult.metricLevel == MetricLevel.PACKAGE
        assert metricResult.count == expectedResultValues['count']
        assert metricResult[VALUE] == expectedResultValues[VALUE]
        assert metricResult[TOTAL] == expectedResultValues[TOTAL]
        assert metricResult[AVERAGE] == expectedResultValues[AVERAGE]
        if (expectedResultValues.containsKey(REFERENCED_FROM_PACKAGES)) {
            assert metricResult[REFERENCED_FROM_PACKAGES] == expectedResultValues[REFERENCED_FROM_PACKAGES] as Set
        }
        if (expectedResultValues.containsKey(REFERENCED_PACKAGES)) {
            assert metricResult[REFERENCED_PACKAGES] == expectedResultValues[REFERENCED_PACKAGES] as Set
        }
    }

}