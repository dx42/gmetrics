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

/**
 * Tests for CoberturaBranchCoverageMetric
 *
 * @author Chris Mair
 */
class CoberturaBranchCoverageMetricTest extends AbstractCoberturaMetricTestCase {

    static metricClass = CoberturaBranchCoverageMetric

    private static final EMAIL_VALUE = 0.61
    private static final SERVICE_PACKAGE_VALUE = 0.65

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    void testHasProperName() {
        assert metric.name == 'CoberturaBranchCoverage'
    }

    // Tests for applyToClass()

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
        assertApplyToClass(SOURCE, EMAIL_VALUE, EMAIL_VALUE, ['String toString()':0.91])
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
        assertApplyToClass(SOURCE, 0.31, 0.31, ['void <init>(Collection)':0.32])
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
        assertApplyToClass(SOURCE, 0.61, 0.61, [
            'void <init>(String)':0.35,
            'void <init>(String, String)':0.45,
            'void <init>(String, Throwable)':0.55,
            'void <init>(Throwable)':0.65,
        ])
    }

    void testApplyToClass_ContainsInnerClasses() {
        final SOURCE = """
            package com.example.service
            class GenericLookupService {
                Map buildReverseLookupMap(Map map) { }
                Map get() { }
                Object getAllEnabledClients() { }
            }
        """
        assertApplyToClass(SOURCE, 0.92, 0.92, [
            'Map buildReverseLookupMap(Map)':1.00,
            'Map get()':1.00,
            'Object getAllEnabledClients()':1.00])
    }

    // Tests for applyToPackage()

    void testApplyToPackage() {
        assertApplyToPackage('com.example.service', SERVICE_PACKAGE_VALUE)
    }

    void testApplyToPackage_PackagePath() {
        assertApplyToPackage('com/example/service', SERVICE_PACKAGE_VALUE)
    }

    void testApplyToPackage_NullPath_ReturnsOverallValue() {
        assertApplyToPackage(null, 0.79)
    }

    // Test loading file using resource syntax

    void testLoadCoberturaFile_ClassPathResource() {
        metric.coberturaFile = COBERTURA_XML_CLASSPATH_PREFIX
        assertApplyToPackage('com.example.service', SERVICE_PACKAGE_VALUE)
    }

    void testLoadCoberturaFile_FileResource() {
        metric.coberturaFile = COBERTURA_XML_FILE_PREFIX
        assertApplyToPackage('com.example.service', SERVICE_PACKAGE_VALUE)
    }

    // Tests for getBranchCoverageRatioForClass

    void testGetBranchCoverageRatioForClass() {
        assertRatio(metric.getBranchCoverageRatioForClass('com.example.service.Email'), 2, 6)
    }

    void testGetBranchCoverageRatioForClass_ClassContainingClosures() {
        assertRatio(metric.getBranchCoverageRatioForClass('com.example.service.GenericLookupService'), 11, 12)
    }

    void testGetBranchCoverageRatioForClass_EmptyClass() {
        assertRatio(metric.getBranchCoverageRatioForClass('com.example.service.ClientMappingDao'), 0, 0)
    }

    void testGetBranchCoverageRatioForClass_NoSuchClass_ReturnsNull() {
        assert metric.getBranchCoverageRatioForClass('NoSuchClass') == null
    }

}