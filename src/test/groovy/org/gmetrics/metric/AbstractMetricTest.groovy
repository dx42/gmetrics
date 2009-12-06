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
package org.gmetrics.metric

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.source.SourceString
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.gmetrics.result.MetricResult
import org.gmetrics.result.NumberMetricResult

/**
 * Abstract superclass for metric test classes.
 *
 * Subclasses must define a property named 'metricClass' that specifies the Metric class under test.
 *
 * @author Chris Mair
 * @version $Revision: 224 $ - $Date: 2009-09-22 22:04:03 -0400 (Tue, 22 Sep 2009) $
 */
abstract class AbstractMetricTest extends AbstractTestCase {

    private static final METRIC = [:] as Metric
    protected metric
    protected sourceCode

    void setUp() {
        super.setUp()
        Class mClass = getProperty('metricClass')
        metric = mClass.newInstance()
    }

    protected calculate(node) {
        def metricResult = metric.calculate(node, sourceCode)
        log("metricResult=$metricResult")
        assertMetricForMetricResult(metricResult) 
        return valueFromMetricResult(metricResult)
    }

    protected valueFromMetricResult(MetricResult metricResult) {
        return metricResult.value
    }

    protected parseClass(String source) {
        sourceCode = new SourceString(source)
        def ast = sourceCode.ast
        return ast.classes[0]
    }

    protected calculateForMethod(String source) {
        def classNode = parseClass(source)
        def methodNode = classNode.methods.find { it.lineNumber >= 0 }
        assert methodNode
        return calculate(methodNode)
    }

    protected calculateForConstructor(String source) {
        def classNode = parseClass(source)
        def constructorNode = classNode.declaredConstructors[0]
        assert constructorNode
        return calculate(constructorNode)
    }

    protected calculateForClosureField(String source) {
        def fieldNode = findFirstField(source)
        assert fieldNode.initialExpression
        assert fieldNode.initialExpression instanceof ClosureExpression
        return calculate(fieldNode.initialExpression)
    }

    protected void assertApplyToClass(String source, classTotalValue, classAverageValue, Map methodValues) {
        def classNode = parseClass(source)
        def results = metric.applyToClass(classNode, sourceCode)
        log("results=$results")
        def classMetricResult = results.classMetricResult
        assertEquals(classMetricResult.averageValue, classAverageValue)
        assertEquals(classMetricResult.totalValue, classTotalValue)

        def methodMetricResults = results.methodMetricResults
        assertBothAreFalseOrElseNeitherIs(methodValues, methodMetricResults) 

        def methodNames = methodValues?.keySet()
        methodNames.each { methodName ->
            def methodValue = methodMetricResults[methodName].value
            assertEquals(methodValue, methodValues[methodName])
        }
    }

    protected void assertApplyToPackage(Collection childMetricResults, classTotalValue, classAverageValue) {
        def metricResult = metric.applyToPackage(childMetricResults)
        assert metricResult.totalValue == classTotalValue
        assert metricResult.averageValue == classAverageValue
    }

    protected void assertMetricForMetricResult(MetricResult metricResult) {
        assert metricResult.metric == metric
    }

    protected MetricResult metricResult(number) {
        new NumberMetricResult(METRIC, number)
    }

    private findFirstField(String source) {
        def classNode = parseClass(source)
        return classNode.fields.find { it.lineNumber >= 0 }
    }

}