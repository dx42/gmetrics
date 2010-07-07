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

import org.gmetrics.metric.MetricLevel
import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.StubMetric

/**
 * Tests for LevelsCriteriaFilter
 *
 * @author Chris Mair
 * @version $Revision: 107 $ - $Date: 2010-06-05 07:23:27 -0400 (Sat, 05 Jun 2010) $
 */

class LevelsCriteriaFilterTest extends AbstractTestCase {

    private static final METRIC_ABC = new StubMetric(name:'ABC')
    private static final METRIC_XXX = new StubMetric(name:'XXX')
    private static final METRIC_123 = new StubMetric(name:'123')

    private levelsCriteriaFilter = new LevelsCriteriaFilter()

    void testNoLevelsDefined_IncludesLevel_ReturnsTrue() {
        assert levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.CLASS)
        assert levelsCriteriaFilter.includesLevel(METRIC_XXX, MetricLevel.PACKAGE)
    }

    void testOneMetric_OneLevelDefined_IncludesLevel_ReturnsTrueForThat_AndFalseForOthers() {
        levelsCriteriaFilter.setLevels('ABC=package')
        assert levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.PACKAGE)
        assert !levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.CLASS)
        assert !levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.METHOD)

        assert levelsCriteriaFilter.includesLevel(METRIC_XXX, MetricLevel.CLASS)
    }

    void testOneMetric_SingleLevelDefined_IncludesLevel_ReturnsTrueForMatching_AndFalseForOthers() {
        levelsCriteriaFilter.setLevels('ABC=package,method')
        assert levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.PACKAGE)
        assert !levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.CLASS)
        assert levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.METHOD)

        assert levelsCriteriaFilter.includesLevel(METRIC_XXX, MetricLevel.CLASS)
    }

    void testMultipleMetrics_MultipleLevelsDefined_IncludesLevel_ReturnsTrueForMatching_AndFalseForOthers() {
        levelsCriteriaFilter.setLevels('ABC=class,method; XXX=class,package; ZZZ=method')
        assert !levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.PACKAGE)
        assert levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.CLASS)
        assert levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.METHOD)

        assert levelsCriteriaFilter.includesLevel(METRIC_XXX, MetricLevel.PACKAGE)
        assert levelsCriteriaFilter.includesLevel(METRIC_XXX, MetricLevel.CLASS)
        assert !levelsCriteriaFilter.includesLevel(METRIC_XXX, MetricLevel.METHOD)

        assert levelsCriteriaFilter.includesLevel(METRIC_123, MetricLevel.METHOD)
    }

    void testMultipleLevelsDefined_IncludesLevel_IsCaseInsensitive() {
        levelsCriteriaFilter.setLevels('ABC=pACKaGe,METHOD;   XXX = Class ')
        assert levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.PACKAGE)
        assert !levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.CLASS)
        assert levelsCriteriaFilter.includesLevel(METRIC_ABC, MetricLevel.METHOD)

        assert levelsCriteriaFilter.includesLevel(METRIC_XXX, MetricLevel.CLASS)
        assert !levelsCriteriaFilter.includesLevel(METRIC_XXX, MetricLevel.METHOD)
    }

    void testInvalidCriteriaString_ThrowsException() {
        shouldFailWithMessageContaining('criteria') { levelsCriteriaFilter.setLevels('%#') }
        shouldFailWithMessageContaining('criteria') { levelsCriteriaFilter.setLevels('ABC') }
        shouldFailWithMessageContaining('criteria') { levelsCriteriaFilter.setLevels('ABC:123') }
    }

    void testSetLevels_NullOrEmptyCriteriaString_ThrowsException() {
        shouldFailWithMessageContaining('criteria') { levelsCriteriaFilter.setLevels(null) }
        shouldFailWithMessageContaining('criteria') { levelsCriteriaFilter.setLevels('') }
    }

}