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
import org.gmetrics.metric.cyclomatic.CyclomaticComplexityMetric
import org.gmetrics.result.StubMetricResult
import org.gmetrics.result.MetricResult

/**
 * Tests for CrapMetric
 *
 * @author Chris Mair
 */
class CrapMetricTest extends AbstractMetricTestCase {

    static metricClass = CrapMetric

    private static final SOURCE = "def myMethod() { }"
    private static final DEFAULT_COVERAGE = 0.5
    private static final DEFAULT_COMPLEXITY = 10
    private static final DEFAULT_CRAP = 22.50

    private coverageMetricResult = metricResult(DEFAULT_COVERAGE)
    private complexityMetricResult = metricResult(DEFAULT_COMPLEXITY)

    @Override
    void setUp() {
        super.setUp()
        metric.coverageMetric = [calculate:{ methodNode, sourceCode -> coverageMetricResult }]
        metric.complexityMetric = [calculate:{ methodNode, sourceCode -> complexityMetricResult }]
    }

    void testMetricName() {
        assert metric.name == 'CRAP'
    }

    void testComplexityMetric_DefaultsToCyclomaticComplexityMetric() {
        assert new CrapMetric().complexityMetric instanceof CyclomaticComplexityMetric
    }

    void testCalculate_CoverageMetricNotSet_ThrowsException() {
        metric.coverageMetric = null
        shouldFailWithMessageContaining('coverageMetric') { assert calculateForMethod(SOURCE) }
    }

    void testCalculate_ComplexityMetricNotSet_ThrowsException() {
        metric.complexityMetric = null
        shouldFailWithMessageContaining('complexityMetric') { assert calculateForMethod(SOURCE) }
    }

    void testCalculate_Valid_Values1() {
        assert calculateForMethod(SOURCE) == DEFAULT_CRAP
    }

    void testCalculate_Valid_Values2() {
        complexityMetricResult = metricResult(5.0)
        coverageMetricResult = metricResult(0.00)
        assert calculateForMethod(SOURCE) == 30.0
    }

    void testCalculate_ComplexityNotAvailable_ReturnsNull() {
        complexityMetricResult = null
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    void testCalculate_CoverageNotAvailable_ReturnsNull() {
        coverageMetricResult = null
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    void testCalculate_ReturnsNullForAbstractMethodDeclaration() {
        final SOURCE = """
            abstract class MyClass {
                abstract void doSomething()
            }
        """
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    void testCalculate_ReturnsNullForSyntheticMethod() {
        final SOURCE = """
            println 123
        """
        def methodNode = findSyntheticMethod(SOURCE)
        assert metric.calculate(methodNode, sourceCode) == null
    }

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

    // Tests for applyToClass()

    void testApplyToClass_OnlyClosureFields_ReturnsNull() {
        final SOURCE = """
            class MyClass {
                def closure = { }
            }
        """
        assert applyToClass(SOURCE) == null
    }

    // Tests for calculateCrapScore()

    void testCalculateCrapScore() {
        assert metric.calculateCrapScore(0.0, 0.0) == 0
        assert metric.calculateCrapScore(1.0, 0.0) == 2.0
        assert metric.calculateCrapScore(1.00, 0.00) == 2.00
        assert metric.calculateCrapScore(1.00, 1.00) == 1.00
        assert metric.calculateCrapScore(5.00, 0.00) == 30.00
        assert metric.calculateCrapScore(5.00, 0.50) == 8.13       // round to scale=2
        assert metric.calculateCrapScore(5.00, 1.00) == 5.00
        assert metric.calculateCrapScore(10.00, 0.00) == 110.00
        assert metric.calculateCrapScore(10.00, 0.25) == 52.19   // round to scale=2
        assert metric.calculateCrapScore(10.00, 0.50) == 22.50
        assert metric.calculateCrapScore(10.00, 1.00) == 10.00
        assert metric.calculateCrapScore(20.00, 0.00) == 420.00
        assert metric.calculateCrapScore(20.00, 0.50) == 70.00
        assert metric.calculateCrapScore(30.00, 0.00) == 930.00
        assert metric.calculateCrapScore(30.00, 0.50) == 142.50
        assert metric.calculateCrapScore(30.00, 1.00) == 30.00
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    private static MetricResult metricResult(BigDecimal value) {
        return new StubMetricResult(total:value)
    }

}