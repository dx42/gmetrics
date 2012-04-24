/*
 * Copyright 2010 the original author or authors.
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
package org.gmetrics.metric.cyclomatic

import org.gmetrics.metric.AbstractMetricTestCase

/**
 * Tests for CyclomaticComplexityMetric - for methods
 *
 * @author Chris Mair
 */
class CyclomaticComplexityMetric_MethodTest extends AbstractMetricTestCase {

    static metricClass = CyclomaticComplexityMetric

    void testApplyToMethod() {
        final SOURCE = """
            def myMethod() { }
        """
        assert applyToMethodValue(SOURCE) == 1
    }

    void testCalculate_ReturnsOne_ForEmptyMethod() {
        final SOURCE = """
            def myMethod() { }
        """
        assert calculateForMethod(SOURCE) == 1
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
        assert calculateForConstructor(SOURCE) == 1
    }

    void testCalculate_IncrementsForEach_If() {
        final SOURCE = """
            def myMethod() {
                if (ready) { }
                if (printing)
                    println 'pr'
                else println 'no'
            }
        """
        assert calculateForMethod(SOURCE) == 3
    }

    void testCalculate_IncrementsForEach_While() {
        final SOURCE = """
            def myMethod() {
                while (ready) { }
                while (!done) {
                    while(true) { println '*' }
                }
            }
        """
        assert calculateForMethod(SOURCE) == 4
    }

    void testCalculate_IncrementsForEach_ClassicForLoop() {
        final SOURCE = """
            def myMethod() {
                for (int i=0; i < 99; i++) {
                    for (int j=0; j < 99; j++) {
                    }
                }
            }
        """
        assert calculateForMethod(SOURCE) == 3
    }

    void testCalculate_IncrementsForEach_ForInLoop() {
        final SOURCE = """
            def myMethod() {
                for (x in [1,2,3]) {
                    for (y in [4,5,6]) {
                    }
                }
            }
        """
        assert calculateForMethod(SOURCE) == 3
    }

    void testCalculate_IncrementsForEach_JavaStyleForEachLoop() {
        final SOURCE = """
            def myMethod() {
                for(int x: [1,2,3]) {
                    for(int y: [4,5,6]) {
                    }
                }
            }
        """
        assert calculateForMethod(SOURCE) == 3
    }

    void testCalculate_IncrementsForEach_Case() {
        final SOURCE = """
            def myMethod() {
                switch(x) {
                    case 0: println 'zero'
                    case 1: println 'one'
                    default: println 'default'
                }
            }
        """
        assert calculateForMethod(SOURCE) == 3
    }

    void testCalculate_IncrementsForEach_Catch() {
        final SOURCE = """
            def myMethod() {
                try {
                }
                catch(Exception e) { }
                catch(Throwable t) {
                    t.printStackTrace()
                    try { } catch(Throwable ignore) { println 'error' }
                }
                finally { }
            }
        """
        assert calculateForMethod(SOURCE) == 4
    }

    void testCalculate_IncrementsForEach_And_Or() {
        final SOURCE = """
            def myMethod() {
                done = x && y               // +1
                ready = x < 10
                error = y <= 0 || z == 1    // +1
            }
        """
        assert calculateForMethod(SOURCE) == 3
    }

    void testCalculate_IncrementsForEach_TernaryOperator() {
        final SOURCE = """
            def myMethod() {
                value = ready ? 0 : 1       // +1
            }
        """
        assert calculateForMethod(SOURCE) == 2
    }

    void testCalculate_IncrementsForEach_ElvisOperator() {
        final SOURCE = """
            def myMethod() {
                value = ready ?: null       // +1
            }
        """
        assert calculateForMethod(SOURCE) == 2
    }

    void testCalculate_IncrementsForEach_NullCheckOperator() {
        final SOURCE = """
            def myMethod() {
                return parameter?.value       // +1
                println parameter.other
            }
        """
        assert calculateForMethod(SOURCE) == 2
    }

    void testCalculate_ProperCount_Combination() {
        final SOURCE = """
            def myMethod() {
                try {
                    if (ready?.cutoff) { }              // +1
                    for (x in [1,2,3]) {                // +1
                        switch(x) {
                            case 0: println 'zero'      // +1
                            case 1: println 'one'       // +1
                            default:
                                done = x && y           // +1
                                open = y == 0 || z      // +1
                                name = x ? "X" : "None" // +1
                                value = value ?: 'bad'  // +1
                        }
                    }
                }
                catch(Exception e) {                    // +1
                    while (!done) { }                   // +1
                }
                catch(Throwable t) {                    // +1
                    t.printStackTrace()

                }
                finally { }
            }
        """
        assert calculateForMethod(SOURCE) == 13
    }

    void testCalculate_MethodContainingNestedClosure() {
        final SOURCE = """
            def myMethod() {
                if (ready) { }
                def action = { result = started || paused }
                def runnable = { println a && b } as Runnable
            }
        """
        assert calculateForMethod(SOURCE) == 4
    }

    void testCalculate_NullCheckOperator_OnMethodCalls() {
        final SOURCE = """
            private Map buildServerMap(serverCodes) {
                Map serverMap = new HashMap()
                for(serverCode in serverCodes){
                    def index = serverCode?.indexOf("=")
                    def length = serverCode?.length()
                    if(index>0){
                        serverMap.put(serverCode?.substring(0, index)?.trim(), serverCode?.substring(index+1,length)?.trim())
                    }
                }
                return serverMap
            }
        """
        assert calculateForMethod(SOURCE) == 9
    }

}