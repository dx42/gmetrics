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

import org.junit.Test

/**
 * Abstract superclass for tests common to all method-level Metric classes.
 *
 * Subclasses must define a property named 'metricClass' that specifies the Metric class under test.
 *
 * @author Chris Mair
 */
abstract class AbstractCommonMethodMetricTestCase extends AbstractCommonMetricTestCase {

    private static final SOURCE = '''
        class MyClass {
            def myClosure = { }
            def myMethod() { }
        }
    '''

    static boolean doesMetricTreatClosuresAsMethods = true

    @Test
	void testImplementsMethodMetricInterface() {
        assert metric instanceof MethodMetric
    }

    @Test
	void testApplyToMethod_EnabledIsFalse_ReturnsNull() {
        metric.enabled = false
        assert applyToMethod(SOURCE) == null
    }

    @Test
	void testApplyToClosure_EnabledIsFalse_ReturnsNull() {
        metric.enabled = false
        assert applyToClosure(SOURCE) == null
    }

    @Test
	void testCalculate_Method_SetsLineNumber() {
        final SOURCE = """
            def myMethod() { }
        """
        def metricResult = metric.calculate(findFirstMethod(SOURCE), sourceCode)
        assert metricResult.lineNumber == 2
    }

    @Test
	void testCalculate_ClosureField_SetsLineNumber() {
        def metricResult = metric.calculate(findFirstField(SOURCE).initialExpression, sourceCode)
        if (getProperty('doesMetricTreatClosuresAsMethods')) {
            assert metricResult.lineNumber == 3
        }
        else {
            assert metricResult == null
        }
    }

    @Test
	void testApplyToClass_IncludeClosureFieldsIsFalse_ReturnsNull() {
        final SOURCE = """
            class MyClass {
                def myClosure = { }
            }
        """
        metric.includeClosureFields = false
        assert applyToClass(SOURCE) == null
    }

}