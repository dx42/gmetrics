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
package org.gmetrics.analyzer

import org.gmetrics.metric.linecount.MethodLineCountMetric
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.result.StubMetricResult
import org.gmetrics.resultsnode.ResultsNodeTestUtil
import org.gmetrics.test.AbstractTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Abstract superclass for SourceAnalyzer integration tests. These tests access the real filesystem
 * and use a real Metric implementation.
 *
 * @author Chris Mair
 */
abstract class AbstractSourceAnalyzer_IntegrationTest extends AbstractTestCase {

    protected static final String BASE_DIR = 'src/test/resources/source'
    protected static final String SCRIPTS_DIR = 'src/test/resources/samplescripts'
    protected static final String NESTED_DIR = 'src/test/resources/nested'
    protected static final String GROOVY_FILES = '**/*.groovy'
    
    protected analyzer
    protected metric
    protected metricSet

    protected abstract SourceAnalyzer createSourceAnalyzer()
    protected abstract void initializeSourceAnalyzerForEmptyDirectory()
    protected abstract void initializeSourceAnalyzerForDirectoryWithNoMatchingFiles()
    

    @BeforeEach
    void setUp_AbstractSourceAnalyzer_IntegrationTest() {
        analyzer = createSourceAnalyzer()
        metric = new MethodLineCountMetric()
        metricSet = new ListMetricSet([metric])
    }

    @Test
	void testAnalyze_EmptyDirectory() {
        initializeSourceAnalyzerForEmptyDirectory()
        def results = new StubMetricResult(metric:metric, count:0, total:0, average:0)
        assertAnalyze_ResultsNodeStructure([metricResults:[results]])
    }

    @Test
	void testAnalyze_NoMatchingFiles() {
        initializeSourceAnalyzerForDirectoryWithNoMatchingFiles()
        def results = new StubMetricResult(metric:metric, count:0, total:0, average:0)
        assertAnalyze_ResultsNodeStructure([metricResults:[results]])
    }

    @Test
	void testAnalyze_NestedSubdirectories() {
        def classA1_method1 = new StubMetricResult(metric:metric, count:1, total:3, average:3)
        def classA1_method2 = new StubMetricResult(metric:metric, count:1, total:5, average:5)
        def classA1 = new StubMetricResult(metric:metric, count:2, total:8, average:4)
        def classA2_method1 = new StubMetricResult(metric:metric, count:1, total:4, average:4)
        def classA2_method2 = new StubMetricResult(metric:metric, count:1, total:4, average:4)
        def classA2 = new StubMetricResult(metric:metric, count:2, total:8, average:4)
        def dirA = new StubMetricResult(metric:metric, count:4, total:16, average:4)

        def classB1_method1 = new StubMetricResult(metric:metric, count:1, total:1, average:1)
        def classB1_method2 = new StubMetricResult(metric:metric, count:1, total:1, average:1)
        def classB1_closure3 = new StubMetricResult(metric:metric, count:1, total:7, average:7)
        def classB1 = new StubMetricResult(metric:metric, count:3, total:9, average:3.0)
        def dirB = new StubMetricResult(metric:metric, count:3, total:9, average:3.0)

        def all = new StubMetricResult(metric:metric, count:7, total:25, average:scale(25/7))

        assertAnalyze_ResultsNodeStructure([
            metricResults:[all],
            children:[
                dirA:[
                    metricResults:[dirA],
                    children:[
                        'org.gmetrics.ClassA1':[
                            metricResults:[classA1],
                            children:[
                                'method1':[metricResults:[classA1_method1]],
                                'method2':[metricResults:[classA1_method2]]
                            ]
                        ],
                        'ClassA2':[
                            metricResults:[classA2],
                            children:[
                                'method1':[metricResults:[classA2_method1]],
                                'method2':[metricResults:[classA2_method2]]
                            ]
                        ]
                    ]
                ],

                dirB:[
                    metricResults:[dirB],
                    children:[
                        'org.gmetrics.example.ClassB1':[
                            metricResults:[classB1],
                            children:[
                                'method1':[metricResults:[classB1_method1]],
                                'method2':[metricResults:[classB1_method2]],
                                'closure3':[metricResults:[classB1_closure3]]
                            ]
                        ]
                    ]
                ]

        ]])
    }

    // --------------------------- Helper methods ---------------------------------------

    protected void assertAnalyze_ResultsNodeStructure(Map resultsNodeStructure) {
        def resultsNode = analyzer.analyze(metricSet)
        ResultsNodeTestUtil.print(resultsNode)
        ResultsNodeTestUtil.assertResultsNodeStructure(resultsNode, resultsNodeStructure)
    }
}