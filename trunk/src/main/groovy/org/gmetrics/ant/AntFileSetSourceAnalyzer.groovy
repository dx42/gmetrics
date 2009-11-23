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
package org.gmetrics.ant

import org.gmetrics.analyzer.SourceAnalyzer
import org.gmetrics.metricset.MetricSet
import org.apache.tools.ant.Project
import org.gmetrics.resultsnode.ResultsNode

class AntFileSetSourceAnalyzer implements SourceAnalyzer {

    /**
     * Construct a new instance on the specified List of Ant FileSets.
     * @param project - the Ant Project
     * @param fileSets - the List of Ant FileSet; my be empty; must not be null
     */
    AntFileSetSourceAnalyzer(Project project, List fileSets) {
        // TODO
    }

    ResultsNode analyze(MetricSet metricSet) {
        // TODO
        return null;
    }

}