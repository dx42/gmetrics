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

/**
 * Tests for LevelsCriteriaFilter
 *
 * @author Chris Mair
 * @version $Revision: 107 $ - $Date: 2010-06-05 07:23:27 -0400 (Sat, 05 Jun 2010) $
 */

class LevelsCriteriaFilterTest extends AbstractTestCase {

    private levelsCriteriaFilter = new LevelsCriteriaFilter()

    void testNoLevelsDefined_IncludesLevel_ReturnsTrue() {
        assert levelsCriteriaFilter.includesLevel(MetricLevel.CLASS)
        assert levelsCriteriaFilter.includesLevel(MetricLevel.PACKAGE)
    }

    void testOneLevelDefined_IncludesLevel_ReturnsTrueForThat_AndFalseForOthers() {
        levelsCriteriaFilter.setLevels('package')
        assert !levelsCriteriaFilter.includesLevel(MetricLevel.CLASS)
        assert levelsCriteriaFilter.includesLevel(MetricLevel.PACKAGE)
    }

    void testMultipleLevelsDefined_IncludesLevel_ReturnsTrueForMatching_AndFalseForOthers() {
        levelsCriteriaFilter.setLevels(' method ,777,class')
        assert levelsCriteriaFilter.includesLevel(MetricLevel.METHOD)
        assert levelsCriteriaFilter.includesLevel(MetricLevel.CLASS)
        assert !levelsCriteriaFilter.includesLevel(MetricLevel.PACKAGE)
    }

    void testMultipleLevelsDefined_IncludesLevel_IsCaseInsensitive() {
        levelsCriteriaFilter.setLevels(' meTHod,PACKAGE    ,  class,777')
        assert levelsCriteriaFilter.includesLevel(MetricLevel.METHOD)
        assert levelsCriteriaFilter.includesLevel(MetricLevel.CLASS)
        assert levelsCriteriaFilter.includesLevel(MetricLevel.PACKAGE)
    }
}