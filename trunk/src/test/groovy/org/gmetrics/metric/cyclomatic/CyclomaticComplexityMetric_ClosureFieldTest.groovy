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
 * Tests for CyclomaticComplexityMetric - for closure fields
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class CyclomaticComplexityMetric_ClosureFieldTest extends AbstractMetricTestCase {
    static metricClass = CyclomaticComplexityMetric

    void testCalculate_ReturnsOne_ForEmptyClosureField() {
        final SOURCE = """
            class MyClass {
                def myClosure = {
                }
            }
        """
        assert calculateForClosureField(SOURCE) == 1
    }

    void testCalculate_ProperCount_ClosureField() {
        final SOURCE = """
            class MyClass {
              def myClosure = {
                try {
                    if (ready) { }                      // +1
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
            }
        """
        assert calculateForClosureField(SOURCE) == 12
    }

    void testCalculate_ClosureFieldContainingNestedClosure() {
        final SOURCE = """
            class MyClass {
                def myClosure = {
                    def innerClosure1 = {
                        if (ready || paused) { }
                    }
                    def innerClosure2 = {
                        if (printing && !stopped) { }
                    }
                }
            }
        """
        assert calculateForClosureField(SOURCE) == 5
    }

}