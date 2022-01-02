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
package org.gmetrics.metricset

import org.gmetrics.metric.StubMetric
import org.gmetrics.metric.abc.AbcMetric
import org.gmetrics.metric.crap.CrapMetric
import org.gmetrics.metricregistry.MetricRegistry
import org.gmetrics.metricregistry.MetricRegistryHolder
import org.gmetrics.test.AbstractTestCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for MetricSetBuilder
 *
 * @author Chris Mair
 */
class MetricSetBuilderTest extends AbstractTestCase {

    private MetricSetBuilder metricSetBuilder
    private newMetric
    private originalMetricRegistry

    @Test
	void testMetricset_NullFilename() {
        log(metricSetBuilder)
        shouldFailWithMessageContaining('path') {
            metricSetBuilder.metricset {
                metricset(null)
            }
        }
    }

    @Test
	void testMetricset_GroovyFile_MetricSetFileDoesNotExist() {
        shouldFailWithMessageContaining('DoesNotExist.groovy') {
            metricSetBuilder.metricset {
                metricset('DoesNotExist.groovy')
            }
        }
    }

    @Test
	void testMetricset_GroovyFile_ConfigureMetricUsingMap() {
        metricSetBuilder.metricset {
            metricset(MetricSetTestFiles.METRICSET1) {
                'Stub' otherProperty:'888', name:'NewName'
            }
        }
        assertMetricNames('NewName', 'XXX')
        assertMetricProperties('NewName', [otherProperty:'888'])
    }

    @Test
	void testMetricset_GroovyFile_NestedMetricDefinitionNotIncludedInMetricSet() {
        metricSetBuilder.metricset {
            metricset(MetricSetTestFiles.METRICSET1) {
                Stub {
                    otherProperty = ABC
                }
            }
        }
        assertMetricNames('Stub', 'XXX')
    }

    @Test
	void testMetricset_GroovyFile_ConfigureMetricUsingMap_MetricNotFound() {
        shouldFailWithMessageContaining('NotFound') {
            metricSetBuilder.metricset {
                metricset(MetricSetTestFiles.METRICSET1) {
                    'NotFound' otherProperty:'abc'
                }
            }
        }
    }

    @Test
	void testMetricset_GroovyFile_ConfigureMetricUsingClosure() {
        metricSetBuilder.metricset {
            metricset(MetricSetTestFiles.METRICSET1) {
                Stub {
                    otherProperty = '999'
                }
            }
        }
        assertMetricNames('Stub', 'XXX')
        assertMetricProperties('Stub', [otherProperty:'999'])
    }

    @Test
	void testMetricset_GroovyFile_ConfigureMetricUsingClosure_MetricNotFound() {
        shouldFailWithMessageContaining('NotFound') {
            metricSetBuilder.metricset {
                metricset(MetricSetTestFiles.METRICSET1) {
                    'NotFound' {
                        otherProperty = 'abc'
                    }
                }
            }
        }
    }

    @Test
	void testMetricset_GroovyFile_NoClosure() {
        metricSetBuilder.metricset {
            metricset(MetricSetTestFiles.METRICSET1)
        }
        assertMetricNames('Stub', 'XXX')
    }

    @Test
	void testMetric_Class_Map() {
        metricSetBuilder.metricset {
            newMetric = metric(AbcMetric, [enabled:false])
        }
        assertMetricNames('ABC')
        assert !findMetric('ABC').enabled
        assert newMetric instanceof AbcMetric
    }

    @Test
	void testMetric_Class_NoClosure() {
        metricSetBuilder.metricset {
            newMetric = metric(AbcMetric)
        }
        assertMetricNames('ABC')
        assert newMetric instanceof AbcMetric
    }

    @Test
	void testMetric_Class_NoClosure_NullMetricClass() {
        shouldFailWithMessageContaining('metricClass') {
            metricSetBuilder.metricset {
                metric(null)
            }
        }
    }

    @Test
	void testMetric_Class_NoClosure_ClassDoesNotImplementRuleInterface() {
        shouldFailWithMessageContaining('metricClass') {
            metricSetBuilder.metricset {
                metric(this.class)
            }
        }
    }

    @Test
	void testMetric_Class_Closure() {
        metricSetBuilder.metricset {
            metric(StubMetric) {
                name = 'xxx'
            }
            newMetric = metric(StubMetric) {
                name = 'yyyy'
                otherProperty = '1234'
            }

        }
        assertMetricNames('xxx', 'yyyy')
        assert findMetric('yyyy').otherProperty == '1234'
        assert newMetric.name == 'yyyy'
    }

    @Test
	void testMetric_Class_Closure_NullRuleClass() {
        shouldFailWithMessageContaining('metricClass') {
            metricSetBuilder.metricset {
                metric((Class)null) {
                    name = 'xxx'
                }
            }
        }
    }

