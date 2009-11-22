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

/**
 * Helper class to run GMetrics.
 * <p/>
 * The following properties must be configured before invoking the <code>execute()</code> method:
 * <ul>
 *   <li><code>rulesetfiles</code> - The path to the Groovy or XML RuleSet definition files, relative to the classpath. This can be a
 *          single file path, or multiple paths separated by commas.</li>
 *   <li><code>sourceAnalyzer</code> - An instance of a <code>org.GMETRICS.analyzer.SourceAnalyzer</code> implementation.</li>
 *   <li><code>reportWriters</code> - The list of <code>ReportWriter</code> instances. A report is generated
 *          for each element in this list. At least one <code>ReportWriter</code> must be configured.</li>
 * </ul>
 *
 * NOTE: This is an internal class. Its API is subject to change.
 *
 * @author Chris Mair
 * @version $Revision: 219 $ - $Date: 2009-09-07 21:48:47 -0400 (Mon, 07 Sep 2009) $
 */
class GMetricsRunner {

    // TODO Incomplete implementation

    private static final LOG = Logger.getLogger(GMetricsRunner)

    //String ruleSetFiles
    MetricSet metricSet
    SourceAnalyzer sourceAnalyzer
    List reportWriters = []

    ResultsNode execute() {
        // TODO
    }
}