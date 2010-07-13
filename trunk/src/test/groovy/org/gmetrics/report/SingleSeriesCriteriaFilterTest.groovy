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
package org.gmetrics.report

import org.gmetrics.metric.StubMetric
import org.gmetrics.test.AbstractTestCase
import org.gmetrics.result.StubMetricResult
import org.gmetrics.metricset.ListMetricSet

/**
 * Tests for SingleSeriesCriteriaFilter
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */

class SingleSeriesCriteriaFilterTest extends AbstractTestCase {

    private filter = new SingleSeriesCriteriaFilter()
    private resultsNode
    private metric1, metric2
    private metricSet

    void testBuildSeriesData_Method() {
        filter.metric = 'Metric1'
        filter.level = 'method'
        filter.function = 'average'
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA1.MethodA1a':1311, 'ClassA1.MethodA1c':1313])
    }

    void testBuildSeriesData_Class() {
        filter.metric = 'Metric1'
        filter.level = 'class'
        filter.function = 'average'
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, [ClassA2:1212, ClassA3:1213])
    }

    void testBuildSeriesData_Package() {
        filter.metric = 'Metric1'
        filter.level = 'package'
        filter.function = 'average'
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, [DirA:1112, DirB:1113, DirC:1114])
    }

    void testBuildSeriesData_NoSuchMetric_ThrowsException() {
        filter.level = 'package'
        filter.function = 'average'
        filter.metric = 'NoSuchMetric'
        shouldFailWithMessageContaining('NoSuchMetric') { filter.buildSeriesData(resultsNode, metricSet) }
    }

    void testBuildSeriesData_NoSuchLevel() {
        filter.metric = 'Metric1'
        filter.level = 'NoSuchLevel'
        filter.function = 'average'
        shouldFailWithMessageContaining('NoSuchLevel') { filter.buildSeriesData(resultsNode, metricSet) }
    }

    void testBuildSeriesData_NoSuchFunction() {
        filter.metric = 'Metric1'
        filter.level = 'package'
        filter.function = 'NoSuchFunction'
        shouldFailWithMessageContaining('NoSuchFunction') { filter.buildSeriesData(resultsNode, metricSet) }
    }

    void testBuildSeriesData_NullOrEmptyLevel_ThrowsException() {
        filter.metric = 'Metric1'
        filter.function = 'average'

        [null, ''].each { value ->
            filter.level = value
            shouldFailWithMessageContaining('level') { filter.buildSeriesData(resultsNode, metricSet) }
        }
    }

    void testBuildSeriesData_NullOrEmptyMetric_ThrowsException() {
        filter.level = 'package'
        filter.function = 'average'

        [null, ''].each { value ->
            filter.metric = value
            shouldFailWithMessageContaining('metric') { filter.buildSeriesData(resultsNode, metricSet) }
        }
    }

    void testBuildSeriesData_NullOrEmptyFunction_ThrowsException() {
        filter.metric = 'Metric1'
        filter.level = 'package'

        [null, ''].each { value ->
            filter.function = value
            shouldFailWithMessageContaining('function') { filter.buildSeriesData(resultsNode, metricSet) }
        }
    }

    void setUp() {
        super.setUp()
        metric1 = new StubMetric(name:'Metric1')
        metric2 = new StubMetric(name:'Metric2')
        metricSet = new ListMetricSet([metric1, metric2])

        // Metric1=1000-1999 Metric2=2000-2999; package=x1xx class=x2xx method=x3xx; total=xx0x average=xx1x
        resultsNode = packageResultsNode([metricResults:[metric1Result(average:1111, total:1101)]],
        [
            DirA: packageResultsNode([path:'DirA', metricResults:[metric1Result(average:1112, total:1102)]],
            [
                DirB: packageResultsNode([path:'DirB', metricResults:[metric1Result(average:1113)]]),
                ClassA1: classResultsNode([metricResults:[metric1Result(total:1201), metric2Result(average:2211)]],
                [
                    MethodA1a: methodResultsNode(metricResults:[metric1Result(total:1301, average:1311)]),
                    MethodA1b: methodResultsNode(metricResults:[metric1Result(total:1302)]),
                    MethodA1c: methodResultsNode(metricResults:[metric1Result(average:1313), metric2Result(average:2313)])
                ]),
                ClassA2: classResultsNode(metricResults:[metric1Result(average:1212)]),
                ClassA3: classResultsNode(metricResults:[metric1Result(total:1203, average:1213)])
            ]),
            DirC: packageResultsNode(path:'DirC', metricResults:[metric1Result(average:1114), metric2Result(average:2114)]),
            DirD: packageResultsNode(path:'DirD', metricResults:[metric1Result(total:1105), metric2Result(total:2105)])
        ])
    }

    private void assertSeriesData(seriesData, Map expectedData) {
        log("seriesData=$seriesData")
        assert seriesData.size() == expectedData.size()
        int index = 0
        expectedData.each { name, value ->
            assert seriesData[index].name == name, seriesData[index]
            assert seriesData[index].value == value, seriesData[index]
            index++
        }
    }

    private metric1Result(Map map) {
        new StubMetricResult([metric:metric1] + map)
    }

    private metric2Result(Map map) {
        new StubMetricResult([metric:metric2] + map)
    }
}