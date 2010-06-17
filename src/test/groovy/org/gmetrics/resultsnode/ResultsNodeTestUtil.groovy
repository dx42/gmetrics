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
import org.gmetrics.metric.MetricLevel

/**
 * Utility methods related to ResultsNode objects, for testing.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ResultsNodeTestUtil {
    static void assertResultsNodeStructure(ResultsNode resultsNode, Map results, String name=null) {
        assertMetricResultList(resultsNode.metricResults, results.metricResults, name)
        assertEqualsOrBothFalse(resultsNode.children?.keySet(), results.children?.keySet())
        resultsNode.children.each { childName, child ->
            assertResultsNodeStructure(child, results.children[childName], name)
        }
    }

    static void print(ResultsNode resultsNode, String name='', int indent=0) {
        def path = resultsNode.level == MetricLevel.PACKAGE ? "path=[${resultsNode.path}]" : ''
        println '  '*indent + "(${resultsNode.level}) ${name}: $path ${resultsNode.metricResults}"
        resultsNode.children.each { childName, childNode -> print(childNode, childName, indent+1) }
    }

    /**
     * Assert that the actual value is equal to the expected or else that both evaluate to (Groovy) false.
     */
    private static void assertEqualsOrBothFalse(actual, expected) {
        def condition = actual ? actual == expected : !expected
        assert condition, "actual=$actual  expected=$expected"
    }

    private static void assertMetricResultList(List actual, List expected, String name) {
        assert actual.size() == expected.size(), "[$name] Unexpected number of MetricResult objects; actual=${actual.size()}, expected=${expected.size()}"
        actual.eachWithIndex { act, index ->
            assertMetricResult(actual[index], expected[index], name)
        }
    }

    private static void assertMetricResult(MetricResult actual, MetricResult expected, String name) {
        assert actual.metric == expected.metric, "[$name] actual=$actual.metric, expected=$expected.metric"
        assert actual.count == expected.count, "[$name] actual=$actual.count, expected=$expected.count"
        assert actual['total'] == expected['total'], "[$name] actual=$actual['total'], expected=$expected['total']"
        assert actual['average'] == expected['average'], "[$name] actual=$actual['average'], expected=$expected['average']"
    }

    private ResultsNodeTestUtil() { }

}