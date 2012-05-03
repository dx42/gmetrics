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

import static org.gmetrics.result.FunctionNames.TOTAL
import static org.gmetrics.result.FunctionNames.VALUE

import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.MapMetricResult
import org.gmetrics.result.MetricResult
import org.gmetrics.util.Calculator

/**
 * Metric for counting the number of other packages that the classes in the package depend upon.
 *
 * @author Chris Mair
 */
class EfferentCouplingMetric extends AbstractPackageCouplingMetric {

    final String name = 'EfferentCoupling'

    @Override
    protected MetricResult calculateForPackage(String packageName, Collection<MetricResult> childMetricResults) {
        Set referencedPackages = []
        int total = 0
        int count = 0
        boolean containsClasses = false
        childMetricResults.each { childMetricResult ->
            if (childMetricResult.metricLevel == MetricLevel.CLASS) {
                referencedPackages.addAll(childMetricResult[REFERENCED_PACKAGES])
                containsClasses = true
            }
            else {
                count += childMetricResult.count
                total += childMetricResult[TOTAL]
            }
        }
        count += containsClasses ? 1 : 0
        total += containsClasses ? referencedPackages.size() : 0

        def average = Calculator.calculateAverage(total, count, 2)
        def resultsMap = [count:count, total:total, average:average]
        if (referencedPackages) {
            resultsMap[REFERENCED_PACKAGES] = referencedPackages
            resultsMap[VALUE] = referencedPackages.size()
        }
        return new MapMetricResult(this, MetricLevel.PACKAGE, resultsMap)
    }

}