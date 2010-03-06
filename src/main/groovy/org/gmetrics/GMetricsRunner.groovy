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

import org.gmetrics.resultsnode.ResultsNode
import org.gmetrics.analyzer.SourceAnalyzer
import org.apache.log4j.Logger
import org.gmetrics.metricset.MetricSet
import org.gmetrics.analyzer.AnalysisContext

/**
 * Helper class to run GMetrics.
 * <p/>
 * The following properties must be configured before invoking the <code>execute()</code> method:
 * <ul>
 *   <li><code>sourceAnalyzer</code> - An instance of a <code>org.gmetrics.analyzer.SourceAnalyzer</code> implementation.</li>
 *   <li><code>reportWriters</code> - The list of <code>ReportWriter</code> instances. A report is generated
 *          for each element in this list. This list can be empty, but cannot be null.</li>
 * </ul>
 *
 * NOTE: This is an internal class. Its API is subject to change.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class GMetricsRunner {
    private static final LOG = Logger.getLogger(GMetricsRunner)

    MetricSet metricSet
    SourceAnalyzer sourceAnalyzer
    List reportWriters = []

    ResultsNode execute() {
        assert metricSet
        assert sourceAnalyzer
        assert reportWriters != null\

        def startTime = System.currentTimeMillis()
        def resultsNode = sourceAnalyzer.analyze(metricSet)
        def elapsedTime = System.currentTimeMillis() - startTime
        LOG.debug("resultsNode=$resultsNode")

        def analysisContext = new AnalysisContext(metricSet:metricSet, sourceDirectories:sourceAnalyzer.sourceDirectories)

        reportWriters.each { reportWriter ->
            reportWriter.writeReport(resultsNode, analysisContext)
        }

        LOG.info("GMetrics completed: ${elapsedTime}ms")
        return resultsNode
    }
}