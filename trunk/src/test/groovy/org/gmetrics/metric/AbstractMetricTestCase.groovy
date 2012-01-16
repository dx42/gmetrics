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

import org.gmetrics.result.MethodKey
import org.gmetrics.result.SingleNumberMetricResult

/**
 * Abstract superclass for metric test classes.
 *
 * Subclasses must define a property named 'metricClass' that specifies the Metric class under test.
 *
 * @author Chris Mair
 */
abstract class AbstractMetricTestCase extends AbstractTestCase {

    private static final METRIC = [getFunctions:{ ['total', 'average'] }] as Metric
    protected static final CONSTRUCTOR_NAME = '<init>'
    protected static final DEFAULT_CONSTRUCTOR = 'void <init>()'
    protected static final RUN_METHOD = 'java.lang.Object run()'

    protected metric
    protected sourceCode

    void setUp() {
        super.setUp()
        Class mClass = getProperty('metricClass')
        metric = mClass.newInstance()
    }

    protected applyToMethod(String source) {
        return metric.applyToMethod(findFirstMethod(source), sourceCode)
    }

    protected applyToMethodValue(String source) {
        def metricResult = metric.applyToMethod(findFirstMethod(source), sourceCode)
        log("metricResult=$metricResult")
        assertMetricForMetricResult(metricResult)
        return valueFromMetricResult(metricResult)
    }

    protected applyToClosure(String source) {
        return metric.applyToClosure(findFirstClosureExpression(source), sourceCode)
    }

    protected applyToClosureValue(String source) {
        def metricResult = metric.applyToClosure(findFirstClosureExpression(source), sourceCode)
        log("metricResult=$metricResult")
        assertMetricForMetricResult(metricResult)
        return valueFromMetricResult(metricResult)
    }

    protected calculate(node) {
        def metricResult = metric.calculate(node, sourceCode)
        log("metricResult=$metricResult")
        assertMetricForMetricResult(metricResult) 
        return valueFromMetricResult(metricResult)
    }

    protected valueFromMetricResult(MetricResult metricResult) {
        return metricResult['total']
    }

    protected parseClass(String source) {
        sourceCode = new SourceString(source)
        assert sourceCode.valid
        def ast = sourceCode.ast
        return ast.classes[0]
    }

    protected findFirstMethod(String source) {
        def classNode = parseClass(source)
        def methodNode = classNode.methods.find { it.lineNumber >= 0 }
        assert methodNode
        return methodNode
    }

    protected calculateForMethod(String source) {
        return calculate(findFirstMethod(source))
    }

    protected void assertCalculateForMethodReturnsNull(String source) {
        assert metric.calculate(findFirstMethod(source), sourceCode) == null
    }

    protected findSyntheticMethod(String source) {
        def classNode = parseClass(source)
        return classNode.methods.find { it.lineNumber < 0 && it.name != 'run'}
    }

    protected calculateForConstructor(String source) {
        def classNode = parseClass(source)
        def constructorNode = classNode.declaredConstructors[0]
        assert constructorNode
        return calculate(constructorNode)
    }

    protected calculateForClosureField(String source) {
        return calculate(findFirstClosureExpression(source))
    }

    protected ClosureExpression findFirstClosureExpression(String source) {
        def fieldNode = findFirstField(source)
        assert fieldNode.initialExpression
        assert fieldNode.initialExpression instanceof ClosureExpression
        return fieldNode.initialExpression
    }

    protected applyToClass(String source) {
        def classNode = parseClass(source)
        def results = metric.applyToClass(classNode, sourceCode)
        log("results=$results")
        return results
    }

    protected void assertApplyToClass(String source, classTotalValue, classAverageValue=classTotalValue, Map methodValues=null) {
        def results = applyToClass(source)
        def classMetricResult = results.classMetricResult
        assert classMetricResult.metricLevel == MetricLevel.CLASS
        assertEquals('average', classAverageValue, classMetricResult['average'])
        assertEquals('total', classTotalValue, classMetricResult['total'])

        def methodMetricResults = results.methodMetricResults
        assertBothAreFalseOrElseNeitherIs(methodValues, methodMetricResults) 

        def methodNames = methodValues?.keySet()
        methodNames.each { methodName ->
            def methodKey = new MethodKey(methodName)
            def metricResults = methodMetricResults[methodKey]
            assert metricResults, "No MetricResults exist for method named [$methodName]"
            assert metricResults.metricLevel == MetricLevel.METHOD
            def methodValue = metricResults['total']
            assertEquals("methodName=$methodName", methodValues[methodName], methodValue)
        }
    }

    protected void assertApplyToPackage(Collection childMetricResults, classTotalValue, classAverageValue) {
        assertApplyToPackage(null, childMetricResults, classTotalValue, classAverageValue)
    }

    protected void assertApplyToPackage(String packageName, Collection childMetricResults, totalValue, averageValue) {
        def metricResult = metric.applyToPackage(packageName, childMetricResults)
        assert metricResult, "No MetricResult for package [$packageName]"
        assert metricResult['total'] == totalValue
        assert metricResult['average'] == averageValue
    }

    protected void assertApplyToPackage(String packageName, value) {
        assertApplyToPackage(packageName, null, value, value)
    }

    protected void assertMetricForMetricResult(MetricResult metricResult) {
        assert metricResult.metric == metric
    }

    protected MetricResult metricResult(number) {
        new SingleNumberMetricResult(METRIC, MetricLevel.METHOD, number)
    }

    protected MetricResult metricResultForClass(number) {
        new SingleNumberMetricResult(METRIC, MetricLevel.CLASS, number)
    }

    protected MetricResult metricResultForPackage(number) {
        new SingleNumberMetricResult(METRIC, MetricLevel.PACKAGE, number)
    }

    protected findFirstField(String source) {
        def classNode = parseClass(source)
        return classNode.fields.find { it.lineNumber >= 0 }
    }

}