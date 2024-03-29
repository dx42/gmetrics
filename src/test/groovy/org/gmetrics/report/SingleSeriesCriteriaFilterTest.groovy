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
package org.gmetrics.report

import org.gmetrics.metric.Metric
import org.gmetrics.metric.StubMetric
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.metricset.MetricSet
import org.gmetrics.result.StubMetricResult
import org.gmetrics.resultsnode.ResultsNode
import org.gmetrics.test.AbstractTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for SingleSeriesCriteriaFilter
 *
 * @author Chris Mair
 */

class SingleSeriesCriteriaFilterTest extends AbstractTestCase {

    private SingleSeriesCriteriaFilter filter = new SingleSeriesCriteriaFilter()
    private ResultsNode resultsNode
    private Metric metric1
    private Metric metric2
    private MetricSet metricSet

    @Test
	void testBuildSeriesData_Method() {
        configureFilter(metric:'Metric1', level:'method', function:'average')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA1#MethodA1a':1311, 'ClassA1#MethodA1c':1314, 'ClassA3#MethodA3a':1313])
    }

    @Test
	void testBuildSeriesData_Class() {
        configureFilter(metric:'Metric1', level:'class', function:'average')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, [ClassA2:1212, ClassA3:1213])
    }

    @Test
	void testBuildSeriesData_Package() {
        configureFilter(metric:'Metric1', level:'package', function:'average')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['org.gmetrics.DirA':1112, 'org.gmetrics.DirB':1113, 'org.gmetrics.DirC':1114])
    }

    @Test
	void testBuildSeriesData_Sort_Descending() {
        configureFilter(metric:'Metric1', level:'method', function:'average', sort:'descending')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA1#MethodA1c':1314, 'ClassA3#MethodA3a':1313, 'ClassA1#MethodA1a':1311])
    }

    @Test
	void testBuildSeriesData_Sort_Ascending_Integer() {
        configureFilter(metric:'Metric1', level:'method', function:'average', sort:'ascending')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA1#MethodA1a':1311, 'ClassA3#MethodA3a':1313, 'ClassA1#MethodA1c':1314])
    }

    @Test
	void testBuildSeriesData_Sort_Descending_BigDecimal() {
        configureFilter(metric:'Metric1', level:'class', function:'total', sort:'descending')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA3':1203.77, 'ClassA1':1201.99])
    }

    @Test
	void testBuildSeriesData_Sort_NullOrEmpty() {
        [null, ''].each { sort ->
            configureFilter(metric:'Metric1', level:'package', function:'average', sort:sort)
            def seriesData = filter.buildSeriesData(resultsNode, metricSet)
            assertSeriesData(seriesData, ['org.gmetrics.DirA':1112, 'org.gmetrics.DirB':1113, 'org.gmetrics.DirC':1114])
        }
    }

    @Test
	void testBuildSeriesData_InvalidSortValue_ThrowsException() {
        configureFilter(metric:'Metric1', level:'package', function:'average', sort:'xxx')
        shouldFailWithMessageContaining(['sort','xxx']) { filter.buildSeriesData(resultsNode, metricSet) }
    }

    @Test
	void testBuildSeriesData_MaxResults_Sort() {
        configureFilter(metric:'Metric1', level:'method', function:'average', sort:'descending', maxResults:'2')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA1#MethodA1c':1314, 'ClassA3#MethodA3a':1313])
    }

    @Test
	void testBuildSeriesData_MaxResults_MaxResultsLargerThanResultsSize() {
        configureFilter(metric:'Metric1', level:'method', function:'average', sort:'descending', maxResults:'99')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA1#MethodA1c':1314, 'ClassA3#MethodA3a':1313, 'ClassA1#MethodA1a':1311])
    }

    @Test
	void testBuildSeriesData_MaxResults_ZeroNullOrEmpty_NoLimit() {
        ['0', null, ''].each { maxResults ->
            configureFilter(metric:'Metric1', level:'method', function:'average', sort:'descending', maxResults:maxResults)
            def seriesData = filter.buildSeriesData(resultsNode, metricSet)
            assertSeriesData(seriesData, ['ClassA1#MethodA1c':1314, 'ClassA3#MethodA3a':1313, 'ClassA1#MethodA1a':1311])
        }
    }

    @Test
	void testBuildSeriesData_InvalidMaxResults_ThrowsException() {
        configureFilter(metric:'Metric1', level:'package', function:'average')
        ['-1', 'xx'].each { maxResults ->
            filter.maxResults = maxResults
            shouldFailWithMessageContaining(['maxResults',maxResults]) { filter.buildSeriesData(resultsNode, metricSet) }            
        }
    }

    @Test
	void testBuildSeriesData_GreaterThan_Integer() {
        configureFilter(metric:'Metric1', level:'method', function:'average', greaterThan:'1311')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA1#MethodA1c':1314, 'ClassA3#MethodA3a':1313])
    }

    @Test
	void testBuildSeriesData_GreaterThan_BigDecimal() {
        configureFilter(metric:'Metric1', level:'class', function:'total', greaterThan:'1202.7543')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA3':1203.77])
    }

    @Test
	void testBuildSeriesData_GreaterThan_LargerThanAllValues() {
        configureFilter(metric:'Metric1', level:'method', function:'average', sort:'descending', greaterThan:'1315')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, [:])
    }

    @Test
	void testBuildSeriesData_GreaterThan_LessThanThanAllValues() {
        configureFilter(metric:'Metric1', level:'method', function:'average', sort:'descending', greaterThan:'1300')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA1#MethodA1c':1314, 'ClassA3#MethodA3a':1313, 'ClassA1#MethodA1a':1311])
    }

    @Test
	void testBuildSeriesData_GreaterThan_NullOrEmpty() {
        [null, ''].each { greaterThan ->
            configureFilter(metric:'Metric1', level:'method', function:'average', sort:'descending', greaterThan:greaterThan)
            def seriesData = filter.buildSeriesData(resultsNode, metricSet)
            assertSeriesData(seriesData, ['ClassA1#MethodA1c':1314, 'ClassA3#MethodA3a':1313, 'ClassA1#MethodA1a':1311])
        }
    }

    @Test
	void testBuildSeriesData_InvalidGreaterThan_ThrowsException() {
        configureFilter(metric:'Metric1', level:'package', function:'average')
        ['234.67zzz', 'xx'].each { greaterThan ->
            filter.greaterThan = greaterThan
            shouldFailWithMessageContaining(['greaterThan',greaterThan]) { filter.buildSeriesData(resultsNode, metricSet) }            
        }
    }

    @Test
	void testBuildSeriesData_LessThan_Integer() {
        configureFilter(metric:'Metric1', level:'method', function:'average', lessThan:'1314')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA1#MethodA1a':1311, 'ClassA3#MethodA3a':1313])
    }

    @Test
	void testBuildSeriesData_LessThan_BigDecimal() {
        configureFilter(metric:'Metric1', level:'class', function:'total', lessThan:'1202.7543')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA1':1201.99])
    }

    @Test
	void testBuildSeriesData_LessThan_SmallerThanAllValues() {
        configureFilter(metric:'Metric1', level:'method', function:'average', sort:'descending', lessThan:'1200')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, [:])
    }

    @Test
	void testBuildSeriesData_LessThan_GreaterThanThanAllValues() {
        configureFilter(metric:'Metric1', level:'method', function:'average', sort:'descending', lessThan:'1400')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA1#MethodA1c':1314, 'ClassA3#MethodA3a':1313, 'ClassA1#MethodA1a':1311])
    }

    @Test
	void testBuildSeriesData_LessThan_NullOrEmpty() {
        [null, ''].each { lessThan ->
            configureFilter(metric:'Metric1', level:'method', function:'average', sort:'descending', lessThan:lessThan)
            def seriesData = filter.buildSeriesData(resultsNode, metricSet)
            assertSeriesData(seriesData, ['ClassA1#MethodA1c':1314, 'ClassA3#MethodA3a':1313, 'ClassA1#MethodA1a':1311])
        }
    }

    @Test
	void testBuildSeriesData_InvalidLessThan_ThrowsException() {
        configureFilter(metric:'Metric1', level:'package', function:'average')
        ['234.67zzz', 'xx'].each { lessThan ->
            filter.lessThan = lessThan
            shouldFailWithMessageContaining(['lessThan',lessThan]) { filter.buildSeriesData(resultsNode, metricSet) }
        }
    }

    @Test
	void testBuildSeriesData_LessThan_AndGreaterThan() {
        configureFilter(metric:'Metric1', level:'method', function:'average', lessThan:'1314', greaterThan:'1312')
        def seriesData = filter.buildSeriesData(resultsNode, metricSet)
        assertSeriesData(seriesData, ['ClassA3#MethodA3a':1313])
    }

    @Test
	void testBuildSeriesData_NoSuchMetric_ThrowsException() {
        configureFilter(metric:'NoSuchMetric', level:'package', function:'average')
        shouldFailWithMessageContaining('NoSuchMetric') { filter.buildSeriesData(resultsNode, metricSet) }
    }

    @Test
	void testBuildSeriesData_NoSuchLevel() {
        configureFilter(metric:'Metric1', level:'NoSuchLevel', function:'average')
        shouldFailWithMessageContaining('NoSuchLevel') { filter.buildSeriesData(resultsNode, metricSet) }
    }

    @Test
	void testBuildSeriesData_NoSuchFunction() {
        configureFilter(metric:'Metric1', level:'package', function:'NoSuchFunction')
        shouldFailWithMessageContaining('NoSuchFunction') { filter.buildSeriesData(resultsNode, metricSet) }
    }

    @Test
	void testBuildSeriesData_NullOrEmptyLevel_ThrowsException() {
        configureFilter(metric:'Metric1', function:'average')

        [null, ''].each { value ->
            filter.level = value
            shouldFailWithMessageContaining('level') { filter.buildSeriesData(resultsNode, metricSet) }
        }
    }

    @Test
	void testBuildSeriesData_NullOrEmptyMetric_ThrowsException() {
        configureFilter(level:'package', function:'average')

        [null, ''].each { value ->
            filter.metric = value
            shouldFailWithMessageContaining('metric') { filter.buildSeriesData(resultsNode, metricSet) }
        }
    }

    @Test
	void testBuildSeriesData_NullOrEmptyFunction_ThrowsException() {
        configureFilter(metric:'Metric1', level:'package')

        [null, ''].each { value ->
            filter.function = value
            shouldFailWithMessageContaining('function') { filter.buildSeriesData(resultsNode, metricSet) }
        }
    }

    @BeforeEach
    void setUp() {
        metric1 = new StubMetric(name:'Metric1')
        metric2 = new StubMetric(name:'Metric2')
        metricSet = new ListMetricSet([metric1, metric2])

        // Metric1=1000-1999 Metric2=2000-2999; package=x1xx class=x2xx method=x3xx; total=xx0x average=xx1x
        resultsNode = packageResultsNode([metricResults:[metric1Result(average:1111, total:1101)]],
        [
            DirA: packageResultsNode([path:'org.gmetrics.DirA', metricResults:[metric1Result(average:1112, total:1102)]],
            [
                DirB: packageResultsNode([path:'org.gmetrics.DirB', metricResults:[metric1Result(average:1113)]]),
                ClassA1: classResultsNode([metricResults:[metric1Result(total:1201.99), metric2Result(average:2211)]],
                [
                    MethodA1a: methodResultsNode(name:'MethodA1a', metricResults:[metric1Result(total:1301, average:1311)]),
                    MethodA1b: methodResultsNode(name:'MethodA1b', metricResults:[metric1Result(total:1302)]),
                    MethodA1c: methodResultsNode(name:'MethodA1c', metricResults:[metric1Result(average:1314), metric2Result(average:2314)])
                ]),
                ClassA2: classResultsNode(metricResults:[metric1Result(average:1212)]),
                ClassA3: classResultsNode(metricResults:[metric1Result(total:1203.77, average:1213)],
                [
                    MethodA3a: methodResultsNode(name:'MethodA3a', metricResults:[metric1Result(average:1313)]),
                ])
            ]),
            DirC: packageResultsNode(path:'org.gmetrics.DirC', metricResults:[metric1Result(average:1114), metric2Result(average:2114)]),
            DirD: packageResultsNode(path:'org.gmetrics.DirD', metricResults:[metric1Result(total:1105), metric2Result(total:2105)])
        ])
    }

    private void configureFilter(Map properties) {
        properties.each { name, value -> filter[name] = value }
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