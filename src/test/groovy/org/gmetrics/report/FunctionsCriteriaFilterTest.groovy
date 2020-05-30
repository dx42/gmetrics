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

import org.gmetrics.metric.StubMetric
import org.gmetrics.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for FunctionsCriteriaFilter
 *
 * @author Chris Mair
 */

class FunctionsCriteriaFilterTest extends AbstractTestCase {

    private static final StubMetric METRIC_ABC = new StubMetric(name:'ABC')
    private static final StubMetric METRIC_XXX = new StubMetric(name:'XXX')
    private static final StubMetric METRIC_123 = new StubMetric(name:'123')

    private FunctionsCriteriaFilter functionsCriteriaFilter = new FunctionsCriteriaFilter()

    @Test
	void testNoFunctionsDefined_IncludesFunction_ReturnsTrue() {
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'average')
        assert functionsCriteriaFilter.includesFunction(METRIC_XXX, 'total')
    }

    @Test
	void testOneMetric_OneLevelDefined_IncludesFunction_ReturnsTrueForThat_AndFalseForOthers() {
        functionsCriteriaFilter.setFunctions('ABC=average')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'average')
        assert !functionsCriteriaFilter.includesFunction(METRIC_ABC, 'total')
        assert !functionsCriteriaFilter.includesFunction(METRIC_ABC, 'minimum')

        assert functionsCriteriaFilter.includesFunction(METRIC_XXX, 'average')
    }

    @Test
	void testOneMetric_SingleLevelDefined_IncludesFunction_ReturnsTrueForMatching_AndFalseForOthers() {
        functionsCriteriaFilter.setFunctions('ABC=total,minimum')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'total')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'minimum')
        assert !functionsCriteriaFilter.includesFunction(METRIC_ABC, 'average')

        assert functionsCriteriaFilter.includesFunction(METRIC_XXX, 'average')
    }

    @Test
	void testMultipleMetrics_MultipleLevelsDefined_IncludesFunction_ReturnsTrueForMatching_AndFalseForOthers() {
        functionsCriteriaFilter.setFunctions('ABC=minimum,total; XXX=average,maximum; ZZZ=average')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'minimum')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'total')
        assert !functionsCriteriaFilter.includesFunction(METRIC_ABC, 'average')

        assert functionsCriteriaFilter.includesFunction(METRIC_XXX, 'average')
        assert functionsCriteriaFilter.includesFunction(METRIC_XXX, 'maximum')
        assert !functionsCriteriaFilter.includesFunction(METRIC_XXX, 'total')

        assert functionsCriteriaFilter.includesFunction(METRIC_123, 'average')
    }

    @Test
	void testMultipleLevelsDefined_IncludesFunction_IsCaseInsensitive() {
        functionsCriteriaFilter.setFunctions('ABC=avERAGe,TOTAL;   XXX = Minimum')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'average')
        assert functionsCriteriaFilter.includesFunction(METRIC_ABC, 'total')
        assert !functionsCriteriaFilter.includesFunction(METRIC_ABC, 'minimum')

        assert functionsCriteriaFilter.includesFunction(METRIC_XXX, 'minimum')
        assert !functionsCriteriaFilter.includesFunction(METRIC_XXX, 'total')
    }

    @Test
	void testInvalidCriteriaString_ThrowsException() {
        shouldFailWithMessageContaining('criteria') { functionsCriteriaFilter.setFunctions('%#') }
        shouldFailWithMessageContaining('criteria') { functionsCriteriaFilter.setFunctions('ABC') }
        shouldFailWithMessageContaining('criteria') { functionsCriteriaFilter.setFunctions('ABC:123') }
    }

    @Test
	void testSetFunctions_NullOrEmptyCriteriaString_ThrowsException() {
        shouldFailWithMessageContaining('criteria') { functionsCriteriaFilter.setFunctions(null) }
        shouldFailWithMessageContaining('criteria') { functionsCriteriaFilter.setFunctions('') }
    }

}