    @Test
	void testMetric_Class_Closure_ClassDoesNotImplementMetricInterface() {
        shouldFailWithMessageContaining('metricClass') {
            metricSetBuilder.metricset {
                metric(this.class) {
                    name = 'xxx'
                }
            }
        }
    }

    @Test
	void testMetric_MetricName_EmptyParentheses() {
        metricSetBuilder.metricset {
            newMetric = ABC()
        }
        assertMetricNames('ABC')
        assert newMetric instanceof AbcMetric
    }

    @Test
	void testMetric_MetricName_ParenthesesWithMap() {
        metricSetBuilder.metricset {
            newMetric = ABC([enabled:false])
        }
        assertMetricNames('ABC')
        assert !findMetric('ABC').enabled
        assert newMetric instanceof AbcMetric
    }

    @Test
	void testMetric_MetricName_NoParenthesesWithClosure() {
        metricSetBuilder.metricset {
            newMetric = ABC {
                enabled = false
            }
        }
        assertMetricNames('ABC')
        assert !findMetric('ABC').enabled
        assert newMetric instanceof AbcMetric
    }

    @Test
	void testMetric_NestedMetricDefinitionUsingMetric_NotIncludedInMetricSet() {
        metricSetBuilder.metricset {
            CRAP {
                coverageMetric = metric(StubMetric)
            }
        }
        assertMetricNames('CRAP')
    }

    @Test
	void testMetric_NestedMetricDefinitionUsingMetricWithMap_NotIncludedInMetricSet() {
        metricSetBuilder.metricset {
            CRAP {
                coverageMetric = metric(StubMetric, [name:'yyyy'])
            }
        }
        assertMetricNames('CRAP')
    }

    @Test
	void testMetric_NestedMetricDefinitionUsingMetricWithClosure_NotIncludedInMetricSet() {
        metricSetBuilder.metricset {
            CRAP {
                coverageMetric = metric(StubMetric) {
                    name = 'yyyy'
                    otherProperty = '1234'
                }
            }
        }
        assertMetricNames('CRAP')
    }

    @Test
	void testMetric_NestedMetricDefinition_AssignNestedMetricWithinMap_KnownLimitation() {
        metricSetBuilder.metricset {
            CRAP(coverageMetric:ABC)
        }
        // Known limitation *****
        assertMetricNames('ABC', 'CRAP')
    }

    @Test
	void testMetric_NestedMetricDefinitionUsingMetricNameWithClosure_NotIncludedInMetricSet() {
        metricSetBuilder.metricset {
            CRAP {
                coverageMetric = ABC {
                    enabled = false
                }
            }
        }
        assertMetricNames('CRAP')
    }

    @Test
	void testMetric_NestedMetricDefinitionUsingMetricNameWithMap_IsIncludedInMetricSet() {
        metricSetBuilder.metricset {
            CRAP([coverageMetric: ABC(enabled:false)])
        }
        assertMetricNames('ABC', 'CRAP')
    }

    @Test
	void testMetric_MetricName_NoParenthesesOrClosure() {
        metricSetBuilder.metricset {
            newMetric = ABC
        }
        assertMetricNames('ABC')
        assert newMetric instanceof AbcMetric
    }

    @Test
	void testMetric_MetricName_NoSuchMetricName() {
        MetricRegistryHolder.metricRegistry = [getMetricClass:{ null }] as MetricRegistry
        shouldFailWithMessageContaining('DoesNotExist') {
            metricSetBuilder.metricset {
                DoesNotExist
            }
        }
    }

    @Test
	void testDescription() {
        metricSetBuilder.metricset {
            description 'abc'
        }
    }

    //------------------------------------------------------------------------------------
    // Setup and helper methods
    //------------------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        metricSetBuilder = new MetricSetBuilder()
        final METRICS = [CRAP:CrapMetric, ABC:AbcMetric]
        originalMetricRegistry = MetricRegistryHolder.metricRegistry
        MetricRegistryHolder.metricRegistry = [getMetricClass:{ name -> METRICS[name] }] as MetricRegistry
    }

    @AfterEach
    void tearDown() {
        MetricRegistryHolder.metricRegistry = originalMetricRegistry
    }

    private MetricSet getMetricSet() {
        metricSetBuilder.getMetricSet()
    }

    private void assertMetricNames(String[] names) {
        assert getMetricSet().metrics*.name == names
    }

    private void assertMetricProperties(String metricName, Map properties) {
        def metric = findMetric(metricName)
        properties.each { key, value -> assert metric[key] == value }
    }

    private findMetric(String name) {
        getMetricSet().metrics.find { metric -> metric.name == name }
    }
}
