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
 package org.gmetrics.metric.coverage

import org.gmetrics.metric.AbstractMetric
import org.gmetrics.metric.MetricLevel
import groovy.util.slurpersupport.GPathResult
import org.apache.log4j.Logger
import org.codehaus.groovy.ast.MethodNode
import org.gmetrics.result.SingleNumberMetricResult
import org.gmetrics.result.MetricResult
import org.codehaus.groovy.ast.ClassNode
import org.gmetrics.result.MethodKey
import org.gmetrics.util.PathUtil
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.source.SourceCode
import org.gmetrics.result.MetricResultBuilder
import org.gmetrics.util.AstUtil

/**
 * Abstract superclass for metrics that provide test code coverage from a Cobertura XML file.
 *
 * @author Chris Mair
 */
abstract class AbstractCoberturaCoverageMetric extends AbstractMetric {

    protected static final int SCALE = 2
    protected static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP

    @SuppressWarnings('LoggerWithWrongModifiers')
    protected static final LOG = Logger.getLogger(AbstractCoberturaCoverageMetric)

    final MetricLevel baseLevel = MetricLevel.METHOD
    String coberturaFile
    String packageNamePrefixes

    private classMetricResultBuilder = new MetricResultBuilder(metric:this, metricLevel:MetricLevel.CLASS, scale:2)
    private CoberturaCoverageFile coberturaCoverageFile
    private Object coberturaLock = new Object()

    //------------------------------------------------------------------------------------
    // Abstract Methods
    //------------------------------------------------------------------------------------

    /**
     * @return the name of the desired coverage attribute within the Cobertura XML file (e.g. "line-rate")
     */
    protected abstract String getAttributeName()

    /**
     * @return the calculated coverage ratio for the Cobertura XML class element
     */
    protected abstract Ratio getCoverageRatioForSingleClass(matchingClassElement)

    //------------------------------------------------------------------------------------
    // API and Template methods
    //------------------------------------------------------------------------------------

    @Override
    protected ClassMetricResult calculateForClass(ClassNode classNode, SourceCode sourceCode) {
        if (classNode.isInterface()) {
            return null
        }

        def className = classNode.name
        def matchingClassElement = getCoberturaCoverageFile().findClassElement(className)
        if (matchingClassElement.isEmpty()) {
            LOG.warn("No coverage information found for class [$className]")
            return null
        }

        def lineRate = getCoberturaCoverageFile().hasInnerClasses(className) ?
            calculateCoverageForClassAndInnerClasses(className) :
            getCoberturaCoverageFile().parseCoverageRate(matchingClassElement)

        Map methodResults = buildMethodResults(classNode, matchingClassElement)
        def classLevelMetricResult = classMetricResultBuilder.createAggregateMetricResult(methodResults.values(), classNode.lineNumber, [total:lineRate])
        return new ClassMetricResult(classLevelMetricResult, methodResults)
    }

    @Override
    protected MetricResult calculateForPackage(String packagePath, Collection<MetricResult> childMetricResults) {
        if (packagePath == null) {
            return getOverallPackageMetricValue()
        }

        def packageName = PathUtil.toPackageName(packagePath)
        def matchingPackageElement = getCoberturaCoverageFile().findMatchingPackageElement(packageName, packagePath)
        if (matchingPackageElement == null || matchingPackageElement.isEmpty()) {
            if (containsClasses(childMetricResults)) {
                LOG.warn("No coverage information found for package [$packageName]")
            }
            return null
        }
        def lineRate = getCoberturaCoverageFile().parseCoverageRate(matchingPackageElement)
        return new SingleNumberMetricResult(this, MetricLevel.PACKAGE, lineRate)
    }

    @Override
    MetricResult calculate(MethodNode methodNode, SourceCode sourceCode) {
        def className = methodNode.declaringClass.name
        def classXmlElement = getCoberturaCoverageFile().findClassElement(className)
        return calculateMethodResult(methodNode, classXmlElement)
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    protected Ratio getCoverageRatioForClass(String className) {
        def matchingClassElement = getCoberturaCoverageFile().findClassElement(className)
        def overallClassRatio = getCoverageRatioForSingleClass(matchingClassElement)

        def innerClasses = getCoberturaCoverageFile().findInnerClasses(className)
        innerClasses.each { innerClassElement ->
            overallClassRatio += getCoverageRatioForSingleClass(innerClassElement)
        }
        return overallClassRatio
    }

    private BigDecimal calculateCoverageForClassAndInnerClasses(String className) {
        def ratio = getCoverageRatioForClass(className)
        return ratio.toBigDecimal(SCALE, ROUNDING_MODE)
    }

    private MetricResult getOverallPackageMetricValue() {
        def lineRate = getCoberturaCoverageFile().getOverallCoverageRate()
        return new SingleNumberMetricResult(this, MetricLevel.PACKAGE, lineRate)
    }

    private Map buildMethodResults(ClassNode classNode, GPathResult classXmlElement) {
        Map<MethodKey, MetricResult> childMetricResults = [:]

        def methodsPlusConstructors = classNode.getMethods() + classNode.getDeclaredConstructors()
        def validMethods = methodsPlusConstructors.findAll { methodNode ->
            !methodNode.isAbstract() && !AstUtil.isFromGeneratedSourceCode(methodNode)
        }
        validMethods.each { methodNode ->
            def metricResult = calculateMethodResult(methodNode, classXmlElement)
            if (metricResult) {
                def methodKey = new MethodKey(methodNode)
                childMetricResults[methodKey] = metricResult
            }
        }
        return childMetricResults
    }

    protected SingleNumberMetricResult calculateMethodResult(MethodNode methodNode, GPathResult classXmlElement) {
        def matchingMethodElement = findMethodElement(methodNode, classXmlElement)
        if (!matchingMethodElement.isEmpty()) {
            def lineRate = getCoberturaCoverageFile().parseCoverageRate(matchingMethodElement)
            return new SingleNumberMetricResult(this, MetricLevel.METHOD, lineRate, methodNode.lineNumber)
        }
        logMissingMethodCoverageInformation(methodNode)
        return null
    }

    private void logMissingMethodCoverageInformation(MethodNode methodNode) {
        if (!AstUtil.isEmptyMethod(methodNode)) {
            def className = methodNode.declaringClass.name
            LOG.warn("No coverage information found for method [${className}.${methodNode.name}]")
        }
    }

    protected GPathResult findMethodElement(MethodNode methodNode, GPathResult classXmlElement) {
        def numParameters = methodNode.parameters.size()
        def methodName = methodNode.name
        def methodSignature = methodNode.typeDescriptor
        return getCoberturaCoverageFile().findMethodElement(methodName, numParameters, methodSignature, classXmlElement)
    }

    private boolean containsClasses(Collection<MetricResult> childMetricResults) {
        childMetricResults.find { metricResult -> metricResult.metricLevel == MetricLevel.CLASS }
    }

    private CoberturaCoverageFile getCoberturaCoverageFile() {
        synchronized(coberturaLock) {
            if (coberturaCoverageFile == null) {
                assert coberturaFile
                coberturaCoverageFile = new CoberturaCoverageFile(coberturaFile, getAttributeName(), packageNamePrefixes)
            }
        }
        return coberturaCoverageFile
    }

}