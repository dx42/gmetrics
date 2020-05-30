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

import org.gmetrics.test.AbstractTestCase
import org.junit.Assert
import org.junit.Test

/**
 * Tests for AbcVector
 *
 * @author Chris Mair
 */
class AbcVectorTest extends AbstractTestCase {

    @Test
	void testPassingNegativeAssignmentsIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('assignments') { new AbcVector(-1, 0, 0) } 
    }

    @Test
	void testPassingNegativeBranchesIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('branches') { new AbcVector(0, -1, 0) } 
    }

    @Test
	void testPassingNegativeConditionsIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('conditions') { new AbcVector(0, 0, -1) }
    }

    @Test
	void testValueForEmptyVectorSetIsZero() {
        assert abcVectorMagnitude(0, 0, 0) == 0
    }

    @Test
	void testVectorWithIntegerResultValue() {
        assert abcVectorMagnitude(1, 2, 2) == 3
    }

    @Test
	void testVectorWithNonIntegerResultValue() {
        assert abcVectorMagnitude(7, 1, 2) == 7.3
    }

    @Test
	void testVectorWithOnlyAssignmentValueIsThatValue() {
        assert abcVectorMagnitude(7, 0, 0) == 7
    }

    @Test
	void testVectorWithOnlyBranchValueIsThatValue() {
        assert abcVectorMagnitude(0, 7, 0) == 7
    }

    @Test
	void testVectorWithOnlyConditionalValueIsThatValue() {
        assert abcVectorMagnitude(0, 0, 7) == 7
    }

    @Test
	void testVectorWithHugeNumbers() {
        Assert.assertEquals(46589.5, abcVectorMagnitude(8408, 45703, 3335), 0.01)
    }


    private abcVectorMagnitude(int a, int b, int c) {
        def abcVector = new AbcVector(a, b, c)
        log(abcVector)
        return abcVector.getMagnitude()
    }
    
}