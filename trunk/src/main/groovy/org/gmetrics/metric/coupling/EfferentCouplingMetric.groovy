/*
 * Copyright 2012 the original author or authors.
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
package org.gmetrics.metric.coupling

import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.MetricResult
import org.gmetrics.metric.PostProcessingMetric

/**
 * Metric for counting the number of other packages that the classes in the package depend upon.
 *
 * @author Chris Mair
 */
class EfferentCouplingMetric extends AbstractPackageCouplingMetric implements PostProcessingMetric {

    final String name = 'EfferentCoupling'

    private final EfferentCouplingReferenceManager referenceManager = new EfferentCouplingReferenceManager(this)

    @Override
    protected MetricResult calculateForPackage(String packageName, Collection<MetricResult> childMetricResults) {
        childMetricResults.each { childMetricResult ->
            if (childMetricResult.metricLevel == MetricLevel.CLASS) {
                referenceManager.addReferencesFromPackage(packageName, childMetricResult[REFERENCED_PACKAGES])
            }
        }
        return referenceManager.getPackageMetricResult(packageName)
    }

    // For testing
    protected MetricResult getMetricResult(String packageName) {
        return referenceManager.getPackageMetricResult(packageName)
    }

    @Override
    void afterAllSourceCodeProcessed() {
        referenceManager.updateStatisticsForAllPackages()
    }

}