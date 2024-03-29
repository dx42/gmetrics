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

import org.gmetrics.metric.PostProcessingMetric
import org.gmetrics.result.FunctionNames
import org.junit.jupiter.api.Test

/**
 * Tests for applying AfferentCouplingMetric at the class level
 *
 * @see AfferentCouplingMetric_PackageTest
 *
 * @author Chris Mair
 */
class AfferentCouplingMetric_ClassTest extends AbstractPackageCouplingMetric_ClassTestCase {

    static Class metricClass = AfferentCouplingMetric

    //------------------------------------------------------------------------------------
    // Additional Test (beyond tests from superclass)
    //------------------------------------------------------------------------------------

    @Test
	void testMetricName() {
        assert metric.name == 'AfferentCoupling'
    }

    @Test
	void testFunctions() {
        assert metric.functions == [FunctionNames.VALUE, FunctionNames.AVERAGE]
    }

    @Test
	void testMetricImplementsPostProcessingMetricInterface() {
        assert metric instanceof PostProcessingMetric
    }

}