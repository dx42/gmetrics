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
package org.gmetrics.ant

import org.apache.tools.ant.Project
import org.apache.tools.ant.types.FileSet
import org.gmetrics.analyzer.AbstractSourceAnalyzer_IntegrationTest
import org.gmetrics.analyzer.SourceAnalyzer
import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel
import org.gmetrics.metric.PostProcessingMetric
import org.gmetrics.metric.StubMetric
import org.gmetrics.metric.linecount.ClassLineCountMetric
import org.gmetrics.metric.linecount.MethodLineCountMetric
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.result.MethodKey
import org.gmetrics.result.MetricResult
import org.gmetrics.result.SingleNumberMetricResult
import org.gmetrics.result.StubMetricResult
import org.gmetrics.resultsnode.PackageResultsNode
import org.gmetrics.resultsnode.ResultsNodeTestUtil
import org.gmetrics.resultsnode.StubResultsNode
import org.junit.Before
import org.junit.Test

/**
 * Tests for AntFileSetSourceAnalyzer
 *
 * @author Chris Mair
 */
class AntFileSetSourceAnalyzerTest extends AbstractSourceAnalyzer_IntegrationTest {

    private Project project = new Project(basedir:'.')
    private FileSet fileSet = new FileSet(project:project, dir:new File(BASE_DIR), includes:GROOVY_FILES)
    private Metric metric1
    private Metric metric2
    private MetricResult metricResult1
    private MetricResult metricResult2

    @Override
    protected SourceAnalyzer createSourceAnalyzer() {
        return new AntFileSetSourceAnalyzer(project, [fileSet])
    }

    @Override
    protected void initializeSourceAnalyzerForEmptyDirectory() {
        fileSet.dir = new File(BASE_DIR + '/empty')
    }

    @Override
    protected void initializeSourceAnalyzerForDirectoryWithNoMatchingFiles() {
        fileSet.dir = new File(BASE_DIR + '/no_matching_files')
    }

    @Before
    void setUp() {
        metric1 = new StubMetric()
        metricResult1 = new SingleNumberMetricResult(metric1, MetricLevel.METHOD, 11)
        metric1.packageMetricResult = metricResult1
        metric1.classMetricResult = new ClassMetricResult(metricResult1, [:])
        metric2 = new StubMetric()
        metricResult2 = new SingleNumberMetricResult(metric2, MetricLevel.METHOD, 22)
        metric2.packageMetricResult = metricResult2
        metric2.classMetricResult = new ClassMetricResult(metricResult2, [:])

        analyzer = new AntFileSetSourceAnalyzer(project, [fileSet])
    }

    @Test
	void testConstructor_ThrowsExceptionIfFileSetsIsNull() {
        shouldFailWithMessageContaining('fileSets') { new AntFileSetSourceAnalyzer(project, null) }
    }

    @Test
	void testConstructor_ThrowsExceptionIfProjectIsNull() {
        shouldFailWithMessageContaining('project') { new AntFileSetSourceAnalyzer(null, [fileSet]) }
    }

    @Test
	void testAnalyze_ReturnsResultsNodeWithNoChildrenForEmptyFileSets() {
        def analyzer = new AntFileSetSourceAnalyzer(project, [])
        def resultsNode = analyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")
        assert resultsNode.children.isEmpty()
    }

    @Test
	void testAnalyze_ReturnsEmptyResultsNodeForEmptyMetricSet() {
        def resultsNode = analyzer.analyze(new ListMetricSet([]))
        log("resultsNode=$resultsNode")
        assert resultsNode.metricResults.isEmpty()
    }

    @Test
	void testAnalyze_ScriptClass_ReturnsMethodResults() {
        metricSet = new ListMetricSet([new MethodLineCountMetric()])
        fileSet.dir = new File(SCRIPTS_DIR)

        def resultsNode = analyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")
        assert resultsNode.metricResults[0]['total'] == 3
        def methodKey = new MethodKey('java.lang.Object doConfig()')
        assert resultsNode.children.config.children[methodKey]
    }

