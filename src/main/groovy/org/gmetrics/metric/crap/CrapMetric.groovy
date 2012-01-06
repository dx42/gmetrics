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

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.gmetrics.metric.AbstractMethodMetric
import org.gmetrics.metric.MetricLevel
import org.gmetrics.metric.cyclomatic.CyclomaticComplexityMetric
import org.gmetrics.result.MetricResult
import org.gmetrics.result.SingleNumberMetricResult
import org.gmetrics.source.SourceCode

/**
 * Metric to calculate the CRAP metric
 *
 * See http://www.artima.com/weblogs/viewpost.jsp?thread=210575
 *
 * @author Chris Mair
 */
class CrapMetric extends AbstractMethodMetric {

    private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP

    final String name = 'CRAP'

    Object coverageMetric // TODO type as Metric
    Object complexityMetric = new CyclomaticComplexityMetric()   // TODO type as Metric

    @Override
    MetricResult calculate(ClosureExpression closureExpression, SourceCode sourceCode) {
        return null
    }

    @Override
    MetricResult calculate(MethodNode methodNode, SourceCode sourceCode) {
        assert coverageMetric
        assert complexityMetric

        if (methodNode.isAbstract() || methodNode.lineNumber < 0) {
            return null
        }

        def complexityResult = complexityMetric.calculate(methodNode, sourceCode)
        if (complexityResult == null) {
            return null
        }
        def complexityValue = complexityResult['total']

        def coverageResult = coverageMetric.calculate(methodNode, sourceCode)
        if (coverageResult == null) {
            return null
        }
        def coverageValue = coverageResult['total']
        def crap = calculateCrapScore(complexityValue, coverageValue)

        return crap == null ? null : new SingleNumberMetricResult(this, MetricLevel.METHOD, crap, methodNode.lineNumber)
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    /**
     * Calculate the CRAP metric score
     * @param complexity - the cyclomatic complexity of a method
     * @param coverage - the code coverage (by line) of a method
     * @return the CRAP Metric score
     *
     * Given a Java method m, C.R.A.P. for m is calculated as follows:
     *
     *      C.R.A.P.(m) = comp(m)^2 * (1 – cov(m)/100)^3 + comp(m)
     *
     * See http://www.artima.com/weblogs/viewpost.jsp?thread=210575
     */
    protected BigDecimal calculateCrapScore(BigDecimal complexity, BigDecimal coverage) {
        if (complexity == null || coverage == null) {
            return null
        }

        def result = (complexity * complexity) * ((1.0 - coverage) ** 3) + complexity
        return result.setScale(2, ROUNDING_MODE)
    }

}