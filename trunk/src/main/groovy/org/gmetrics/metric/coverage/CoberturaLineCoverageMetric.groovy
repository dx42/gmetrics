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

import org.gmetrics.metric.MetricLevel
import org.gmetrics.metric.AbstractMetric
import org.gmetrics.result.ClassMetricResult
import org.codehaus.groovy.ast.ClassNode
import org.gmetrics.source.SourceCode
import org.gmetrics.result.NumberMetricResult
import org.gmetrics.result.MethodKey
import groovy.util.slurpersupport.GPathResult
import org.gmetrics.result.MetricResult
import org.apache.log4j.Logger
import org.gmetrics.util.PathUtil
import org.codehaus.groovy.ast.MethodNode

/**
 * Metric for test code coverage by line (line-rate) from a Cobertura XML file.
 *
 * @author Chris Mair
 */
class CoberturaLineCoverageMetric extends AbstractMetric {

    private static final LOG = Logger.getLogger(CoberturaLineCoverageMetric)

    final String name = 'CoberturaLineCoverage'
    final MetricLevel baseLevel = MetricLevel.METHOD
    String coberturaFile
    private Object xmlLock = new Object()
    private xml

    @Override
    protected ClassMetricResult calculateForClass(ClassNode classNode, SourceCode sourceCode) {
        if (classNode.isInterface()) {
            return null
        }

        def className = classNode.name
        def coverage = getCoberturaXml()

        def matchingClassElement = coverage.packages.package.classes.class.find { it.@name == className }
        if (matchingClassElement.isEmpty()) {
            LOG.warn("No coverage information found for class [$className]")
            return null
        }
        def lineRate = parseLineRate(matchingClassElement)
        def metricResult = new NumberMetricResult(this, MetricLevel.CLASS, lineRate, classNode.lineNumber)
        Map methodResults = buildMethodResults(classNode, matchingClassElement)
        return new ClassMetricResult(metricResult, methodResults)
    }

    @Override
    protected MetricResult calculateForPackage(String packagePath, Collection<MetricResult> childMetricResults) {
        def packageName = PathUtil.toPackageName(packagePath)
        def coverage = getCoberturaXml()
        def matchingPackageElement = coverage.packages.package.find { it.@name == packageName }
        if (matchingPackageElement.isEmpty()) {
            LOG.warn("No coverage information found for package [$packageName]")
            return null
        }
        def lineRate = parseLineRate(matchingPackageElement)
        return new NumberMetricResult(this, MetricLevel.PACKAGE, lineRate)
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    private BigDecimal parseLineRate(GPathResult node) {
        def lineRateStr = node.@'line-rate'.text()
        return lineRateStr as BigDecimal
    }

    private Map buildMethodResults(ClassNode classNode, GPathResult classXmlElement) {
        Map<MethodKey, MetricResult> childMetricResults = [:]

        def methodsPlusConstructors = classNode.getMethods() + classNode.getDeclaredConstructors()
        def validMethods = methodsPlusConstructors.findAll { methodNode -> !methodNode.isAbstract() }
        validMethods.each { methodNode ->
            def matchingMethodElement = findMatchingMethodElement(methodNode, classXmlElement)
            if (!matchingMethodElement.isEmpty()) {
                def lineRate = parseLineRate(matchingMethodElement)
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

    private findMatchingMethodElement(MethodNode methodNode, GPathResult classXmlElement) {
        def matchingMethodElement = classXmlElement.methods.method.find {
            CoberturaSignatureParser.matchesCoberturaMethod(methodNode, it.@name.text(), it.@signature.text())
        }
        return matchingMethodElement
    }

    private GPathResult getCoberturaXml() {
        assert coberturaFile
        synchronized(xmlLock) {
            if (xml == null) {
                LOG.info("Loading Cobertura XML file [$coberturaFile]")
                def xmlSlurper = new XmlSlurper()
                xml = xmlSlurper.parse(new File(coberturaFile))
            }
        }
        return xml
    }
}
