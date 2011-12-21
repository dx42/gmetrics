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
import org.gmetrics.metric.MetricLevel
import org.gmetrics.metric.Metric

/**
 * Tests for CoberturaLineCoverageMetric
 *
 * @author Chris Mair
 */
class CoberturaLineCoverageMetricTest extends AbstractMetricTestCase {

    static metricClass = CoberturaLineCoverageMetric

    private static final COBERTURA_XML_FILENAME = 'Cobertura-example-small.xml'
    private static final COBERTURA_XML_RELATIVE_PATH = 'src/test/resources/coverage/' + COBERTURA_XML_FILENAME
    private static final COBERTURA_XML_RELATIVE_TO_CLASSPATH = 'coverage/' + COBERTURA_XML_FILENAME
    private static final COBERTURA_XML_FILE_PREFIX = 'file:' + COBERTURA_XML_RELATIVE_PATH
    private static final COBERTURA_XML_CLASSPATH_PREFIX = 'classpath:' + COBERTURA_XML_RELATIVE_TO_CLASSPATH
    private static final CHANNEL_VALUE = (14 / 16 as BigDecimal).setScale(2, BigDecimal.ROUND_HALF_UP)
    private static final EMAIL_VALUE = 0.66

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    void testBaseLevelIsMethod() {
        assert metric.baseLevel == MetricLevel.METHOD
    }

    void testHasProperName() {
        assert metric.name == 'CoberturaLineCoverage'
    }

    void testImplementsMetricInterface() {
        assert metric instanceof Metric
    }

    // Tests for applyToClass()

    void testApplyToClass_ReturnNullForInterface() {
        final SOURCE = """
            package com.example.model
            interface Channel { }
        """
        assert applyToClass(SOURCE) == null
    }

    void testApplyToClass_ClassWithNoMethods() {
        final SOURCE = """
            package com.example.service
            class Email { }
        """
        assertApplyToClass(SOURCE, EMAIL_VALUE)
    }

    void testApplyToClass_ClassWithOneMethod() {
        final SOURCE = """
            package com.example.service
            class Email {
                String toString() { }
            }
        """
        assertApplyToClass(SOURCE, EMAIL_VALUE, EMAIL_VALUE, ['String toString()':0.99])
    }

    void testApplyToClass_ClassWithMethodThatHasNoCoverageInformation() {
        final SOURCE = """
            package com.example.service
            class Email {
                int unknown() { }
            }
        """
        assertApplyToClass(SOURCE, EMAIL_VALUE)
    }

    void testApplyToClass_IgnoresAbstractMethods() {
        final SOURCE = """
            package com.example.service
            class Email {
                abstract String getId()
            }
        """
        assertApplyToClass(SOURCE, EMAIL_VALUE)
    }

    void testApplyToClass_Constructor() {
        final SOURCE = """
            package com.example.service
            class Context {
                Context(Collection stuff) { }
            }
        """
        assertApplyToClass(SOURCE, 0.11, 0.11, ['void <init>(Collection)':0.22])
    }

    void testApplyToClass_OverloadedConstructor() {
        final SOURCE = """
            package com.example.service
            class MyException {
                MyException(String name) { }
                MyException(String name, String id) { }
                MyException(String name, Throwable cause) { }
                MyException(Throwable cause) { }
            }
        """
        assertApplyToClass(SOURCE, 0.66, 0.66, [
            'void <init>(String)':0.3,
            'void <init>(String, String)':0.4,
            'void <init>(String, Throwable)':0.5,
            'void <init>(Throwable)':0.6,
        ])
    }

    void testApplyToClass_ContainsInnerClasses() {
        final SOURCE = """
            package com.example.model
            class Channel {
                Channel(String name, int count, String code) { }
                static Channel parse(String text) { }
                String getId() { }
            }
        """
        assertApplyToClass(SOURCE, CHANNEL_VALUE, CHANNEL_VALUE, [
            'void <init>(String, int, String)':0.9,
            'Channel parse(String)':1.0,
            'String getId()':1.0])
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
        assertApplyToPackage('com.example.service', null, 0.85, 0.85)
    }

    void testApplyToPackage_PackagePath() {
        assertApplyToPackage('com/example/service', null, 0.85, 0.85)
    }

    void testApplyToPackage_NullPath_ReturnsOverallValue() {
        assertApplyToPackage(null, 0.95)
    }

    void testApplyToPackage_NoCoverageInformation() {
        metric.applyToPackage('no.such.package', null) == null
    }

    // Test loading file using resource syntax

    void testLoadCoberturaFile_ClassPathResource() {
        metric.coberturaFile = COBERTURA_XML_CLASSPATH_PREFIX
        assertApplyToPackage('com.example.service', null, 0.85, 0.85)
    }

    void testLoadCoberturaFile_FileResource() {
        metric.coberturaFile = COBERTURA_XML_FILE_PREFIX
        assertApplyToPackage('com.example.service', null, 0.85, 0.85)
    }

    // Tests for getLineCoverageRatioForClass

    void testGetLineCoverageRatioForClass() {
        assertRatio(metric.getLineCoverageRatioForClass('com.example.service.MyException'), 16, 24)
    }

    void testGetLineCoverageRatioForClass_ClassContainingClosures() {
        assertRatio(metric.getLineCoverageRatioForClass('com.example.model.Channel'), 14, 16)
    }

    void testGetLineCoverageRatioForClass_EmptyClass() {
        assertRatio(metric.getLineCoverageRatioForClass('com.example.service.ClientMappingDao'), 0, 0)
    }

    void testGetLineCoverageRatioForClass_NoSuchClass_ReturnsNull() {
        assert metric.getLineCoverageRatioForClass('NoSuchClass') == null
    }


    //------------------------------------------------------------------------------------
    // Set up and helper methods
    //------------------------------------------------------------------------------------

    void setUp() {
        super.setUp()
        metric.coberturaFile = COBERTURA_XML_RELATIVE_TO_CLASSPATH
    }

    private void assertRatio(Ratio ratio, int numerator, int denominator) {
        assert ratio.numerator == numerator
        assert ratio.denominator == denominator
    }
}