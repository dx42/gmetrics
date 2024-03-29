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
 package org.gmetrics.metric

import org.junit.jupiter.api.Test

/**
 * Abstract superclass for tests common to all Metric classes.
 *
 * Subclasses must define a property named 'metricClass' that specifies the Metric class under test.
 *
 * @author Chris Mair
 */
abstract class AbstractCommonMetricTestCase extends AbstractMetricTestCase {

    protected static final SOURCE = '''
        class MyClass {
            int myValue
            def myMethod() { }
        }
    '''

    @Test
	void testImplementsMetricInterface() {
        assert metric instanceof Metric
    }

    @Test
	void testFunctions_DefaultsTo_TotalAndAverage() {
        assert metric.getFunctions() == ['total', 'average']
    }

    @Test
	void testEnabledFalse_ReturnsNullFor_ApplyToPackage() {
        metric.enabled = false
        assert metric.applyToPackage(null, null, []) == null
    }

    @Test
	void testEnabledFalse_ReturnsNullFor_ApplyToClass() {
        metric.enabled = false
        assert applyToClass(SOURCE) == null
    }

    @Test
	void testApplyToClass_SetsLineNumber() {
        def results = applyToClass(SOURCE)
        assert results.classMetricResult.lineNumber == 2
    }
}
