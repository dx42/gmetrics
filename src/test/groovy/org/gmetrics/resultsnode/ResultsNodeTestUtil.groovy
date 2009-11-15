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
package org.gmetrics.resultsnode

import org.gmetrics.result.MetricResult
import org.gmetrics.result.MetricResult

/**
 * Utility methods related to ResultsNode objects, for testing.
 *
 * @author Chris Mair
 * @version $Revision: 228 $ - $Date: 2009-09-29 21:52:31 -0400 (Tue, 29 Sep 2009) $
 */
class ResultsNodeTestUtil {
    /**
     * Assert that the actual value is equal to the expected or else that both evaluate to (Groovy) false.
     */
    private static void assertEqualsOrBothFalse(actual, expected) {
        def condition = actual ? actual == expected : !expected
        assert condition, "actual=$actual  expected=$expected"
    }

    static void assertResultsNodeStructure(ResultsNode resultsNode, Map results) {
        assertMetricResultList(resultsNode.metricResults, results.metricResults)
        assertEqualsOrBothFalse(resultsNode.children?.keySet(), results.children?.keySet())
        resultsNode.children.each { name, child ->
            assertResultsNodeStructure(child, results.children[name])
        }
    }

    private static void assertMetricResultList(List actual, List expected) {
        assert actual.size() == expected.size(), "Unexpected number of MetricResult objects; actual=${actual.size()}, expected=${expected.size()}"
        actual.eachWithIndex { act, index ->
            assertMetricResult(actual[index], expected[index])
        }
    }

    private static void assertMetricResult(MetricResult actual, MetricResult expected) {
        assert actual.metric == expected.metric, "actual=$actual.metric, expected=$expected.metric"
        assert actual.count == expected.count, "actual=$actual.count, expected=$expected.count"
        assert actual.totalValue == expected.totalValue, "actual=$actual.totalValue, expected=$expected.totalValue"
        assert actual.averageValue == expected.averageValue, "actual=$actual.averageValue, expected=$expected.averageValue"
    }

    private ResultsNodeTestUtil() { }

}