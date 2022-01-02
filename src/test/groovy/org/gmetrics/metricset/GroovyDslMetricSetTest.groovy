/*
 * Copyright 2010 the original author or authors.
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
import org.junit.jupiter.api.Test

/**
 * Tests for GroovyDslMetricSet
 *
 * @author Chris Mair
 */
class GroovyDslMetricSetTest extends AbstractTestCase {

    @Test
	void testImplementsMetricSet() {
        assert new GroovyDslMetricSet(MetricSetTestFiles.METRICSET1) instanceof MetricSet  
    }

    @Test
	void testNullPath() {
        shouldFailWithMessageContaining('path') { new GroovyDslMetricSet(null) }
    }

    @Test
	void testEmptyPath() {
        shouldFailWithMessageContaining('path') { new GroovyDslMetricSet('') }
    }

    @Test
	void testFileDoesNotExist() {
        Throwable exception = shouldFail { new GroovyDslMetricSet('DoesNotExist.xml') }
        assertContainsAll(exception.message, ['DoesNotExist.xml', 'does not exist'])
    }

    @Test
	void testLoadGroovyMetricSet() {
        [MetricSetTestFiles.METRICSET1, MetricSetTestFiles.METRICSET1_RELATIVE_PATH].each { path ->
            def metrics = loadMetricSetFromFile(path)
            assert metrics*.name == ['Stub', 'XXX']
            assert metrics[0].otherProperty == 'abc'
        }
    }

    @Test
	void testLoadNestedGroovyMetricSet() {
        def metrics = loadMetricSetFromFile(MetricSetTestFiles.METRICSET2)
        assert metrics*.name == ['CustomMetric', 'Stub', 'XXX']
        assert metrics[0].otherProperty == '345'
        assert metrics[1].otherProperty == 'abc'
    }

    @Test
	void testLoadNestedGroovyMetricSet_CustomizeContainedMetricUsingClosure() {
        def metrics = loadMetricSetFromFile(MetricSetTestFiles.METRICSET3)
        assert metrics*.name == ['CustomMetric', 'Stub', 'XXX']
        assert metrics[0].otherProperty == '678'
        assert metrics[1].otherProperty == 'abc'
    }

    @Test
	void testLoadGroovyMetricSet_SetNonExistentMetricProperty() {
        shouldFailWithMessageContaining('noSuchProperty') { loadMetricSetFromFile('metricsets/GroovyMetricSet_Bad.txt') }
    }

    private loadMetricSetFromFile(String path) {
        def groovyDslMetricSet = new GroovyDslMetricSet(path)
        def metrics = groovyDslMetricSet.metrics
        log("metrics=$metrics")
        return metrics
    }
}
