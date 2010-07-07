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
package org.gmetrics.metric

/**
 * Abstract superclass for tests common to all Metric classes.
 *
 * Subclasses must define a property named 'metricClass' that specifies the Metric class under test.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbstractCommonMetricTestCase extends AbstractMetricTestCase {
    private static final SOURCE = '''
        class MyClass {
            int myValue
            def myMethod() { }
        }
    '''

    void testImplementsMetricInterface() {
        assert metric instanceof Metric
    }

    void testFunctionNames_DefaultsTo_TotalAndAverage() {
        assert metric.getFunctionNames() == ['total', 'average']
    }

    void testEnabledFalse_ReturnsNullFor_ApplyToPackage() {
        metric.enabled = false
        assert metric.applyToPackage([]) == null
    }

    void testEnabledFalse_ReturnsNullFor_ApplyToClass() {
        metric.enabled = false
        assert applyToClass(SOURCE) == null
    }

}