    @Test
	void testAnalyze_ScriptClass_ReturnsNoResultsForClassMetricThatIgnoresSyntheticClasses() {
        metricSet = new ListMetricSet([new ClassLineCountMetric()])
        fileSet.dir = new File(SCRIPTS_DIR)

        def resultsNode = analyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")
        assert resultsNode.children.isEmpty()
    }

    @Test
	void testAnalyze_MatchingFiles_ButNoSubdirectories() {
        metricSet = new ListMetricSet([metric1])
        fileSet.dir = new File(BASE_DIR + '/dirA')

        assertAnalyze_ResultsNodeStructure([
            metricResults:[metricResult1],
            children:[
                'org.gmetrics.ClassA1':[metricResults:[metricResult1]],
                'ClassA2':[metricResults:[metricResult1]]
            ]])
    }

    @Test
	void testAnalyze_IncludesFileNameAndFilePath_ForClassResultsNode() {
        metricSet = new ListMetricSet([metric1])
        fileSet.dir = new File(BASE_DIR + '/dirA')
        def resultsNode = analyzer.analyze(metricSet)
        ResultsNodeTestUtil.print(resultsNode)
        assert resultsNode.children['org.gmetrics.ClassA1'].fileName == 'ClassA1.groovy'
        assert resultsNode.children['org.gmetrics.ClassA1'].filePath.endsWith('/dirA/ClassA1.groovy')
    }

    @Test
	void testAnalyze_IncludesPackageName_ForPackageResultsNode() {
        def resultsNode = analyzer.analyze(metricSet)
        ResultsNodeTestUtil.print(resultsNode)
        assert resultsNode.packageName == ''
        assert resultsNode.children['dirA'].packageName == 'org.gmetrics'
        assert resultsNode.children['dirB'].packageName == 'org.gmetrics.example'
    }

    @Test
	void testAnalyze_OnlyIncludesPackageName_ForPackagesWithClasses() {
        fileSet.dir = new File(NESTED_DIR)
        def resultsNode = analyzer.analyze(metricSet)
        ResultsNodeTestUtil.print(resultsNode)
        assert resultsNode.children['dir1'].children['dir2'].packageName == 'dir1.dir2'
        assert resultsNode.children['dir1'].packageName == ''
    }

    @Test
	void testAnalyze_NoPackageDeclarationInClass_NoPackageName_ForPackageResultsNode() {
        fileSet.dir = new File(SCRIPTS_DIR)
        def resultsNode = analyzer.analyze(metricSet)
        ResultsNodeTestUtil.print(resultsNode)
        assert resultsNode.packageName == ''
    }


    private class PostProcessingTestMetric extends StubMetric implements PostProcessingMetric {
        boolean afterAllSourceCodeProcessedCalled = false
        @Override
        void afterAllSourceCodeProcessed() {
            afterAllSourceCodeProcessedCalled = true
        }
    }

    @Test
	void testAnalyze_InvokesPostProcessingMetric() {
        def metric = new PostProcessingTestMetric()
        metricSet = new ListMetricSet([new StubMetric(), metric])
        analyzer.analyze(metricSet)
        assert metric.afterAllSourceCodeProcessedCalled
    }

    @Test
	void testAnalyze_MultipleFileSets() {
        def fileSet1 = new FileSet(project:project, dir:new File(BASE_DIR + '/dirA'))
        def fileSet2 = new FileSet(project:project, dir:new File(BASE_DIR + '/dirB'))
        def analyzer = new AntFileSetSourceAnalyzer(project, [fileSet1, fileSet2])
        def resultsNode = analyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")

        final TOP_LEVEL_RESULTS = new StubMetricResult(metric:metric, count:7, total:25, average:scale(25/7))
        ResultsNodeTestUtil.assertMetricResultList(resultsNode.metricResults, [TOP_LEVEL_RESULTS], "top-level")
        assertEqualSets(resultsNode.children.keySet(), ['org.gmetrics.ClassA1', 'ClassA2', 'org.gmetrics.example.ClassB1'])
    }

    @Test
	void testGetSourceDirectories_ReturnsEmptyListForNoFileSets() {
        def analyzer = new AntFileSetSourceAnalyzer(project, [])
        assert analyzer.sourceDirectories == []
    }

