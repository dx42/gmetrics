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
package org.gmetrics.metric.abc

import org.junit.jupiter.api.Test

/**
 * Tests for AbcMetric - calculate ABC complexity for methods
 *
 * @author Chris Mair
 */
class AbcMetric_MethodTest extends AbstractAbcMetricTest {

    static Class metricClass = AbcMetric

    @Test
	void testApplyToMethod() {
        final SOURCE = """
            def myMethod() { }
        """
        def result = applyToMethodValue(SOURCE)
        AbcTestUtil.assertEquals(result, ZERO_VECTOR)
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
	void testCalculate_ZeroResultForEmptyMethod() {
        final SOURCE = """
                def myMethod() { }
        """
        assertCalculateForMethod(SOURCE, ZERO_VECTOR)
    }

    @Test
	void testCalculate_CountsAssignmentsForVariableDeclarations() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    def x = 1               // A=1
                    int y                   // A=1 - implicit assignment to null
                }
            }
        """
        assertCalculateForMethod(SOURCE, [2, 0, 0])
    }

    @Test
	void testCalculate_IgnoresAssignmentsForConstantDeclarations() {
        final SOURCE = """
            def myMethod() {
                final CONST = 'abc'     // A=0
                String x = 'def'        // A=1
                final int C2 = 99       // A=0
            }
        """
        assertCalculateForMethod(SOURCE, [1, 0, 0])
    }

    @Test
	void testCalculate_CountsAssignmentsForIncrementAndDecrement() {
        final SOURCE = """
            def myMethod() {
                x ++                    // A=1
                y --                    // A=1
                ++y; --x                // A=2
            }
        """
        assertCalculateForMethod(SOURCE, [4, 0, 0])
    }

    @Test
	void testCalculate_CountsAssignmentsForArithmeticOperatorAssignment() {
        final SOURCE = """
            def myMethod() {
                y += 23                 // A=1
                x -= 23                 // A=1
                x /= 2; y*=3; x%=2      // A=3
            }
        """
        assertCalculateForMethod(SOURCE, [5, 0, 0])
    }

    @Test
	void testCalculate_CountsAssignmentsForShiftOperatorAssignment() {
        final SOURCE = """
            def myMethod() {
                y >>= 2; x<<=3;     // A=2
                y>>>=4              // A=1
            }
        """
        assertCalculateForMethod(SOURCE, [3, 0, 0])
    }

    @Test
	void testCalculate_CountsAssignmentsForElvisAssignment() {
        final SOURCE = """
            def myMethod() {
                y ?= 23     // A=1
            }
        """
        assertCalculateForMethod(SOURCE, [1, 0, 0])
    }

    @Test
	void testCalculate_CountsAssignmentsForBitwiseOperatorAssignment() {
        final SOURCE = """
            def myMethod() {
                x &= 2; y|=4; y^=3      // A=3
            }
        """
        assertCalculateForMethod(SOURCE, [3, 0, 0])
    }

    @Test
	void testCalculate_CountsBranchesForMethodCalls() {
        final SOURCE = """
            def myMethod() {
                println 'ok'                    // B=1
                someInstance.someMethod()       // B=1
                SomeClass.someStaticMethod(23)  // B=1
                other.method().getSomething()   // B=2
            }
        """
        assertCalculateForMethod(SOURCE, [0, 5, 0])
    }

    @Test
	void testCalculate_CountsBranchesForConstructorCalls() {
        final SOURCE = """
            def myMethod() {
                new SomeClass(99)               // B=1
                new SomeClass()                 // B=1
            }
        """
        assertCalculateForMethod(SOURCE, [0, 2, 0])
    }

    @Test
	void testCalculate_CountsBranchesForPropertyAccess() {
        final SOURCE = """
            def myMethod() {
                myObject.value              // B=1
            }
        """
        assertCalculateForMethod(SOURCE, [0, 1, 0])
    }

    @Test
	void testCalculate_CountsBranchesForNullSafeDereference() {
        final SOURCE = """
            def myMethod() {
                return x?.y                 // B=1                         
            }
        """
        // NOTE: Should this be counted as a condition instead of, or in addition to, a branch?
        assertCalculateForMethod(SOURCE, [0, 1, 0])
    }

    @Test
	void testCalculate_CountsBranchesForNullSafeIndexing() {
        final SOURCE = """
            def myMethod() {
                x?[1, 3]             // B=1                         
                return x?[1]         // B=1                         
            }
        """
        // NOTE: Should this be counted as a condition instead of, or in addition to, a branch?
        assertCalculateForMethod(SOURCE, [0, 2, 0])
    }

    @Test
	void testCalculate_CountsConditionsForComparisonOperators() {
        final SOURCE = """
            def myMethod() {
                x < 23              // C=1
                x <= 11             // C=1
                x > 99              // C=1
                x >= 22             // C=1
                x == 44             // C=1
                x != 1              // C=1
                x <=> y             // C=1
                x =~ /abc/          // C=1
                x ==~ /abc/         // C=1
                x === 3             // C=1
                x !== 3             // C=1
                x in [1, 2]         // C=1
                x !in [1, 2]        // C=1
                x instanceof String // C=1
                x !instanceof String // C=1
            }
        """
        assertCalculateForMethod(SOURCE, [0, 0, 15])
    }

    @Test
	void testCalculate_CountsConditionsForIfOnly() {
        final SOURCE = """
            def myMethod() {
                if (x < 23) {
                }
            }
        """
        assertCalculateForMethod(SOURCE, [0, 0, 1])
    }

    @Test
	void testCalculate_CountsConditionsForIfElse() {
        final SOURCE = """
            def myMethod() {
                if (x < 23) {
                }
                else { }
            }
        """
        assertCalculateForMethod(SOURCE, [0, 0, 2])
    }

    @Test
	void testCalculate_CountsConditionsForSwitchWithDefault() {
        final SOURCE = """
            def myMethod() {
                switch(x) {
                    case 1: break
                    case 3: break
                    default: break
                }
            }
        """
        assertCalculateForMethod(SOURCE, [0, 0, 3])
    }

    @Test
	void testCalculate_CountsConditionsForSwitchWithNoDefault() {
        final SOURCE = """
            def myMethod() {
                switch(x) {
                    case 1: break
                    case 3: break
                }
            }
        """
        assertCalculateForMethod(SOURCE, [0, 0, 2])
    }

    @Test
	void testCalculate_CountsConditionsForTryWithCatch() {
        final SOURCE = """
            def myMethod() {
                try {
                }
                catch(Exception e) { }
                catch(Throwable t) { }
            }
        """
        assertCalculateForMethod(SOURCE, [0, 0, 3])
    }

    @Test
	void testCalculate_CountsConditionsForTryWithoutCatch() {
        final SOURCE = """
            def myMethod() {
                try {           // C=1
                }
                finally {
                    a = 1       // A=1
                    b = 2       // A=1
                }
            }
        """
        assertCalculateForMethod(SOURCE, [2, 0, 1])
    }

    @Test
	void testCalculate_CountsConditionsForTryWithResources() {
        final SOURCE = """
            def myMethod() {
                try(File f = new File('abc.txt')) {
                }
            }
        """
        assertCalculateForMethod(SOURCE, [1, 1, 1])
    }

    @Test
	void testCalculate_CountsConditionsForTernaryOperator() {
        final SOURCE = """
            def myMethod() {
                return !(x < 23) ? 0 : 1
            }
        """
        assertCalculateForMethod(SOURCE, [0, 0, 2])
    }

    @Test
	void testCalculate_CountsConditionsForElvisOperator() {
        final SOURCE = """
            def myMethod() {
                return x ?: 1           // C=1 (for unary x) + 1 (for ?)
            }
        """
        assertCalculateForMethod(SOURCE, [0, 0, 2])
    }

    @Test
	void testCalculate_CountsConditionsForUnaryConditionals() {
        final SOURCE = """
            def myMethod(x = 0) {
                if (x || !y || z) {
                    23
                }
                if (y) { 99 }
            }
        """
        assertCalculateForMethod(SOURCE, [0, 0, 4])
    }

    @Test
	void testCalculate_CountsConditionsForMultipleBooleanConditionals() {
        final SOURCE = """
            def myMethod(x = 0) {
                return x && x > 0 && x < 100 && !ready      // C=4
            }
        """
        assertCalculateForMethod(SOURCE, [0, 0, 4])
    }

    @Test
	void testCalculate_CountsForConstructor() {
        final SOURCE = """
            class MyClass {
                MyClass() {
                    def x = 1; x++                         // A=2
                    doSomething()                          // B=1
                    if (x == 23) return 99 else return 0   // C=2
                }
            }
        """
        assertCalculateForConstructor(SOURCE, [2, 1, 2])
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
            @SomeAnnotation def myMethod() { 
                println 123
            }
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

    private void assertCalculateForMethod(String source, List expectedValues) {
        def result = calculateForMethod(source)
        AbcTestUtil.assertEquals(result, expectedValues)
    }

    private void assertCalculateForConstructor(String source, List expectedValues) {
        def result = calculateForConstructor(source)
        AbcTestUtil.assertEquals(result, expectedValues)
    }

}