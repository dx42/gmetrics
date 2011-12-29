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

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metricset.GroovyDslMetricSet

/**
 * Integration test for CrapMetric that loads a MetricSet definition from a MetricSet Groovy DSL file.
 *
 * @author Chris Mair
 */
class CrapMetric_MetricSetTest extends AbstractTestCase {

    void testLoadMetricSet() {
        def metricSet = new GroovyDslMetricSet('crap/CrapMetricSet.txt')
        log metricSet.metrics
        def crapMetric = metricSet.metrics.find { it instanceof CrapMetric }
        log "crapMetric=" + crapMetric

        assert metricSet.metrics.size() == 3
        assert crapMetric.coverageMetric.functions == ['total']
        assert crapMetric.complexityMetric.functions == ['total']
    }

}
