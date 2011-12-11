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

    private static final COBERTURA_FILE = 'src/test/resources/coverage/Cobertura-example-small.xml'

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    // TODO Synthetic methods?
    // TODO Overloaded methods
    // TODO Methods with other primitive parameter types: int, long, boolean, char, float, double
    // TODO Class containing inner classes and also closures

    void testBaseLevelIsMethod() {
        assert metric.baseLevel == MetricLevel.METHOD
    }

    void testHasProperName() {
        assert metric.name == 'CoberturaLineCoverage'
    }

    void testImplementsMetricInterface() {
        assert metric instanceof Metric
    }

    void testApplyToClass_ReturnNullForInterface() {
        final SOURCE = """
            package com.example.model
            interface Channel { }
        """
        assert applyToClass(SOURCE) == null
    }

    void testApplyToClass_ClassWithNoMethods() {
        final SOURCE = """
            package com.example.model
            class Channel { }
        """
        assertApplyToClass(SOURCE, 0.8, 0.8)
    }

    void testApplyToClass_ClassWithOneMethod() {
        final SOURCE = """
            package com.example.model
            class Channel {
                String getId() { }
            }
        """
        assertApplyToClass(SOURCE, 0.8, 0.8, ['String getId()':1.0])
    }

    void testApplyToClass_ClassWithMethodThatHasNoCoverageInformation() {
        final SOURCE = """
            package com.example.model
            class Channel {
                int unknown() { }
            }
        """
        assertApplyToClass(SOURCE, 0.8, 0.8)
    }

    void testApplyToClass_IgnoresAbstractMethods() {
        final SOURCE = """
            package com.example.model
            class Channel {
                abstract String getId()
            }
        """
        assertApplyToClass(SOURCE, 0.8, 0.8)
    }

    void testApplyToClass_Constructor() {
        final SOURCE = """
            package com.example.model
            class Channel {
                Channel(String name, int id, String id) { }
            }
        """
        assertApplyToClass(SOURCE, 0.8, 0.8, ['void <init>(String, int, String)':0.9])
    }

    void testApplyToClass_OverloadedConstructor() {
        final SOURCE = """
            package com.example.service.clientmapping
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

    void testApplyToClass_CoberturaFileNotSet_ThrowsException() {
        final SOURCE = 'class Channel { }'
        metric.coberturaFile = null
        shouldFailWithMessageContaining('coberturaFile') {
            applyToClass(SOURCE)
        }
    }

    // Tests for applyToPackage()

    void testApplyToPackage() {
        assertApplyToPackage('com.example.service.clientmapping', null, 0.85, 0.85)
    }

    void testApplyToPackage_PackagePath() {
        assertApplyToPackage('com/example/service/clientmapping', null, 0.85, 0.85)
    }

    void testApplyToPackage_NoCoverageInformation() {
        metric.applyToPackage('no.such.package', null) == null
    }

    //------------------------------------------------------------------------------------
    // Set up and helper methods
    //------------------------------------------------------------------------------------

    void setUp() {
        super.setUp()
        metric.coberturaFile = COBERTURA_FILE
    }

}