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

import org.gmetrics.metric.AbstractMetricTestCase
import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel

/**
 * Abstract superclass for Cobertura Metric test classes
 *
 * @author Chris Mair
 */
abstract class AbstractCoberturaMetricTestCase extends AbstractMetricTestCase {

    protected static final COBERTURA_XML_FILENAME = 'Cobertura-example-small.xml'
    protected static final COBERTURA_XML_RELATIVE_PATH = 'src/test/resources/coverage/' + COBERTURA_XML_FILENAME
    protected static final COBERTURA_XML_RELATIVE_TO_CLASSPATH = 'coverage/' + COBERTURA_XML_FILENAME
    protected static final COBERTURA_XML_FILE_PREFIX = 'file:' + COBERTURA_XML_RELATIVE_PATH
    protected static final COBERTURA_XML_CLASSPATH_PREFIX = 'classpath:' + COBERTURA_XML_RELATIVE_TO_CLASSPATH
    protected static final COBERTURA_XML_BAD_DTD= 'coverage/Cobertura-bad-dtd.xml'

    //------------------------------------------------------------------------------------
    // Abstract Methods
    //------------------------------------------------------------------------------------

    protected abstract BigDecimal getRootPackageValue()
    protected abstract BigDecimal getServicePackageValue()

    //------------------------------------------------------------------------------------
    // Common Tests
    //------------------------------------------------------------------------------------

    void testBaseLevelIsMethod() {
        assert metric.baseLevel == MetricLevel.METHOD
    }

    void testImplementsMetricInterface() {
        assert metric instanceof Metric
    }


    void testCanLoadCoberturaFileWithDTDSpecifyingUnreachableURI() {
        metric.coberturaFile = COBERTURA_XML_BAD_DTD
        metric.applyToPackage('whatever', null) == null
    }

    // Tests for applyToClass()

    void testApplyToClass_ReturnNullForInterface() {
        final SOURCE = """
            package com.example.model
            interface Channel { }
        """
        assert applyToClass(SOURCE) == null
    }

    void testApplyToClass_CoberturaFileNotSet_ThrowsException() {
        final SOURCE = 'class Channel { }'
        metric.coberturaFile = null
        shouldFailWithMessageContaining('coberturaFile') {
            applyToClass(SOURCE)
        }
    }

    // Tests for applyToPackage()

    void testApplyToPackage() {
        assertApplyToPackage('com.example.service', getServicePackageValue())
    }

    void testApplyToPackage_PackagePath() {
        assertApplyToPackage('com/example/service', getServicePackageValue())
    }

    void testApplyToPackage_NullPath_ReturnsOverallValue() {
        assertApplyToPackage(null, getRootPackageValue())
    }

    void testApplyToPackage_SinglePrefix_MatchesPackageNamePrefixes() {
        metric.packageNamePrefixes = 'src/main/java'
        assertApplyToPackage('src/main/java/com.example.service', getServicePackageValue())
    }

    void testApplyToPackage_MultiplePrefixes_MatchesPackageNamePrefixes() {
        metric.packageNamePrefixes = 'src/main/java,other'
        assertApplyToPackage('other/com/example/service', getServicePackageValue())
    }

    void testApplyToPackage_AllowsWhitespaceAroundPackageNamePrefixes() {
        metric.packageNamePrefixes = 'src/main/java, other '
        assertApplyToPackage('other/com/example/service', getServicePackageValue())
    }

    void testApplyToPackage_PrefixHasTrailingSeparator_MatchesPackageNamePrefixes() {
        metric.packageNamePrefixes = 'other/'
        assertApplyToPackage('other/com.example.service', getServicePackageValue())
    }

    void testApplyToPackage_DoesNotMatchPackageNamePrefixes() {
        metric.packageNamePrefixes = 'src/other'
        assert metric.applyToPackage('src/main/java/com.example.service', null) == null
    }

    void testApplyToPackage_NoCoverageInformation() {
        assert metric.applyToPackage('no.such.package', null) == null
    }

    // Test loading file using resource syntax

    void testLoadCoberturaFile_ClassPathResource() {
        metric.coberturaFile = COBERTURA_XML_CLASSPATH_PREFIX
        assertApplyToPackage('com.example.service', getServicePackageValue())
    }

    void testLoadCoberturaFile_FileResource() {
        metric.coberturaFile = COBERTURA_XML_FILE_PREFIX
        assertApplyToPackage('com.example.service', getServicePackageValue())
    }

    //------------------------------------------------------------------------------------
    // Set up and helper methods
    //------------------------------------------------------------------------------------

    void setUp() {
        super.setUp()
        metric.coberturaFile = COBERTURA_XML_RELATIVE_TO_CLASSPATH
    }

    protected void assertRatio(Ratio ratio, int numerator, int denominator) {
        assert ratio.numerator == numerator
        assert ratio.denominator == denominator
    }
}