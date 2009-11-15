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
package org.gmetrics.metric.linecount

import org.gmetrics.metric.AbstractMetricTest

/**
 * Tests for MethodLinesOfCodeMetric - calculate lines of code for closure fields
 *
 * @author Chris Mair
 * @version $Revision: 224 $ - $Date: 2009-09-22 22:04:03 -0400 (Tue, 22 Sep 2009) $
 */
class MethodLineCountMetric_ClosureFieldTest extends AbstractMetricTest {
    static metricClass = MethodLineCountMetric

    void testCalculate_CorrectSizeForSingleLineClosure() {
        final SOURCE = """
            class MyClass {
                def myClosure = { }
            }
        """
        assert calculateForClosureField(SOURCE) == 1
    }

    void testCalculate_CorrectSizeForMultiLineClosure() {
        final SOURCE = """
            class MyClass {
                def myClosure = {
                    println "started"

                    doSomethingElse()
                    return count
                }
            }
        """
        assert calculateForClosureField(SOURCE) == 6
    }

}