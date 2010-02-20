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

/**
 * Tests for GroovyDslMetricSet
 *
 * @author Chris Mair
 * @version $Revision: 24 $ - $Date: 2009-12-10 21:17:05 -0500 (Thu, 10 Dec 2009) $
 */
class GroovyDslMetricSetTest extends AbstractTestCase {
    private static final PATH = 'metricsets/GroovyMetricSet1.txt'  // groovy files are not on classpath; have to use *.txt
    private static final RELATIVE_PATH = 'file:src/test/resources/' + PATH

    void testImplementsMetricSet() {
        assert new GroovyDslMetricSet(PATH) instanceof MetricSet  
    }

    void testNullPath() {
        shouldFailWithMessageContaining('path') { new GroovyDslMetricSet(null) }
    }

    void testEmptyPath() {
        shouldFailWithMessageContaining('path') { new GroovyDslMetricSet('') }
    }

    void testFileDoesNotExist() {
        def errorMessage = shouldFail { new GroovyDslMetricSet('DoesNotExist.xml') }
        assertContainsAll(errorMessage, ['DoesNotExist.xml', 'does not exist'])
    }

    void testLoadGroovyMetricSet() {
        [PATH, RELATIVE_PATH].each { path ->
            def metrics = loadMetricSetFromFile(path)
            assert metrics*.name == ['Stub', 'ABC']
            assert metrics[0].otherProperty == 'abc'
        }
    }

    void testLoadNestedGroovyMetricSet() {
        final PATH = 'metricsets/GroovyMetricSet2.txt'
        def metrics = loadMetricSetFromFile(PATH)
        assert metrics*.name == ['CustomMetric', 'Stub', 'ABC']
        assert metrics[0].otherProperty == '345'
        assert metrics[1].otherProperty == 'abc'
    }

    void testLoadNestedGroovyMetricSet_CustomizeContainedMetricUsingClosure() {
        final PATH = 'metricsets/GroovyMetricSet3.txt'
        def metrics = loadMetricSetFromFile(PATH)
        assert metrics*.name == ['CustomMetric', 'Stub', 'ABC']
        assert metrics[0].otherProperty == '678'
        assert metrics[1].otherProperty == 'abc'
    }

    private loadMetricSetFromFile(String path) {
        def groovyDslMetricSet = new GroovyDslMetricSet(path)
        def metrics = groovyDslMetricSet.metrics
        log("metrics=$metrics")
        return metrics
    }
}
