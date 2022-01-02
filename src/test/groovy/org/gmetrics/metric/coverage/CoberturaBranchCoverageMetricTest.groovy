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

import org.junit.jupiter.api.Test

/**
 * Tests for CoberturaBranchCoverageMetric
 *
 * @author Chris Mair
 */
class CoberturaBranchCoverageMetricTest extends AbstractCoberturaMetricTestCase {

    static Class metricClass = CoberturaBranchCoverageMetric

    private static final BigDecimal EMAIL_VALUE = 0.61

	@Override
	protected BigDecimal getRootPackageValue() {
		return 0.79
	}
	
	@Override
	protected BigDecimal getServicePackageValue() {
		return 0.65
	}

	
    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    @Test
	void testHasProperName() {
        assert metric.name == 'CoberturaBranchCoverage'
    }

    // Tests for applyToMethod()

    @Test
	void testApplyToMethod_EnabledIsFalse_ReturnsNull() {
        final SOURCE = """
            package com.example.service
            class Email {
                String toString() { }
            }
        """
        metric.enabled = false
        assert applyToMethod(SOURCE) == null
    }

    @Test
	void testApplyToMethod() {
        final SOURCE = """
            package com.example.service
            class Email {
                String toString() { }
            }
        """
        assert applyToMethodValue(SOURCE) == 0.91
    }

    // Tests for calculate()

    @Test
	void testCalculate() {
        final SOURCE = """
            package com.example.service
            class Email {
                String toString() { }
            }
        """
        assert calculateForMethod(SOURCE) == 0.91
    }

    @Test
	void testCalculate_MethodThatHasNoCoverageInformation() {
        final SOURCE = """
            package com.example.service
            class Email {
                int unknown() { }
            }
        """
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    @Test
	void testCalculate_ReturnsNullForAbstractMethodDeclaration() {
        final SOURCE = """
            package com.example.service
            class Email {
                abstract String getId()
            }
        """
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    @Test
	void testCalculate_Constructor() {
        final SOURCE = """
            package com.example.service
            class Context {
                Context(Collection stuff) { }
            }
        """
        assert calculateForConstructor(SOURCE) == 0.32
    }

    // Tests for applyToClass()

    @Test
	void testApplyToClass_ClassWithNoMethods() {
        final SOURCE = """
            package com.example.service
            class Email { }
        """
        assertApplyToClass(SOURCE, EMAIL_VALUE, 0)
    }

    @Test
	void testApplyToClass_ClassWithOneMethod() {
        final SOURCE = """
            package com.example.service
            class Email {
                String toString() { }
            }
        """
        assertApplyToClass(SOURCE, EMAIL_VALUE, 0.91, ['String toString()':0.91])
    }

    @Test
	void testApplyToClass_ClassWithMethodThatHasNoCoverageInformation() {
        final SOURCE = """
            package com.example.service
            class Email {
                int unknown() { }
            }
        """
        assertApplyToClass(SOURCE, EMAIL_VALUE, 0)
    }

    @Test
	void testApplyToClass_IgnoresAbstractMethods() {
        final SOURCE = """
            package com.example.service
            class Email {
                abstract String getId()
            }
        """
        assertApplyToClass(SOURCE, EMAIL_VALUE, 0)
    }

    @Test
	void testApplyToClass_Constructor() {
        final SOURCE = """
            package com.example.service
            class Context {
                Context(Collection stuff) { }
            }
        """
        assertApplyToClass(SOURCE, 0.31, 0.32, ['void <init>(Collection)':0.32])
    }

    @Test
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
        assertApplyToClass(SOURCE, 0.61, 0.5, [
            'void <init>(String)':0.35,
            'void <init>(String, String)':0.45,
            'void <init>(String, Throwable)':0.55,
            'void <init>(Throwable)':0.65,
        ])
    }

    @Test
	void testApplyToClass_ContainsInnerClasses() {
        final SOURCE = """
            package com.example.service
            class GenericLookupService {
                Map buildReverseLookupMap(Map map) { }
                Map get() { }
                Object getAllEnabledClients() { }
            }
        """
        assertApplyToClass(SOURCE, 0.92, 0.6, [
            'Map buildReverseLookupMap(Map)':0.8,
            'Map get()':0.5,
            'Object getAllEnabledClients()':0.5])
    }

    // Tests for getCoverageRatioForClass

    @Test
	void testGetCoverageRatioForClass() {
        assertRatio(metric.getCoverageRatioForClass('com.example.service.Email'), 2, 6)
    }

    @Test
	void testGetCoverageRatioForClass_ClassContainingClosures() {
        assertRatio(metric.getCoverageRatioForClass('com.example.service.GenericLookupService'), 11, 12)
    }

    @Test
	void testGetCoverageRatioForClass_EmptyClass() {
        assertRatio(metric.getCoverageRatioForClass('com.example.service.ClientMappingDao'), 0, 0)
    }

    @Test
	void testGetCoverageRatioForClass_NoSuchClass_ReturnsNull() {
        assert metric.getCoverageRatioForClass('NoSuchClass') == null
    }

}