    @Test
	void testGetSourceDirectories_ReturnsSingleDirectoryForSingleFileSet() {
        def analyzer = new AntFileSetSourceAnalyzer(project, [fileSet])
        assert analyzer.sourceDirectories == [normalizedPath(BASE_DIR)]
    }

    @Test
	void testGetSourceDirectories_ReturnsDirectoryForEachFileSet() {
        def fileSet1 = new FileSet(dir:new File('abc'), project:project)
        def fileSet2 = new FileSet(dir:new File('def'), project:project)
        def analyzer = new AntFileSetSourceAnalyzer(project, [fileSet1, fileSet2])
        log("sourceDirectories=${analyzer.sourceDirectories}")
        assert analyzer.sourceDirectories == [normalizedPath('abc'), normalizedPath('def')]
    }

    @Test
	void testGetSourceDirectories_ReturnsDirectoryRelativeToBaseDirectory() {
        def currentDir = new File('').absolutePath
        project = new Project(basedir:currentDir)
        fileSet.setProject(project)
        fileSet.dir = new File(currentDir + '/src/main/groovy')
        def analyzer = new AntFileSetSourceAnalyzer(project, [fileSet])
        log("analyzer.sourceDirectories=${analyzer.sourceDirectories}")
        assert analyzer.sourceDirectories == [normalizedPath('src/main/groovy')]
    }

    @Test
	void testFindResultsNodeForPath_ReturnsNullForPathThatDoesNotExist() {
        assert analyzer.findResultsNodeForPath('DoesNotExist') == null 
    }

    @Test
	void testFindResultsNodeForPath_ReturnsRootResultsNodeForNullPath() {
        assert analyzer.findResultsNodeForPath(null) == analyzer.rootResultsNode
    }

    @Test
	void testFindResultsNodeForPath_IgnoresNonPackageChildNodes() {
        def class1 = new StubResultsNode(metricResults:[metricResult1])
        analyzer.rootResultsNode.addChildIfNotEmpty('a', class1)
        assert analyzer.findResultsNodeForPath('DoesNotExist') == null
    }

    @Test
	void testFindResultsNodeForPath() {
        final PKG = 'org.gmetrics'
        def p1 = new PackageResultsNode('a', PKG, 'p1')
        def p2 = new PackageResultsNode('a', PKG, 'p2')
        def p3 = new PackageResultsNode('a', PKG, 'p3')
        def p4 = new PackageResultsNode('a', PKG, 'p4')
        analyzer.rootResultsNode.addChild('a', p1)
        analyzer.rootResultsNode.addChild('b', p2)
        p1.addChild('c', p3)
        p3.addChild('d', p4)

        assert analyzer.findResultsNodeForPath('p1') == p1
        assert analyzer.findResultsNodeForPath('p2') == p2
        assert analyzer.findResultsNodeForPath('p3') == p3
        assert analyzer.findResultsNodeForPath('p4') == p4
    }

    @Test
	void testFindOrAddResultsNodeForPath() {
        final PKG = 'org.gmetrics'
        def p1 = new PackageResultsNode('a', PKG, 'p1')
        def p2 = new PackageResultsNode('a', PKG, 'p1/p2')   // TODO: BRITTLE. Implicit dependency between path and (child) name
        analyzer.rootResultsNode.addChild('p1', p1)
        p1.addChild('p2', p2)

        assert analyzer.findOrAddResultsNodeForPath('p1', PKG) == p1
        assert analyzer.findOrAddResultsNodeForPath('p1/p2', PKG) == p2

        def p3 = analyzer.findOrAddResultsNodeForPath('p3', PKG)
        assert p3.path == 'p3'
        assert analyzer.rootResultsNode.children.p3 == p3

        def p4 = analyzer.findOrAddResultsNodeForPath('p1/p2/p4', PKG)
        ResultsNodeTestUtil.print(analyzer.rootResultsNode)
        assert p4.path == 'p1/p2/p4'
        assert p2.children.p4 == p4
    }

    private String normalizedPath(String path) {
        return new File(path).path
    }
}