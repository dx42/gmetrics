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
package org.gmetrics.metric.crap

import org.gmetrics.metric.AbstractCommonMethodMetricTestCase
import org.gmetrics.metric.MethodMetric
import org.gmetrics.result.StubMetricResult
import org.junit.jupiter.api.BeforeEach

/**
 * Tests for CrapMetric - common tests
 *
 * @author Chris Mair
 */
class CrapMetric_CommonTest extends AbstractCommonMethodMetricTestCase {

    static Class metricClass = CrapMetric
    static boolean doesMetricTreatClosuresAsMethods = false

    @BeforeEach
    void setUp() {
        metric.coverageMetric = [applyToMethod:{ methodNode, sourceCode -> new StubMetricResult(total:0.0) }] as MethodMetric
    }

}
