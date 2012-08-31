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

import org.gmetrics.analyzer.SourceAnalyzer
import org.gmetrics.metricset.MetricSet
import org.apache.tools.ant.Project
import org.gmetrics.resultsnode.ResultsNode
import org.gmetrics.resultsnode.PackageResultsNode
import org.apache.log4j.Logger
import org.gmetrics.source.SourceFile
import org.gmetrics.resultsnode.ClassResultsNode
import org.codehaus.groovy.ast.ClassNode
import org.gmetrics.source.SourceCode
import org.gmetrics.util.PathUtil
import org.gmetrics.metric.PostProcessingMetric

/**
 * SourceAnalyzer implementation that gets source files from one or more Ant FileSets.
 * This class is not reentrant.
 *
 * @author Chris Mair
 */
class AntFileSetSourceAnalyzer implements SourceAnalyzer {

    private static final LOG = Logger.getLogger(AntFileSetSourceAnalyzer)

    private Project project
    protected List fileSets = []
    protected ResultsNode rootResultsNode = new PackageResultsNode(null)

    /**
     * Construct a new instance on the specified List of Ant FileSets.
     * @param project - the Ant Project
     * @param fileSets - the List of Ant FileSet; my be empty; must not be null
     */
    AntFileSetSourceAnalyzer(Project project, List fileSets) {
        assert project
        assert fileSets != null
        this.project = project
        this.fileSets = fileSets
    }

    /**
     * Analyze all source code using the specified MetricSet and return the results node.
     * @param metricSet - the MetricSet to apply to each source component; must not be null.
     * @return the root ResultsNode resulting from applying the MetricSet to all of the source
     */
    ResultsNode analyze(MetricSet metricSet) {
        fileSets.each { fileSet ->
            processFileSet(fileSet, metricSet)
        }
        calculatePackageLevelMetricResults(rootResultsNode, metricSet)
        afterAllSourceCodeProcessed(metricSet)
        return rootResultsNode
    }

    List getSourceDirectories() {
        def baseDir = project.baseDir.absolutePath
        return fileSets.collect { fileSet ->
            def path = fileSet.getDir(project).path
            removeBaseDirectoryPrefix(baseDir, path)
        }
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private void calculatePackageLevelMetricResults(PackageResultsNode resultsNode, MetricSet metricSet) {
        resultsNode.children.each { name, child -> calculatePackageLevelMetricResults(child, metricSet) }
        metricSet.metrics.each { metric -> resultsNode.applyMetric(metric) }
    }

    @SuppressWarnings('EmptyMethod')
    @SuppressWarnings('UnusedPrivateMethodParameter')
    private void calculatePackageLevelMetricResults(ResultsNode resultsNode, MetricSet metricSet) {
        // do nothing
    }

    private void processFileSet(fileSet, metricSet) {
        def dirScanner = fileSet.getDirectoryScanner(project)
        def baseDir = fileSet.getDir(project)
        def includedFiles = dirScanner.includedFiles

        if (!includedFiles) {
            LOG.info("No matching files found for FileSet with basedir [$baseDir]")
        }

        includedFiles.each {filePath ->
            processFile(baseDir, filePath, metricSet)
        }
    }

    private void processFile(File baseDir, String filePath, MetricSet metricSet) {

        def parentPath = PathUtil.getParent(filePath)

        def file = new File(baseDir, filePath)
        def sourceCode = new SourceFile(file)
        def ast = sourceCode.ast
        if (ast) {
            def parentResultsNode
            ast.classes.each { classNode ->
                if (!parentResultsNode) {
                    def packageName = classNode.packageName
                    parentResultsNode = findOrAddResultsNodeForPath(parentPath, packageName)
                }
                def classResultsNode = applyMetricsToClass(classNode, metricSet, sourceCode)
                def className = classNode.name
                parentResultsNode.addChildIfNotEmpty(className, classResultsNode)
            }
        }
    }

    // TODO Harvest?
    private ClassResultsNode applyMetricsToClass(ClassNode classNode, MetricSet metricSet, SourceCode sourceCode) {
        def classResultsNode = new ClassResultsNode(classNode.name, sourceCode.getName(), sourceCode.getPath())
        metricSet.metrics.each { metric ->
            def classMetricResult = metric.applyToClass(classNode, sourceCode)
            classResultsNode.addClassMetricResult(classMetricResult)
        }
        return classResultsNode
    }

    protected ResultsNode findResultsNodeForPath(String path) {
        return findPackageResultsNodeForPath(rootResultsNode, path)
    }

    private ResultsNode findPackageResultsNodeForPath(PackageResultsNode resultsNode, String path) {
        if (resultsNode.path == path) {
            return resultsNode
        }
        def children = resultsNode.children.values()
        return resultFromFirstMatchOrElseNull(children) { child -> findPackageResultsNodeForPath(child, path) }
    }

    @SuppressWarnings('UnusedPrivateMethodParameter')
    private ResultsNode findPackageResultsNodeForPath(ResultsNode resultsNode, String path) {
        return null
    }

    private resultFromFirstMatchOrElseNull(collection, closure) {
        def result
        def found = collection.find { child ->
            result = closure(child)
        }
        return found ? result : null
    }

    protected ResultsNode findOrAddResultsNodeForPath(String path, String packageName) {
        def resultsNode = findResultsNodeForPath(path)
        if (resultsNode) {
            return resultsNode
        }
        def parentPath = PathUtil.getParent(path)
        def name = PathUtil.getName(path)
        def newPackageNode = new PackageResultsNode(name, packageName, path)
        def parentNode = parentPath ? findOrAddResultsNodeForPath(parentPath, packageName) : rootResultsNode
        parentNode.addChild(name, newPackageNode)
        return newPackageNode
    }

    private String removeBaseDirectoryPrefix(String baseDir, String path) {
        if (path.startsWith(baseDir)) {
            path = path - baseDir
            return removeLeadingSlash(path)
        }
        return path
    }

    private String removeLeadingSlash(path) {
        return (path.startsWith('\\') || path.startsWith('/')) ? path.substring(1) : path
    }

    private void afterAllSourceCodeProcessed(MetricSet metricSet) {
        metricSet.metrics.each { metric ->
            if (metric instanceof PostProcessingMetric) {
                metric.afterAllSourceCodeProcessed()
            }
        }
    }
}