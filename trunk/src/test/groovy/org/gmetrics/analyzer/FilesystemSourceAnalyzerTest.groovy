/*
 * Copyright 2008 the original author or authors.
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
import org.gmetrics.metric.Metric
import org.gmetrics.metric.StubMetric
import org.gmetrics.result.NumberMetricResult
import org.gmetrics.source.SourceString
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.resultsnode.ResultsNodeTestUtil
import org.gmetrics.test.AbstractTestCase

/**
 * Tests for FilesystemSourceAnalyzer.
 *
 * @author Chris Mair
 * @version $Revision: 180 $ - $Date: 2009-07-11 18:30:19 -0400 (Sat, 11 Jul 2009) $
 */
class FilesystemSourceAnalyzerTest extends AbstractTestCase {

    private static final BASE_DIR = 'src/test/resources/sourcewithdirs'
    private analyzer
    private metric1, metric2
    private metricResult1, metricResult2
    private metricSet
    private dirA, dirB, dirC, dirD
    private fileA, fileB, fileC, nonMatchingFile
    private baseDir

    void setUp() {
        super.setUp()
        analyzer = new FilesystemSourceAnalyzer(baseDirectory:BASE_DIR)

        metric1 = new StubMetric()
        metricResult1 = new NumberMetricResult(metric1, 11)
        metric1.packageMetricResult = metricResult1
        metric1.classMetricResult = new ClassMetricResult(metricResult1, [:])
        metric2 = new StubMetric()
        metricResult2 = new NumberMetricResult(metric2, 22)
        metric2.packageMetricResult = metricResult2
        metric2.classMetricResult = new ClassMetricResult(metricResult2, [:])
        metricSet = createMetricSet(metric1)

        dirA = new StubFile(name:'A', directory:true)
        dirB = new StubFile(name:'B', directory:true)
        dirC = new StubFile(name:'C', directory:true)
        dirD = new StubFile(name:'D', directory:true)
        fileA = new StubFile(name:'ClassA.groovy')
        fileB = new StubFile(name:'ClassB.groovy')
        fileC = new StubFile(name:'ClassC.groovy')
        nonMatchingFile = new StubFile(name:'NonMatchingFile.properties')
        baseDir = new StubFile(name:'BASE', directory:true)

        analyzer.fileFactory = [getFile:{ path, filename -> baseDir}]

        analyzer.sourceCodeFactory = [getSourceCode: { file ->
            def name = file.name[0..5]
            return new SourceString("class $name { }", file.name) 
        }]
    }

    void testAnalyze_NullMetricSet() {
        analyzer.baseDirectory = BASE_DIR
        shouldFailWithMessageContaining('metricSet') { analyzer.analyze(null) }
    }

    void testAnalyze_BaseDirectoryNull() {
        analyzer.baseDirectory = null
        shouldFailWithMessageContaining('baseDirectory') { analyzer.analyze(metricSet) }
    }

    void testAnalyze_BaseDirectoryEmpty() {
        analyzer.baseDirectory = ''
        shouldFailWithMessageContaining('baseDirectory') { analyzer.analyze(metricSet) }
    }

    void testAnalyze_ResultsWithNoChildrenIfBaseDirectoryIsEmpty() {
        metric1.packageMetricResult = null
        assertAnalyze_ResultsNodeStructure([metricResults:[]])
    }

    void testAnalyze_ResultsWithNoChildrenIfBaseDirectoryHasNoMatchingFiles() {
        baseDir.files = [nonMatchingFile]
        metric1.packageMetricResult = null
        assertAnalyze_ResultsNodeStructure([metricResults:[]])
    }

    void testAnalyze_NestedSubdirectories_ButNoMatchingFiles() {
        baseDir.files = [dirA, dirB]
        dirA.files = [dirC, dirD]
        metric1.packageMetricResult = null

        assertAnalyze_ResultsNodeStructure([metricResults:[]])
    }

    void testAnalyze_NestedSubdirectoriesAndFiles() {
        baseDir.files = [dirA, fileA]
        dirA.files = [dirB, dirC]
        dirC.files = [fileB, fileC]

        assertAnalyze_ResultsNodeStructure([
                metricResults:[metricResult1],
                children:[
                    A:[metricResults:[metricResult1],
                        children:[
                            'A/C':[metricResults:[metricResult1],
                                children:[
                                    'ClassB':[metricResults:[metricResult1]],
                                    'ClassC':[metricResults:[metricResult1]]
                                ]
                            ]
                        ]
                    ],
                    ClassA:[metricResults:[metricResult1]]
                ]])
    }

    void testAnalyze_TwoMetrics() {
        baseDir.files = [dirA, dirB]
        dirA.files = [dirC, fileA]
        dirB.files = [fileB]
        dirC.files = [fileC]
        metricSet = createMetricSet(metric1, metric2)
        final MR = [metricResult1, metricResult2]

        assertAnalyze_ResultsNodeStructure([
                metricResults:MR,
                children:[
                    A:[metricResults:MR,
                        children:[
                            'A/C':[metricResults:MR,
                                children:[
                                    ClassC:[metricResults:MR]
                                ]],
                            ClassA:[metricResults:MR]]],
                    B:[metricResults:MR,
                        children:[
                            ClassB:[metricResults:MR]
                        ]
                    ]
                ]])
    }

    void testAnalyze_IncludesAndExcludes() {
        baseDir.files = [dirA]
        dirA.files = [fileA, fileB, fileC]
        analyzer.excludes = '**ClassB*'

        assertAnalyze_ResultsNodeStructure([
                metricResults:[metricResult1],
                children:[
                    A:[metricResults:[metricResult1],
                        children:[
                            'ClassA':[metricResults:[metricResult1]],
                            'ClassC':[metricResults:[metricResult1]]
                        ]
                    ]
                ]])
    }

    void testMatches() {
        def source = new SourceString('def x', 'dir/file.txt')
        assertMatches(source, null, null, true)
        assertMatches(source, '', null, true)
        assertMatches(source, '**/file.txt', null, true)
        assertMatches(source, '**/file.txt', 'other', true)
        assertMatches(source, null, 'other', true)

        assertMatches(source, '**/file.txt', '**/file.txt', false)
        assertMatches(source, null, '**/file.txt', false)
        assertMatches(source, '**/OTHER.*', '', false)
    }

    // --------------------------- Internal helper methods ---------------------------------------

    private void assertAnalyze_ResultsNodeStructure(Map resultsNodeStructure) {
        def resultsNode = analyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")
        ResultsNodeTestUtil.assertResultsNodeStructure(resultsNode, resultsNodeStructure)
    }

    private ListMetricSet createMetricSet(Metric[] metrics) {
        new ListMetricSet(metrics as List)
    }

    private void assertMatches(source, includes, excludes, shouldMatch) {
        analyzer.includes = includes
        analyzer.excludes = excludes
        analyzer.initializeWildcardPatterns()
        assert analyzer.matches(source) == shouldMatch
    }

}

class StubFile {
    boolean directory
    List files = []
    String name = hashCode() as String

    void eachFile(Closure closure) {
        files.each { closure(it) }
    }

    String toString() {
        "StubFile[directory=$directory, files=$files]"
    }
}