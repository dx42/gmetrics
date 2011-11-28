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
package org.gmetrics.ant

import org.apache.tools.ant.types.FileSet
import org.apache.tools.ant.Project
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.metric.StubMetric
import org.gmetrics.result.NumberMetricResult
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.resultsnode.ResultsNodeTestUtil
import org.gmetrics.resultsnode.PackageResultsNode
import org.gmetrics.resultsnode.StubResultsNode
import org.gmetrics.result.StubMetricResult
import org.gmetrics.analyzer.AbstractSourceAnalyzer_IntegrationTest
import org.gmetrics.analyzer.SourceAnalyzer
import org.gmetrics.metric.linecount.ClassLineCountMetric
import org.gmetrics.metric.linecount.MethodLineCountMetric
import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.MethodKey

/**
 * Tests for AntFileSetSourceAnalyzer
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AntFileSetSourceAnalyzerTest extends AbstractSourceAnalyzer_IntegrationTest {

    private project = new Project(basedir:'.')
    private fileSet = new FileSet(project:project, dir:new File(BASE_DIR), includes:GROOVY_FILES)
    private metric1, metric2
    private metricResult1, metricResult2

    protected SourceAnalyzer createSourceAnalyzer() {
        return new AntFileSetSourceAnalyzer(project, [fileSet])
    }

    protected void initializeSourceAnalyzerForEmptyDirectory() {
        fileSet.dir = new File(BASE_DIR + '/empty')
    }

    protected void initializeSourceAnalyzerForDirectoryWithNoMatchingFiles() {
        fileSet.dir = new File(BASE_DIR + '/no_matching_files')
    }

    void setUp() {
        super.setUp()
        metric1 = new StubMetric()
        metricResult1 = new NumberMetricResult(metric1, MetricLevel.METHOD, 11)
        metric1.packageMetricResult = metricResult1
        metric1.classMetricResult = new ClassMetricResult(metricResult1, [:])
        metric2 = new StubMetric()
        metricResult2 = new NumberMetricResult(metric2, MetricLevel.METHOD, 22)
        metric2.packageMetricResult = metricResult2
        metric2.classMetricResult = new ClassMetricResult(metricResult2, [:])

        analyzer = new AntFileSetSourceAnalyzer(project, [fileSet])
    }

    void testConstructor_ThrowsExceptionIfFileSetsIsNull() {
        shouldFailWithMessageContaining('fileSets') { new AntFileSetSourceAnalyzer(project, null) }
    }

    void testConstructor_ThrowsExceptionIfProjectIsNull() {
        shouldFailWithMessageContaining('project') { new AntFileSetSourceAnalyzer(null, [fileSet]) }
    }

    void testAnalyze_ReturnsResultsNodeWithNoChildrenForEmptyFileSets() {
        def analyzer = new AntFileSetSourceAnalyzer(project, [])
        def resultsNode = analyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")
        assert resultsNode.children.isEmpty()
    }

    void testAnalyze_ReturnsEmptyResultsNodeForEmptyMetricSet() {
        def resultsNode = analyzer.analyze(new ListMetricSet([]))
        log("resultsNode=$resultsNode")
        assert resultsNode.metricResults.isEmpty()
    }

    void testAnalyze_ScriptClass_ReturnsMethodResults() {
        metricSet = new ListMetricSet([new MethodLineCountMetric()])
        fileSet.dir = new File(SCRIPTS_DIR)

        def resultsNode = analyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")
        assert resultsNode.metricResults[0]['total'] == 3
        def methodKey = new MethodKey('java.lang.Object doConfig()')
        assert resultsNode.children.config.children[methodKey]
    }

    void testAnalyze_ScriptClass_ReturnsNoResultsForClassMetricThatIgnoresSyntheticClasses() {
        metricSet = new ListMetricSet([new ClassLineCountMetric()])
        fileSet.dir = new File(SCRIPTS_DIR)

        def resultsNode = analyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")
        assert resultsNode.children.isEmpty()
    }

    void testAnalyze_MatchingFiles_ButNoSubdirectories() {
        metricSet = new ListMetricSet([metric1])
        fileSet.dir = new File(BASE_DIR + '/dirA')

        assertAnalyze_ResultsNodeStructure([
            metricResults:[metricResult1],
            children:[
                'ClassA1':[metricResults:[metricResult1]],
                'ClassA2':[metricResults:[metricResult1]]
            ]])
    }

    void testAnalyze_MultipleFileSets() {
        def fileSet1 = new FileSet(project:project, dir:new File(BASE_DIR + '/dirA'))
        def fileSet2 = new FileSet(project:project, dir:new File(BASE_DIR + '/dirB'))
        def analyzer = new AntFileSetSourceAnalyzer(project, [fileSet1, fileSet2])
        def resultsNode = analyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")

        final TOP_LEVEL_RESULTS = new StubMetricResult(metric:metric, count:7, total:25, average:scale(25/7))
        ResultsNodeTestUtil.assertMetricResultList(resultsNode.metricResults, [TOP_LEVEL_RESULTS], "top-level")
        assertEqualSets(resultsNode.children.keySet(), ['ClassA1', 'ClassA2', 'ClassB1'])
    }

    void testGetSourceDirectories_ReturnsEmptyListForNoFileSets() {
        def analyzer = new AntFileSetSourceAnalyzer(project, [])
        assert analyzer.sourceDirectories == []
    }

    void testGetSourceDirectories_ReturnsSingleDirectoryForSingleFileSet() {
        def analyzer = new AntFileSetSourceAnalyzer(project, [fileSet])
        assert analyzer.sourceDirectories == [normalizedPath(BASE_DIR)]
    }

    void testGetSourceDirectories_ReturnsDirectoryForEachFileSet() {
        def fileSet1 = new FileSet(dir:new File('abc'), project:project)
        def fileSet2 = new FileSet(dir:new File('def'), project:project)
        def analyzer = new AntFileSetSourceAnalyzer(project, [fileSet1, fileSet2])
        log("sourceDirectories=${analyzer.sourceDirectories}")
        assert analyzer.sourceDirectories == [normalizedPath('abc'), normalizedPath('def')]
    }

    void testGetSourceDirectories_ReturnsDirectoryRelativeToBaseDirectory() {
        def currentDir = new File('').absolutePath
        project = new Project(basedir:currentDir)
        fileSet.setProject(project)
        fileSet.dir = new File(currentDir + '/src/main/groovy')
        def analyzer = new AntFileSetSourceAnalyzer(project, [fileSet])
        log("analyzer.sourceDirectories=${analyzer.sourceDirectories}")
        assert analyzer.sourceDirectories == [normalizedPath('src/main/groovy')]
    }

    void testFindResultsNodeForPath_ReturnsNullForPathThatDoesNotExist() {
        assert analyzer.findResultsNodeForPath('DoesNotExist') == null 
    }

    void testFindResultsNodeForPath_ReturnsRootResultsNodeForNullPath() {
        assert analyzer.findResultsNodeForPath(null) == analyzer.rootResultsNode
    }

    void testFindResultsNodeForPath_IgnoresNonPackageChildNodes() {
        def class1 = new StubResultsNode(metricResults:[metricResult1])
        analyzer.rootResultsNode.addChildIfNotEmpty('a', class1)
        assert analyzer.findResultsNodeForPath('DoesNotExist') == null
    }

    void testFindResultsNodeForPath() {
        def p1 = new PackageResultsNode('a', 'p1')
        def p2 = new PackageResultsNode('a', 'p2')
        def p3 = new PackageResultsNode('a', 'p3')
        def p4 = new PackageResultsNode('a', 'p4')
        analyzer.rootResultsNode.addChild('a', p1)
        analyzer.rootResultsNode.addChild('b', p2)
        p1.addChild('c', p3)
        p3.addChild('d', p4)

        assert analyzer.findResultsNodeForPath('p1') == p1
        assert analyzer.findResultsNodeForPath('p2') == p2
        assert analyzer.findResultsNodeForPath('p3') == p3
        assert analyzer.findResultsNodeForPath('p4') == p4
    }

    void testFindOrAddResultsNodeForPath() {
        def p1 = new PackageResultsNode('a', 'p1')
        def p2 = new PackageResultsNode('a', 'p1/p2')   // TODO: BRITTLE. Implicit dependency between path and (child) name
        analyzer.rootResultsNode.addChild('p1', p1)
        p1.addChild('p2', p2)

        assert analyzer.findOrAddResultsNodeForPath('p1') == p1
        assert analyzer.findOrAddResultsNodeForPath('p1/p2') == p2

        def p3 = analyzer.findOrAddResultsNodeForPath('p3')
        assert p3.path == 'p3'
        assert analyzer.rootResultsNode.children.p3 == p3

        def p4 = analyzer.findOrAddResultsNodeForPath('p1/p2/p4')
        ResultsNodeTestUtil.print(analyzer.rootResultsNode)
        assert p4.path == 'p1/p2/p4'
        assert p2.children.p4 == p4
    }

    private String normalizedPath(String path) {
        return new File(path).path
    }
}