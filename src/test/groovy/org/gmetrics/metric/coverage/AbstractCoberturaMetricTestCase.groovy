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
import org.gmetrics.metric.MethodMetric
import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.StubMetricResult
import org.junit.Before
import org.junit.Test

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
    protected static final CLASS_METRIC_RESULT = new StubMetricResult(metricLevel:MetricLevel.CLASS)
    protected static final PACKAGE_METRIC_RESULT = new StubMetricResult(metricLevel:MetricLevel.PACKAGE)

    //------------------------------------------------------------------------------------
    // Abstract Methods
    //------------------------------------------------------------------------------------

    protected abstract BigDecimal getRootPackageValue()
    protected abstract BigDecimal getServicePackageValue()

    //------------------------------------------------------------------------------------
    // Common Tests
    //------------------------------------------------------------------------------------

    @Test	void testBaseLevelIsMethod() {
        assert metric.baseLevel == MetricLevel.METHOD
    }

    @Test	void testImplementsMethodMetricInterface() {
        assert metric instanceof MethodMetric
    }


    @Test	void testCanLoadCoberturaFileWithDTDSpecifyingUnreachableURI() {
        metric.coberturaFile = COBERTURA_XML_BAD_DTD
        metric.applyToPackage('whatever', null, null) == null
    }

    // Tests for applyToClosure()

    @Test	void testApplyToClosure_ThrowsUnsupportedOperationException() {
        final SOURCE = """
            class MyClass {
                def myClosure = { }
            }
        """
        def closure = findFirstClosureExpression(SOURCE)
        shouldFail(UnsupportedOperationException) { metric.applyToClosure(closure, sourceCode) }
    }

    // Tests for calculate()

//    @Test//	  void testCalculate_NonEmptyMethodThatHasNoCoverageInformation_LogsWarning() {
//        final SOURCE = """
//            package com.example.service
//            class Email {
//                int unknown() {
//                    println 'email'
//                }
//            }
//        """
//        def log4jMessages = captureLog4JMessages { metric.calculate(findFirstMethod(SOURCE), sourceCode) }
//        assert log4jMessages.find { logEvent -> logEvent.message.contains('unknown') }
//    }
//
//    @Test//	  void testCalculate_EmptyMethodThatHasNoCoverageInformation_DoesNotLogWarning() {
//        final SOURCE = """
//            package com.example.service
//            class Email {
//                int unknown() { }
//            }
//        """
//        def log4jMessages = captureLog4JMessages { metric.calculate(findFirstMethod(SOURCE), sourceCode) }
//        assert !log4jMessages.find { logEvent -> logEvent.message.contains('unknown') }
//    }

    // Tests for applyToClass()

    @Test	void testApplyToClass_ReturnNullForInterface() {
        final SOURCE = """
            package com.example.model
            interface Channel { }
        """
        assert applyToClass(SOURCE) == null
    }

    @Test	void testApplyToClass_CoberturaFileNotSet_ThrowsException() {
        final SOURCE = 'class Channel { }'
        metric.coberturaFile = null
        shouldFailWithMessageContaining('coberturaFile') {
            applyToClass(SOURCE)
        }
    }

//    @Test//	  void testApplyToClass_Enum_DoesNotLogWarningForMissingImplicitConstructorCoverageInformation() {
//        final SOURCE = """
//            package com.example.service
//            enum Email {
//                ONE, TWO, THREE
//            }
//        """
//        def log4jMessages = captureLog4JMessages { applyToClass(SOURCE) }
//        assert !log4jMessages.find { logEvent -> logEvent.message.contains('<init>') }
//    }

    // Tests for applyToPackage()

    @Test	void testApplyToPackage() {
        assertApplyToPackage('com.example.service', 'com.example.service', getServicePackageValue())
    }

    @Test	void testApplyToPackage_PackagePath() {
        assertApplyToPackage('com/example/service', 'com.example.service', getServicePackageValue())
    }

    @Test	void testApplyToPackage_PackagePath_DifferentFromPackageName() {
        assertApplyToPackage('src/com/example/service', 'com.example.service', getServicePackageValue())
    }

    @Test	void testApplyToPackage_NullPath_ReturnsOverallValue() {
        assertApplyToPackage(null, getRootPackageValue())
    }

//    @Test//	  void testApplyToPackage_NoCoverageInformation_ForPackageWithNoClasses_DoesNotLogWarning() {
//        def children = [PACKAGE_METRIC_RESULT]
//        def log4jMessages = captureLog4JMessages { metric.applyToPackage('com.example', 'com.example', children) }
//        assert !log4jMessages.find { logEvent -> logEvent.message.contains('com.example') }
//    }
//
//    @Test//	  void testApplyToPackage_NoCoverageInformation_ForPackageWithClasses_LogsWarning() {
//        def children = [PACKAGE_METRIC_RESULT, CLASS_METRIC_RESULT]
//        def log4jMessages = captureLog4JMessages { metric.applyToPackage('com.example', 'com.example', children) }
//        assert log4jMessages.find { logEvent -> logEvent.message.contains('com.example') }
//    }

    @Test	void testApplyToPackage_NoCoverageInformation() {
        assert metric.applyToPackage('no.such.package', null, null) == null
    }

    // Test loading file using resource syntax

    @Test	void testLoadCoberturaFile_ClassPathResource() {
        metric.coberturaFile = COBERTURA_XML_CLASSPATH_PREFIX
        assertApplyToPackage('com.example.service', 'com.example.service', getServicePackageValue())
    }

    @Test	void testLoadCoberturaFile_FileResource() {
        metric.coberturaFile = COBERTURA_XML_FILE_PREFIX
        assertApplyToPackage('com.example.service', 'com.example.service', getServicePackageValue())
    }

    //------------------------------------------------------------------------------------
    // Set up and helper methods
    //------------------------------------------------------------------------------------

    @Before
    void setUp() {
        metric.coberturaFile = COBERTURA_XML_RELATIVE_TO_CLASSPATH
    }

    protected void assertRatio(Ratio ratio, int numerator, int denominator) {
        assert ratio.numerator == numerator
        assert ratio.denominator == denominator
    }
}