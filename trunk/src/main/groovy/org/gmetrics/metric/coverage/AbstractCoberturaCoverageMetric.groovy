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
import org.gmetrics.result.NumberMetricResult
import org.gmetrics.result.MetricResult
import org.codehaus.groovy.ast.ClassNode
import org.gmetrics.result.MethodKey
import org.gmetrics.util.PathUtil
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.source.SourceCode

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

    private ResourceFactory resourceFactory = new DefaultResourceFactory()
    private Object xmlLock = new Object()
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
//    protected abstract Ratio getCoverageRatioForClass(String className)

    //------------------------------------------------------------------------------------
    // API and Template methods
    //------------------------------------------------------------------------------------

    @Override
    protected ClassMetricResult calculateForClass(ClassNode classNode, SourceCode sourceCode) {
        if (classNode.isInterface()) {
            return null
        }

        def className = classNode.name
        def matchingClassElement = findMatchingClassElement(className)
        if (matchingClassElement.isEmpty()) {
            LOG.warn("No coverage information found for class [$className]")
            return null
        }

        def lineRate = hasInnerClasses(className) ?
            calculateCoverageForClassAndInnerClasses(className) :
            parseCoverageRate(matchingClassElement)

        def metricResult = new NumberMetricResult(this, MetricLevel.CLASS, lineRate, classNode.lineNumber)
        Map methodResults = buildMethodResults(classNode, matchingClassElement)
        return new ClassMetricResult(metricResult, methodResults)
    }

    @Override
    protected MetricResult calculateForPackage(String packagePath, Collection<MetricResult> childMetricResults) {
        if (packagePath == null) {
            return getOverallPackageMetricValue()
        }

        def packageName = PathUtil.toPackageName(packagePath)
        def coverage = getCoberturaXml()
        def matchingPackageElement = coverage.packages.package.find { it.@name == packageName }
        if (matchingPackageElement.isEmpty()) {
            LOG.warn("No coverage information found for package [$packageName]")
            return null
        }
        def lineRate = parseCoverageRate(matchingPackageElement)
        return new NumberMetricResult(this, MetricLevel.PACKAGE, lineRate)
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    protected Ratio getCoverageRatioForClass(String className) {
        def matchingClassElement = findMatchingClassElement(className)
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
        return new NumberMetricResult(this, MetricLevel.PACKAGE, lineRate)
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
            def matchingMethodElement = findMatchingMethodElement(methodNode, classXmlElement)
            if (!matchingMethodElement.isEmpty()) {
                def lineRate = parseCoverageRate(matchingMethodElement)
                def methodResult = new NumberMetricResult(this, MetricLevel.METHOD, lineRate, classNode.lineNumber)
                def methodKey = new MethodKey(methodNode)
                childMetricResults[methodKey] = methodResult
            }
            else {
                LOG.warn("No coverage information found for method [${classNode.name}.${methodNode.name}]")
            }
        }
        return childMetricResults
    }

    protected findInnerClasses(String className) {
        def coverage = getCoberturaXml()
        return coverage.packages.package.classes.class.findAll { it.@name.text().startsWith(className + '$_') }
    }

    protected boolean hasInnerClasses(String className) {
        return !findInnerClasses(className).isEmpty()
    }

    protected findMatchingClassElement(String className) {
        def coverage = getCoberturaXml()
        return coverage.packages.package.classes.class.find { it.@name == className }
    }

    protected findMatchingMethodElement(MethodNode methodNode, GPathResult classXmlElement) {
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

    protected GPathResult getCoberturaXml() {
        synchronized(xmlLock) {
            if (xml == null) {
                assert coberturaFile
                LOG.info("Loading Cobertura XML file [$coberturaFile]")
                def inputStream = resourceFactory.getResource(coberturaFile).inputStream
                def xmlSlurper = new XmlSlurper()
                xml = xmlSlurper.parse(inputStream)
            }
        }
        return xml
    }

}