/*
 * Copyright 2011 the original author or authors.
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

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.abc.AbcMetric
import org.gmetrics.metric.StubMetric
import org.gmetrics.metricregistry.MetricRegistryHolder
import org.gmetrics.metricregistry.MetricRegistry
import org.gmetrics.metric.crap.CrapMetric

/**
 * Tests for MetricSetBuilder
 *
 * @author Chris Mair
 */
class MetricSetBuilderTest extends AbstractTestCase {

    private metricSetBuilder
    private newMetric

    void testMetricset_NullFilename() {
        log(metricSetBuilder)
        shouldFailWithMessageContaining('path') {
            metricSetBuilder.metricset {
                metricset(null)
            }
        }
    }

    void testMetricset_GroovyFile_MetricSetFileDoesNotExist() {
        shouldFailWithMessageContaining('DoesNotExist.groovy') {
            metricSetBuilder.metricset {
                metricset('DoesNotExist.groovy')
            }
        }
    }

    void testMetricset_GroovyFile_ConfigureMetricUsingMap() {
        metricSetBuilder.metricset {
            metricset(MetricSetTestFiles.METRICSET1) {
                'Stub' otherProperty:'888', name:'NewName'
            }
        }
        assertMetricNames('NewName', 'XXX')
        assertMetricProperties('NewName', [otherProperty:'888'])
    }

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

    void testMetricset_GroovyFile_ConfigureMetricUsingMap_MetricNotFound() {
        shouldFailWithMessageContaining('NotFound') {
            metricSetBuilder.metricset {
                metricset(MetricSetTestFiles.METRICSET1) {
                    'NotFound' otherProperty:'abc'
                }
            }
        }
    }

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

    void testMetricset_GroovyFile_NoClosure() {
        metricSetBuilder.metricset {
            metricset(MetricSetTestFiles.METRICSET1)
        }
        assertMetricNames('Stub', 'XXX')
    }

    void testMetric_Class_Map() {
        metricSetBuilder.metricset {
            newMetric = metric(AbcMetric, [enabled:false])
        }
        assertMetricNames('ABC')
        assert !findMetric('ABC').enabled
        assert newMetric instanceof AbcMetric
    }

    void testMetric_Class_NoClosure() {
        metricSetBuilder.metricset {
            newMetric = metric(AbcMetric)
        }
        assertMetricNames('ABC')
        assert newMetric instanceof AbcMetric
    }

    void testMetric_Class_NoClosure_NullMetricClass() {
        shouldFailWithMessageContaining('metricClass') {
            metricSetBuilder.metricset {
                metric(null)
            }
        }
    }

    void testMetric_Class_NoClosure_ClassDoesNotImplementRuleInterface() {
        shouldFailWithMessageContaining('metricClass') {
            metricSetBuilder.metricset {
                metric(this.class)
            }
        }
    }

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

    void testMetric_Class_Closure_NullRuleClass() {
        shouldFailWithMessageContaining('metricClass') {
            metricSetBuilder.metricset {
                metric((Class)null) {
                    name = 'xxx'
                }
            }
        }
    }

    void testMetric_Class_Closure_ClassDoesNotImplementMetricInterface() {
        shouldFailWithMessageContaining('metricClass') {
            metricSetBuilder.metricset {
                metric(this.class) {
                    name = 'xxx'
                }
            }
        }
    }

    void testMetric_MetricName_EmptyParentheses() {
        metricSetBuilder.metricset {
            newMetric = ABC()
        }
        assertMetricNames('ABC')
        assert newMetric instanceof AbcMetric
    }

    void testMetric_MetricName_ParenthesesWithMap() {
        metricSetBuilder.metricset {
            newMetric = ABC([enabled:false])
        }
        assertMetricNames('ABC')
        assert !findMetric('ABC').enabled
        assert newMetric instanceof AbcMetric
    }

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

    void testMetric_NestedMetricDefinitionUsingMetric_NotIncludedInMetricSet() {
        metricSetBuilder.metricset {
            CRAP {
                coverageMetric = metric(StubMetric)
            }
        }
        assertMetricNames('CRAP')
    }

    void testMetric_NestedMetricDefinitionUsingMetricWithMap_NotIncludedInMetricSet() {
        metricSetBuilder.metricset {
            CRAP {
                coverageMetric = metric(StubMetric, [name:'yyyy'])
            }
        }
        assertMetricNames('CRAP')
    }

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

    void testMetric_NestedMetricDefinition_AssignNestedMetricWithinMap_KnownLimitation() {
        metricSetBuilder.metricset {
            CRAP(coverageMetric:ABC)
        }
        // Known limitation *****
        assertMetricNames('ABC', 'CRAP')
    }

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

    void testMetric_NestedMetricDefinitionUsingMetricNameWithMap_IsIncludedInMetricSet() {
        metricSetBuilder.metricset {
            CRAP([coverageMetric: ABC(enabled:false)])
        }
        assertMetricNames('ABC', 'CRAP')
    }

    void testMetric_MetricName_NoParenthesesOrClosure() {
        metricSetBuilder.metricset {
            newMetric = ABC
        }
        assertMetricNames('ABC')
        assert newMetric instanceof AbcMetric
    }

    void testMetric_MetricName_NoSuchMetricName() {
        MetricRegistryHolder.metricRegistry = [getMetricClass:{ null }] as MetricRegistry
        shouldFailWithMessageContaining('DoesNotExist') {
            metricSetBuilder.metricset {
                DoesNotExist
            }
        }
    }

    void testDescription() {
        metricSetBuilder.metricset {
            description 'abc'
        }
    }

    //------------------------------------------------------------------------------------
    // Setup and helper methods
    //------------------------------------------------------------------------------------

    void setUp() {
        super.setUp()
        metricSetBuilder = new MetricSetBuilder()
        final METRICS = [CRAP:CrapMetric, ABC:AbcMetric]
        MetricRegistryHolder.metricRegistry = [getMetricClass:{ name -> METRICS[name] }] as MetricRegistry
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
