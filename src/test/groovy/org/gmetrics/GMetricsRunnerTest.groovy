/*
 * Copyright 2009 the original author or authors.
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
package org.gmetrics

import org.gmetrics.analyzer.SourceAnalyzer
import org.gmetrics.metricset.MetricSet
import org.gmetrics.report.ReportWriter
import org.gmetrics.resultsnode.StubResultsNode
import org.gmetrics.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for GMetricsRunner
 *
 * @author Chris Mair
 */
class GMetricsRunnerTest extends AbstractTestCase {

    private static final RESULTS_NODE = new StubResultsNode()
    private static final METRIC_SET = [:] as MetricSet
    private gMetricsRunner = new GMetricsRunner()

    @Test
	void testExecute_ThrowsExceptionForNullMetricSet() {
        shouldFailWithMessageContaining('metricSet') { gMetricsRunner.execute() }
    }

    @Test
	void testExecute_ThrowExceptionForNullSourceAnalyzer() {
        gMetricsRunner.metricSet = METRIC_SET
        shouldFailWithMessageContaining('sourceAnalyzer') { gMetricsRunner.execute() }
    }

    @Test
	void testExecute_ThrowsExceptionForNullReportWriters() {
        gMetricsRunner.metricSet = METRIC_SET
        gMetricsRunner.sourceAnalyzer = [:] as SourceAnalyzer
        gMetricsRunner.reportWriters = null
        shouldFailWithMessageContaining('reportWriters') { gMetricsRunner.execute() }
    }

    @Test
	void testExecute() {
        def analyzedMetricSet
        def sourceAnalyzer = [analyze: { ms -> analyzedMetricSet = ms; return RESULTS_NODE }, getSourceDirectories:{[]}] as SourceAnalyzer
        gMetricsRunner.sourceAnalyzer = sourceAnalyzer

        def reportWriterResultsNode = []
        def reportWriterMetricSet = []
        2.times {
            def reportWriter = [writeReport: { resultsNode, analysisContext ->
                reportWriterResultsNode << resultsNode;
                reportWriterMetricSet << analysisContext.metricSet }] as ReportWriter
            gMetricsRunner.reportWriters << reportWriter
        }
        gMetricsRunner.metricSet = METRIC_SET

        assert gMetricsRunner.execute() == RESULTS_NODE

        assert analyzedMetricSet == METRIC_SET
        (0..1).each { index ->
            assert reportWriterResultsNode[index] == RESULTS_NODE
            assert reportWriterMetricSet[index] == METRIC_SET
        }
    }
}