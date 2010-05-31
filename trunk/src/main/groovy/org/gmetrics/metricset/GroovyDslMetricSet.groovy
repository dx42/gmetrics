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
package org.gmetrics.metricset

import org.gmetrics.util.io.ResourceFactory
import org.gmetrics.util.io.DefaultResourceFactory
import org.apache.log4j.Logger

/**
 * A <code>MetricSet</code> implementation that parses a Groovy DSL of Metric definitions.
 * The filename passed into the constructor is interpreted relative to the classpath.
 * Note that this class attempts to read the file and parse the Groovy from within the constructor.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class GroovyDslMetricSet implements MetricSet {
    private static final LOG = Logger.getLogger(GroovyDslMetricSet)
    private ResourceFactory resourceFactory = new DefaultResourceFactory()
    private metrics

    /**
     * Construct a new instance on the specified Groovy DSL MetricSet file path
     * @param path - the path to the Groovy DSL MetricSet definition file, relative to the classpath; must not be empty or null
     */
    GroovyDslMetricSet(String path) {
        assert path
        LOG.info("Loading metrics from [$path]")
        def inputStream = resourceFactory.getResource(path).inputStream

        def metricSetBuilder = new MetricSetBuilder()

        def callMetricSet = { closure ->
            closure.resolveStrategy = Closure.DELEGATE_ONLY    // fail if access non-existent properties
            metricSetBuilder.metricset(closure) 
        }
        Binding binding = new Binding(metricset:callMetricSet)

        GroovyShell shell = new GroovyShell(binding);
        shell.evaluate(inputStream);

        metrics = metricSetBuilder.metricSet.metrics
    }

    /**
     * @return a List of Metric objects
     */
    List getMetrics() {
        return metrics
    }
}
