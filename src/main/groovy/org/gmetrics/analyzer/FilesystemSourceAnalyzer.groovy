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

import org.gmetrics.util.WildcardPattern
import org.gmetrics.resultsnode.ClassResultsNode
import org.gmetrics.source.SourceFile
import org.gmetrics.source.SourceCode
import org.gmetrics.metricset.MetricSet
import org.gmetrics.resultsnode.PackageResultsNode
import org.codehaus.groovy.ast.ClassNode
import org.gmetrics.resultsnode.ResultsNode
import org.gmetrics.util.PathUtil

/**
 * SourceAnalyzer implementation that recursively processes files from the file system.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class FilesystemSourceAnalyzer implements SourceAnalyzer {
    static final SEP = '/'
    static final DEFAULT_INCLUDES = '**.groovy'

    /**
     * The base (root) directory. Must not be null or empty.
     */
    String baseDirectory

    /**
     * The ant-style pattern of files to include in the analysis. Defaults to match all
     * files with names ending with '.groovy'. If null, match all
     * files/directories. This pattern can optionally contain wildcards: '**', '*' and '?'.
     * All file separators within paths are normalized to the standard '/' separator,
     * so use the '/' separator within this pattern where necessary. Example:
     * "&#42;&#42;/*.groovy". If both <code>includes</code> and <code>excludes</code>
     * are specified, then only files/directories that match at least one of the
     * <code>includes</code> and none of the <code>excludes</code> are analyzed.
     */
    String includes = DEFAULT_INCLUDES

    /**
     * The ant-style pattern of files to exclude from the analysis. If null, exclude no
     * files/directories. This pattern can optionally contain wildcards: '**', '*' and '?'.
     * All file separators within paths are normalized to the standard '/' separator,
     * so use the '/' separator within this pattern where necessary. Example:
     * "&#42;&#42;/*.groovy". If both <code>includes</code> and <code>excludes</code>
     * are specified, then only files/directories that match at least one of the
     * <code>includes</code> and none of the <code>excludes</code> are analyzed.
     */
    String excludes

    protected fileFactory = [getFile:{ path, filename -> new File(baseDirectory)}]

    protected sourceCodeFactory = [getSourceCode:{ file -> new SourceFile(file)}]
                                                                           
    private WildcardPattern includesPattern
    private WildcardPattern excludesPattern

    /**
     * Analyze the source with the configured directory tree(s) using the specified metrics and return the results.
     * @param metricSet - the MetricSet to apply to each of the (applicable) files in the source directories
     * @return the results from applying the metrics to all of the files in the source directories
     */
    ResultsNode analyze(MetricSet metricSet) {
        assert baseDirectory
        assert metricSet        // != null   ????

        initializeWildcardPatterns()
        def dirFile = fileFactory.getFile(baseDirectory, '')
        return processDirectory(dirFile, '', metricSet)
    }

    List getSourceDirectories() {
        return [baseDirectory]
    }

    private PackageResultsNode processDirectory(dirFile, String dir, MetricSet metricSet) {
        String name = PathUtil.getName(dir)
        def dirResults = new PackageResultsNode(name)
        dirFile.eachFile {file ->
            def dirPrefix = dir ? dir + SEP : dir
            def filePath = dirPrefix + file.name
            if (file.directory) {
                def subdirResults = processDirectory(file, filePath, metricSet)
                if (subdirResults.containsClassResults()) {
                    dirResults.addChildIfNotEmpty(filePath, subdirResults)
                }
            }
            else {
                processFile(file, dirResults, metricSet)
            }
        }
        metricSet.metrics.each { metric -> dirResults.applyMetric(metric) }
        return dirResults
    }

    private void processFile(file, PackageResultsNode packageResults, MetricSet metricSet) {
        def sourceCode = sourceCodeFactory.getSourceCode(file)
        if  (matches(sourceCode)) {
            def ast = sourceCode.ast
            if (ast) {
                ast.classes.each { classNode ->
                    def classResultsNode = applyMetricsToClass(classNode, metricSet, sourceCode)
                    def className = classNode.name
                    packageResults.addChildIfNotEmpty(className, classResultsNode)
                }
            }
        }
    }

    private ClassResultsNode applyMetricsToClass(ClassNode classNode, MetricSet metricSet, SourceCode sourceCode) {
        def classResultsNode = new ClassResultsNode(classNode.name)
        metricSet.metrics.each { metric ->
            def classMetricResult = metric.applyToClass(classNode, sourceCode)
            classResultsNode.addClassMetricResult(classMetricResult)
        }
        return classResultsNode
    }

    protected boolean matches(SourceCode sourceCode) {
        return includesPattern.matches(sourceCode.path) &&
            !excludesPattern.matches(sourceCode.path)
    }

    private void initializeWildcardPatterns() {
        includesPattern = new WildcardPattern(includes)
        excludesPattern = new WildcardPattern(excludes, false)  // do not match by default
    }
}