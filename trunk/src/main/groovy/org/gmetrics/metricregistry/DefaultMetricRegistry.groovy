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

import org.gmetrics.metric.Metric
import org.gmetrics.metric.abc.AbcMetric
import org.gmetrics.metric.methodcount.MethodCountMetric
import org.gmetrics.metric.linecount.MethodLineCountMetric
import org.gmetrics.metric.linecount.ClassLineCountMetric
import org.gmetrics.metric.fieldcount.FieldCountMetric
import org.gmetrics.metric.classcount.ClassCountMetric
import org.gmetrics.metric.cyclomatic.CyclomaticComplexityMetric

/**
 * Default implementation of MetricRegistry
 *
 * @author Chris Mair
 */
class DefaultMetricRegistry implements MetricRegistry {

    private static final METRIC_CLASSES = [
        AbcMetric,
        CyclomaticComplexityMetric,
        ClassCountMetric,
        FieldCountMetric,
        ClassLineCountMetric,
        MethodLineCountMetric,
        MethodCountMetric,
    ]
    private static final METRIC_MAP = buildMetricClassMap()

    @Override
    Class<Metric> getMetricClass(String metricName) {
        return METRIC_MAP[metricName]
    }

    @Override
    Collection<String> getAllMetricNames() {
        return METRIC_MAP.keySet()
    }

    private static Map<String,Class<Metric>> buildMetricClassMap() {
        def map = [:]
        METRIC_CLASSES.each { metricClass ->
            def instance = metricClass.newInstance()
            map[instance.getName()] = metricClass
        }
        return map
    }

}
