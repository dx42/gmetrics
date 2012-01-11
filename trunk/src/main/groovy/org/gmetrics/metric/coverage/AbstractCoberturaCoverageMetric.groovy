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
 package org.gmetrics.metric.coverage

import org.gmetrics.metric.AbstractMetric
import org.gmetrics.metric.MetricLevel
import org.gmetrics.util.io.ResourceFactory
import org.gmetrics.util.io.DefaultResourceFactory
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
    protected static final PATH_SEPARATOR = '/'

    @SuppressWarnings('LoggerWithWrongModifiers')
    protected static final LOG = Logger.getLogger(AbstractCoberturaCoverageMetric)

    final MetricLevel baseLevel = MetricLevel.METHOD
    String coberturaFile
    String packageNamePrefixes

    private ResourceFactory resourceFactory = new DefaultResourceFactory()
    private Object xmlLock = new Object()
    private classMetricResultBuilder = new MetricResultBuilder(metric:this, metricLevel:MetricLevel.CLASS, scale:2)

    private xml

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
        def matchingClassElement = findClassElement(className)
        if (matchingClassElement.isEmpty()) {
            LOG.warn("No coverage information found for class [$className]")
            return null
        }

        def lineRate = hasInnerClasses(className) ?
            calculateCoverageForClassAndInnerClasses(className) :
            parseCoverageRate(matchingClassElement)

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
        def matchingPackageElement = findPackageElement(packageName)
        if (matchingPackageElement.isEmpty() && packageNamePrefixes) {
            matchingPackageElement = findPackageElementMatchingPrefix(packagePath)
        }
        if (matchingPackageElement == null || matchingPackageElement.isEmpty()) {
            LOG.warn("No coverage information found for package [$packageName]")
            return null
        }
        def lineRate = parseCoverageRate(matchingPackageElement)
        return new SingleNumberMetricResult(this, MetricLevel.PACKAGE, lineRate)
    }

    @Override
    MetricResult calculate(MethodNode methodNode, SourceCode sourceCode) {
        def className = methodNode.declaringClass.name
        def classXmlElement = findClassElement(className)
        return calculateMethodResult(methodNode, classXmlElement)
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    protected Ratio getCoverageRatioForClass(String className) {
        def matchingClassElement = findClassElement(className)
        def overallClassRatio = getCoverageRatioForSingleClass(matchingClassElement)

        def innerClasses = findInnerClasses(className)
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
        def coverage = getCoberturaXml()
        def lineRate = parseCoverageRate(coverage)
        return new SingleNumberMetricResult(this, MetricLevel.PACKAGE, lineRate)
    }

    protected BigDecimal parseCoverageRate(GPathResult node) {
        def lineRateStr = node.@"${getAttributeName()}".text()
        def lineRate = lineRateStr as BigDecimal
        return lineRate.setScale(SCALE, ROUNDING_MODE)
    }

    private Map buildMethodResults(ClassNode classNode, GPathResult classXmlElement) {
        Map<MethodKey, MetricResult> childMetricResults = [:]

        def methodsPlusConstructors = classNode.getMethods() + classNode.getDeclaredConstructors()
        def validMethods = methodsPlusConstructors.findAll { methodNode -> !methodNode.isAbstract() && !methodNode.isSynthetic() }
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
            def lineRate = parseCoverageRate(matchingMethodElement)
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

    protected GPathResult findInnerClasses(String className) {
        def coverage = getCoberturaXml()
        return coverage.packages.package.classes.class.findAll { it.@name.text().startsWith(className + '$_') }
    }

    protected boolean hasInnerClasses(String className) {
        return !findInnerClasses(className).isEmpty()
    }

    protected GPathResult findClassElement(String className) {
        def coverage = getCoberturaXml()
        return coverage.packages.package.classes.class.find { it.@name == className }
    }

    protected GPathResult findMethodElement(MethodNode methodNode, GPathResult classXmlElement) {
        def numParameters = methodNode.parameters.size()
        def methodName = methodNode.name
        def matchingMethodElements = classXmlElement.methods.method.findAll {
            def xmlNumParameters = CoberturaSignatureParser.numberOfParameters(it.@signature.text())
            methodName == it.@name.text() && numParameters == xmlNumParameters
        }

        if (matchingMethodElements.size() == 1) {
            return matchingMethodElements[0]
        }

        return matchingMethodElements.find {
            CoberturaSignatureParser.matchesCoberturaMethod(methodNode, it.@name.text(), it.@signature.text())
        }
    }

    protected GPathResult findPackageElementMatchingPrefix(String packagePath) {
        def prefixes = packageNamePrefixes.tokenize(',')
        def matchingPackageElement
        prefixes.find { String prefix ->
            def trimmedPrefix = prefix.trim()
            matchingPackageElement = findPackageWithPrefix(packagePath, trimmedPrefix, matchingPackageElement)
        }
        return matchingPackageElement
    }

    protected GPathResult findPackageWithPrefix(String packagePath, String prefix, matchingPackageElement) {
        if (packagePath.startsWith(prefix)) {
            def fullPrefix = prefix.endsWith(PATH_SEPARATOR) ? prefix : prefix + PATH_SEPARATOR
            def pathWithoutPrefix = PathUtil.toPackageName(packagePath - fullPrefix)
            matchingPackageElement = findPackageElement(pathWithoutPrefix)
        }
        return matchingPackageElement
    }

    protected GPathResult findPackageElement(String packageName) {
        def coverage = getCoberturaXml()
        return coverage.packages.package.find { it.@name == packageName }
    }

    protected GPathResult getCoberturaXml() {
        synchronized(xmlLock) {
            if (xml == null) {
                assert coberturaFile
                LOG.info("Loading Cobertura XML file [$coberturaFile]")
                def inputStream = resourceFactory.getResource(coberturaFile).inputStream
                def xmlSlurper = createNonValidatingXmlSlurper()
                xml = xmlSlurper.parse(inputStream)
            }
        }
        return xml
    }

    private XmlSlurper createNonValidatingXmlSlurper() {
        def xmlSlurper = new XmlSlurper()

        // Do not try to validate using the DTD, which may refer to an unavailable URI
        xmlSlurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

        return xmlSlurper
    }

}