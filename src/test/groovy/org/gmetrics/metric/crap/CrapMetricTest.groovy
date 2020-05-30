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
 package org.gmetrics.metric.crap

import org.gmetrics.metric.AbstractMetricTestCase
import org.gmetrics.metric.MethodMetric
import org.gmetrics.metric.cyclomatic.CyclomaticComplexityMetric
import org.gmetrics.result.StubMetricResult
import org.junit.Before
import org.junit.Test

/**
 * Tests for CrapMetric
 *
 * @author Chris Mair
 */
class CrapMetricTest extends AbstractMetricTestCase {

    static Class metricClass = CrapMetric

    private static final String SOURCE = "def myMethod() { }"
    private static final BigDecimal DEFAULT_COVERAGE = 0.5
    private static final BigDecimal DEFAULT_COMPLEXITY = 10
    private static final BigDecimal DEFAULT_CRAP = 22.50

    private coverageMetricResult = metricResult(DEFAULT_COVERAGE)
    private complexityMetricResult = metricResult(DEFAULT_COMPLEXITY)

    @Before
    void setUp() {
        metric.coverageMetric = [applyToMethod:{ methodNode, sourceCode -> coverageMetricResult }] as MethodMetric
        metric.complexityMetric = [applyToMethod:{ methodNode, sourceCode -> complexityMetricResult }] as MethodMetric
    }

    @Test
	void testMetricName() {
        assert metric.name == 'CRAP'
    }

    @Test
	void testComplexityMetric_DefaultsToCyclomaticComplexityMetric() {
        assert new CrapMetric().complexityMetric instanceof CyclomaticComplexityMetric
    }

    @Test
	void testCalculate_CoverageMetricNotSet_ThrowsException() {
        metric.coverageMetric = null
        shouldFailWithMessageContaining('coverageMetric') { assert calculateForMethod(SOURCE) }
    }

    @Test
	void testCalculate_ComplexityMetricNotSet_ThrowsException() {
        metric.complexityMetric = null
        shouldFailWithMessageContaining('complexityMetric') { assert calculateForMethod(SOURCE) }
    }

    @Test
	void testApplyToMethod() {
        assert applyToMethodValue(SOURCE) == DEFAULT_CRAP
    }

    @Test
	void testCalculate_Valid_Values1() {
        assert calculateForMethod(SOURCE) == DEFAULT_CRAP
    }

    @Test
	void testCalculate_Valid_Values2() {
        complexityMetricResult = metricResult(5.0)
        coverageMetricResult = metricResult(0.00)
        assert calculateForMethod(SOURCE) == 30.0
    }

    @Test
	void testCalculate_ComplexityIsNull() {
        complexityMetricResult = new StubMetricResult(total:null)
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    @Test
	void testCalculate_ComplexityNotAvailable_ReturnsNull() {
        complexityMetricResult = null
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    @Test
	void testCalculate_CoverageNotAvailable_ReturnsNull() {
        coverageMetricResult = null
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    @Test
	void testCalculate_ReturnsNullForAbstractMethodDeclaration() {
        final SOURCE = """
            abstract class MyClass {
                abstract void doSomething()
            }
        """
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    @Test
	void testCalculate_ReturnsNullForSyntheticMethod() {
        final SOURCE = """
            println 123
        """
        def methodNode = findSyntheticMethod(SOURCE)
        assert metric.calculate(methodNode, sourceCode) == null
    }

    @Test
	void testCalculate_CountsForConstructor() {
        final SOURCE = """
            class MyClass {
                MyClass() {
                    doSomething()
                }
            }
        """
        assert calculateForConstructor(SOURCE) == DEFAULT_CRAP
    }

    @Test
    void testCalculate_CorrectLineNumberForAnnotatedMethod_NextLine() {
        final SOURCE = """
            @SomeAnnotation
            def myMethod() { }
        """
        assert metricLineNumber(findFirstMethod(SOURCE)) == 3
    }

    @Test
    void testCalculate_CorrectLineNumberForAnnotatedMethod_SameLine() {
        final SOURCE = """
            @SomeAnnotation def myMethod() { }
        """
        assert metricLineNumber(findFirstMethod(SOURCE)) == 2
    }

    @Test
    void testCalculate_CorrectLineNumberForAnnotatedMethod_CommentInBetween() {
        final SOURCE = """
            @SomeAnnotation
            // comment
            def myMethod() { }
        """
        assert metricLineNumber(findFirstMethod(SOURCE)) == 4
    }
    
    // Tests for applyToClass()

    @Test
	void testApplyToClass_OnlyClosureFields_ReturnsNull() {
        final SOURCE = """
            class MyClass {
                def closure = { }
            }
        """
        assert applyToClass(SOURCE) == null
    }

    // Tests for calculateCrapScore()

    @Test
	void testCalculateCrapScore() {
        assert metric.calculateCrapScore(0.0, 0.0) == 0
        assert metric.calculateCrapScore(1.0, 0.0) == 2.0
        assert metric.calculateCrapScore(1.00, 0.00) == 2.0
        assert metric.calculateCrapScore(1.00, 1.00) == 1.0
        assert metric.calculateCrapScore(5.00, 0.00) == 30.0
        assert metric.calculateCrapScore(5.00, 0.50) == 8.1      // round to scale=1
        assert metric.calculateCrapScore(5.00, 1.00) == 5.0
        assert metric.calculateCrapScore(10.00, 0.00) == 110.0
        assert metric.calculateCrapScore(10.00, 0.25) == 52.2   // round to scale=1
        assert metric.calculateCrapScore(10.00, 0.50) == 22.5
        assert metric.calculateCrapScore(10.00, 1.00) == 10.0
        assert metric.calculateCrapScore(20.00, 0.00) == 420.0
        assert metric.calculateCrapScore(20.00, 0.50) == 70.0
        assert metric.calculateCrapScore(30.00, 0.00) == 930.0
        assert metric.calculateCrapScore(30.00, 0.50) == 142.5
        assert metric.calculateCrapScore(30.00, 1.00) == 30.0

        assert metric.calculateCrapScore(0.0, null) == null
        assert metric.calculateCrapScore(null, 0.0) == null
    }

}