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

import groovy.util.slurpersupport.GPathResult
import org.gmetrics.util.io.ResourceFactory
import org.gmetrics.util.io.DefaultResourceFactory
import org.apache.log4j.Logger
import org.gmetrics.util.PathUtil

/**
 * Parses and provides access to a Cobertura "coverage.xml"
 *
 * @author Chris Mair
 */
class CoberturaCoverageFile {

    private static final LOG = Logger.getLogger(CoberturaCoverageFile)
    private static final PATH_SEPARATOR = '/'
    private static final int SCALE = 2
    private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP

    private String coberturaFile
    private xml
    private String attributeName
    private String packageNamePrefixes
    private ResourceFactory resourceFactory = new DefaultResourceFactory()

    CoberturaCoverageFile(String coberturaFile, String attributeName, String packageNamePrefixes) {
        this.coberturaFile = coberturaFile
        this.attributeName = attributeName
        this.packageNamePrefixes = packageNamePrefixes
    }

    protected BigDecimal getOverallCoverageRate() {
        def coverage = getCoberturaXml()
        return parseCoverageRate(coverage)
    }

    protected BigDecimal parseCoverageRate(GPathResult node) {
        def lineRateStr = node.@"$attributeName".text()
        def lineRate = lineRateStr as BigDecimal
        return lineRate.setScale(SCALE, ROUNDING_MODE)
    }

    protected GPathResult findMatchingPackageElement(String packageName, String packagePath) {
        def matchingPackageElement = findPackageElement(packageName)
        if (matchingPackageElement.isEmpty() && packageNamePrefixes) {
            matchingPackageElement = findPackageElementMatchingPrefix(packagePath)
        }
        return matchingPackageElement
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

    protected GPathResult findClassElement(String className) {
        def coverage = getCoberturaXml()
        return coverage.packages.package.classes.class.find { it.@name == className }
    }

    protected GPathResult findInnerClasses(String className) {
        def coverage = getCoberturaXml()
        return coverage.packages.package.classes.class.findAll { it.@name.text().startsWith(className + '$_') }
    }

    protected boolean hasInnerClasses(String className) {
        return !findInnerClasses(className).isEmpty()
    }

    protected GPathResult findMethodElement(String methodName, int numParameters, String methodSignature, GPathResult classXmlElement) {
        def matchingMethodElements = findAllMethodElements(methodName, numParameters, classXmlElement)

        if (matchingMethodElements.size() == 1) {
            return matchingMethodElements[0]
        }

        return matchingMethodElements.find {
            CoberturaSignatureParser.matchesCoberturaMethod(methodName, methodSignature, it.@name.text(), it.@signature.text())
        }
    }

    private findAllMethodElements(String methodName, int numParameters, GPathResult classXmlElement) {
        def matchingMethodElements = classXmlElement.methods.method.findAll {
            def xmlNumParameters = CoberturaSignatureParser.numberOfParameters(it.@signature.text())
            methodName == it.@name.text() && numParameters == xmlNumParameters
        }
        return matchingMethodElements
    }

    protected GPathResult getCoberturaXml() {
            if (xml == null) {
                assert coberturaFile
                LOG.info("Loading Cobertura XML file [$coberturaFile]")
                def inputStream = resourceFactory.getResource(coberturaFile).inputStream
                def xmlSlurper = createNonValidatingXmlSlurper()
                xml = xmlSlurper.parse(inputStream)
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