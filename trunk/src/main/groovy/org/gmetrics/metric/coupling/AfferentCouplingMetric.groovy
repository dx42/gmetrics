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

/**
 * Metric for counting the number of other packages that depend on the classes within this package.
 *
 * @author Chris Mair
 */
class AfferentCouplingMetric extends AbstractPackageCouplingMetric {

    final String name = 'AfferentCoupling'
    private final PackageReferenceManager packageReferenceManager = new PackageReferenceManager(this)

    @Override
    protected MetricResult calculateForPackage(String rawPackageName, Collection<MetricResult> childMetricResults) {
        String packageName = normalizePackageName(rawPackageName)
        packageReferenceManager.registerPackageContainingClasses(packageName)
        childMetricResults.each { childMetricResult ->
            if (childMetricResult.metricLevel == MetricLevel.CLASS) {
                packageReferenceManager.addReferencesFromPackage(packageName, childMetricResult[REFERENCED_PACKAGES])
            }
        }
        return packageReferenceManager.getPackageMetricResult(packageName)
    }

    private String normalizePackageName(String name) {
        return name ? name.replace('/', '.') : name
    }
}