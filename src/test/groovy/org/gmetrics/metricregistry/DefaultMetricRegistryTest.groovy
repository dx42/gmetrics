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
 package org.gmetrics.metricregistry

import org.gmetrics.metric.abc.AbcMetric
import org.gmetrics.metric.classcount.ClassCountMetric
import org.gmetrics.metric.coupling.AfferentCouplingMetric
import org.gmetrics.metric.coupling.EfferentCouplingMetric
import org.gmetrics.metric.coverage.CoberturaBranchCoverageMetric
import org.gmetrics.metric.coverage.CoberturaLineCoverageMetric
import org.gmetrics.metric.crap.CrapMetric
import org.gmetrics.metric.cyclomatic.CyclomaticComplexityMetric
import org.gmetrics.metric.fieldcount.FieldCountMetric
import org.gmetrics.metric.linecount.ClassLineCountMetric
import org.gmetrics.metric.linecount.MethodLineCountMetric
import org.gmetrics.metric.methodcount.MethodCountMetric
import org.gmetrics.test.AbstractTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for DefaultMetricRegistry
 *
 * @author Chris Mair
 */
class DefaultMetricRegistryTest extends AbstractTestCase {

    private DefaultMetricRegistry registry = new DefaultMetricRegistry()

    @Test
	void testImplementsMetricRegistry() {
        assert registry instanceof MetricRegistry
    }

    @Test
	void testRetrieveMetricClassesByName() {
        assert registry.getMetricClass('ABC') == AbcMetric
        assert registry.getMetricClass('AfferentCoupling') == AfferentCouplingMetric
        assert registry.getMetricClass('ClassCount') == ClassCountMetric
        assert registry.getMetricClass('ClassLineCount') == ClassLineCountMetric
        assert registry.getMetricClass('CoberturaBranchCoverage') == CoberturaBranchCoverageMetric
        assert registry.getMetricClass('CoberturaLineCoverage') == CoberturaLineCoverageMetric
        assert registry.getMetricClass('CRAP') == CrapMetric
        assert registry.getMetricClass('CyclomaticComplexity') ==  CyclomaticComplexityMetric
        assert registry.getMetricClass('FieldCount') == FieldCountMetric
        assert registry.getMetricClass('EfferentCoupling') == EfferentCouplingMetric
        assert registry.getMetricClass('MethodCount') == MethodCountMetric
        assert registry.getMetricClass('MethodLineCount') == MethodLineCountMetric
    }

    @Test
	void testGetAllMetricNames() {
        def allNames = registry.allMetricNames
        assert allNames.containsAll(['ABC', 'ClassCount', 'MethodLineCount'])
    }

}