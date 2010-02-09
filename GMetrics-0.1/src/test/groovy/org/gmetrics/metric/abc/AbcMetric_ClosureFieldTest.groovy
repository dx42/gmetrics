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

/**
 * Tests for AbcMetric for fields initialized to a Closure
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbcMetric_ClosureFieldTest extends AbstractAbcMetricTest {
    static metricClass = AbcMetric

    void testCalculate_ZeroResultForEmptyClosure() {
        final SOURCE = """
            class MyClass {
                def myClosure = {
                }
            }
        """
        assertCalculateForClosureField(SOURCE, ZERO_VECTOR)
    }

    void testCalculate_CountsForClosureField() {
        final SOURCE = """
            class MyClass {
                def myClosure = {
                    def x = 1; x++                         // A=2
                    doSomething()                          // B=1
                    if (x == 23) return 99 else return 0   // C=2
                }
            }
        """
        assertCalculateForClosureField(SOURCE, [2, 1, 2])
    }

    private void assertCalculateForClosureField(String source, List expectedValues) {
        def result = calculateForClosureField(source)
        AbcTestUtil.assertEquals(result, expectedValues)
    }

 }