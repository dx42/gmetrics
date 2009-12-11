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

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.result.StubMetricResult
import org.gmetrics.resultsnode.ResultsNodeTestUtil
import org.gmetrics.metric.linecount.MethodLineCountMetric

/**
 * Abstract superclass for SourceAnalyzer integration tests. These tests access the real filesystem
 * and use a real Metric implementation.
 *
 * @author Chris Mair
 * @version $Revision: 180 $ - $Date: 2009-07-11 18:30:19 -0400 (Sat, 11 Jul 2009) $
 */
abstract class AbstractSourceAnalyzer_IntegrationTest extends AbstractTestCase {

    protected static final BASE_DIR = 'src/test/resources/source'
    protected static final GROOVY_FILES = '**/*.groovy'
    
    protected analyzer
    protected metric
    protected metricSet

    protected abstract SourceAnalyzer createSourceAnalyzer()
    protected abstract void initializeSourceAnalyzerForEmptyDirectory()
    protected abstract void initializeSourceAnalyzerForDirectoryWithNoMatchingFiles()
    

    void setUp() {
        super.setUp()
        analyzer = createSourceAnalyzer()
        metric = new MethodLineCountMetric()
        metricSet = new ListMetricSet([metric])
    }

    void testAnalyze_EmptyDirectory() {
        initializeSourceAnalyzerForEmptyDirectory()
        def results = new StubMetricResult(metric:metric, count:0, total:0, average:0)
        assertAnalyze_ResultsNodeStructure([metricResults:[results]])
    }

    void testAnalyze_NoMatchingFiles() {
        initializeSourceAnalyzerForDirectoryWithNoMatchingFiles()
        def results = new StubMetricResult(metric:metric, count:0, total:0, average:0)
        assertAnalyze_ResultsNodeStructure([metricResults:[results]])
    }

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
        def classB1 = new StubMetricResult(metric:metric, count:3, total:9, average:3)
        def dirB = new StubMetricResult(metric:metric, count:3, total:9, average:3)

        def all = new StubMetricResult(metric:metric, count:7, total:25, average:scale(25/7))

        assertAnalyze_ResultsNodeStructure([
            metricResults:[all],
            children:[
                dirA:[
                    metricResults:[dirA],
                    children:[
                        'ClassA1':[
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
                        'ClassB1':[
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