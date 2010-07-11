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
package org.gmetrics.report

import org.gmetrics.metric.StubMetric
import org.gmetrics.test.AbstractTestCase

/**
 * Tests for MetricsCriteriaFilter
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */

class MetricsCriteriaFilterTest extends AbstractTestCase {

    private static final METRIC_ABC = new StubMetric(name:'ABC')
    private static final METRIC_XXX = new StubMetric(name:'XXX')
    private static final METRIC_123 = new StubMetric(name:'123')

    private metricsCriteriaFilter = new MetricsCriteriaFilter()

    void testNoMetricsDefined_IncludesMetric_ReturnsTrue() {
        assert metricsCriteriaFilter.includesMetric(METRIC_ABC)
        assert metricsCriteriaFilter.includesMetric(METRIC_123)
    }

    void testOneMetricDefined_IncludesMetric_ReturnsTrueForThat_AndFalseForOthers() {
        metricsCriteriaFilter.setMetrics('ABC')
        assert metricsCriteriaFilter.includesMetric(METRIC_ABC)
        assert !metricsCriteriaFilter.includesMetric(METRIC_123)
    }

    void testMultipleMetricsDefined_IncludesMetric_ReturnsTrueForMatching_AndFalseForOthers() {
        metricsCriteriaFilter.setMetrics('ABC,777, XXX')
        assert metricsCriteriaFilter.includesMetric(METRIC_ABC)
        assert metricsCriteriaFilter.includesMetric(METRIC_XXX)
        assert !metricsCriteriaFilter.includesMetric(METRIC_123)
    }

}