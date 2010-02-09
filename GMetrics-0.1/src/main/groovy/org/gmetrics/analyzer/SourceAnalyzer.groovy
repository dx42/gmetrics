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
package org.gmetrics.analyzer

import org.gmetrics.metricset.MetricSet
import org.gmetrics.resultsnode.ResultsNode

/**
 * The interface for objects that can analyze the source files within one or more directory
 * trees using a specified MetricSet and produce report results.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
interface SourceAnalyzer {

    /**
     * Analyze all source code using the specified set of Metrics and return the results.
     * @param metricSet - the MetricSet to apply to each source component; must not be null.
     * @return the results from applying the metrics to all of the source
     */
    ResultsNode analyze(MetricSet metricSet)

}