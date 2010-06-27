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
package org.gmetrics.report

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.StubMetric

/**
 * Tests for FunctionsCriteriaFilter
 *
 * @author Chris Mair
 * @version $Revision: 107 $ - $Date: 2010-06-05 07:23:27 -0400 (Sat, 05 Jun 2010) $
 */

class FunctionsCriteriaFilterTest extends AbstractTestCase {

    private static final METRIC_ABC = new StubMetric(name:'ABC')
    private static final METRIC_XXX = new StubMetric(name:'XXX')
    private static final METRIC_123 = new StubMetric(name:'123')

    private functionsCriteriaFilter = new FunctionsCriteriaFilter()

//    void testNoFunctionsDefined_IncludesFunction_ReturnsTrue() {
//        assert functionsCriteriaFilter.includesFunction('xyz')
//        assert functionsCriteriaFilter.includesFunction('average')
//    }
//
//    void testOneFunctionDefined_IncludesFunction_ReturnsTrueForThat_AndFalseForOthers() {
//        functionsCriteriaFilter.setFunctions('average')
//        assert !functionsCriteriaFilter.includesFunction('total')
//        assert functionsCriteriaFilter.includesFunction('average')
//    }
//
//    void testMultipleFunctionsDefined_IncludesFunction_ReturnsTrueForMatching_AndFalseForOthers() {
//        functionsCriteriaFilter.setFunctions(' minimum ,777,average')
//        assert !functionsCriteriaFilter.includesFunction('total')
//        assert functionsCriteriaFilter.includesFunction('average')
//        assert functionsCriteriaFilter.includesFunction('minimum')
//    }
//
//    void testMultipleFunctionsDefined_IncludesFunction_IsCaseInsensitive() {
//        functionsCriteriaFilter.setFunctions(' mINIMum ,777,average,TOTAL  ')
//        assert functionsCriteriaFilter.includesFunction('total')
//        assert functionsCriteriaFilter.includesFunction('average')
//        assert functionsCriteriaFilter.includesFunction('minimum')
//        assert !functionsCriteriaFilter.includesFunction('maximum')
//    }

    void testNoFunctionsDefined_IncludesFunction_ReturnsTrue() {
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'average')
        assert functionsCriteriaFilter.includesFunction(METRIC_XXX, 'total')
    }

    void testOneMetric_OneLevelDefined_IncludesFunction_ReturnsTrueForThat_AndFalseForOthers() {
        functionsCriteriaFilter.setFunctions('ABC=average')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'average')
        assert !functionsCriteriaFilter.includesFunction(METRIC_ABC, 'total')
        assert !functionsCriteriaFilter.includesFunction(METRIC_ABC, 'minimum')

        assert !functionsCriteriaFilter.includesFunction(METRIC_XXX, 'average')
    }

    void testOneMetric_SingleLevelDefined_IncludesFunction_ReturnsTrueForMatching_AndFalseForOthers() {
        functionsCriteriaFilter.setFunctions('ABC=total,minimum')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'total')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'minimum')
        assert !functionsCriteriaFilter.includesFunction(METRIC_ABC, 'average')

        assert !functionsCriteriaFilter.includesFunction(METRIC_XXX, 'average')
    }

    void testMultipleMetrics_MultipleLevelsDefined_IncludesFunction_ReturnsTrueForMatching_AndFalseForOthers() {
        functionsCriteriaFilter.setFunctions('ABC=minimum,total; XXX=average,maximum; ZZZ=average')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'minimum')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'total')
        assert !functionsCriteriaFilter.includesFunction(METRIC_ABC, 'average')

        assert functionsCriteriaFilter.includesFunction(METRIC_XXX, 'average')
        assert functionsCriteriaFilter.includesFunction(METRIC_XXX, 'maximum')
        assert !functionsCriteriaFilter.includesFunction(METRIC_XXX, 'total')

        assert !functionsCriteriaFilter.includesFunction(METRIC_123, 'average')
    }

    void testMultipleLevelsDefined_IncludesFunction_IsCaseInsensitive() {
        functionsCriteriaFilter.setFunctions('ABC=avERAGe,TOTAL;   XXX = Minimum')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'average')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'total')
        assert !functionsCriteriaFilter.includesFunction(METRIC_ABC, 'minimum')

        assert functionsCriteriaFilter.includesFunction(METRIC_XXX, 'minimum')
        assert !functionsCriteriaFilter.includesFunction(METRIC_XXX, 'total')
    }

    void testInvalidCriteriaString_ThrowsException() {
        shouldFailWithMessageContaining('criteria') { functionsCriteriaFilter.setFunctions('%#') }
        shouldFailWithMessageContaining('criteria') { functionsCriteriaFilter.setFunctions('ABC') }
        shouldFailWithMessageContaining('criteria') { functionsCriteriaFilter.setFunctions('ABC:123') }
    }

    void testSetFunctions_NullOrEmptyCriteriaString_ThrowsException() {
        shouldFailWithMessageContaining('criteria') { functionsCriteriaFilter.setFunctions(null) }
        shouldFailWithMessageContaining('criteria') { functionsCriteriaFilter.setFunctions('') }
    }

}