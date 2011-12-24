/*
 * Copyright 2011 the original author or authors.
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
package org.gmetrics.metric.coverage

/**
 * Metric for test code coverage by branch (branch-rate) from a Cobertura XML file.
 *
 * @author Chris Mair
 */
class CoberturaBranchCoverageMetric extends AbstractCoberturaCoverageMetric {

    final String name = 'CoberturaBranchCoverage'
    final String attributeName = 'branch-rate'

    @Override
    protected Ratio getCoverageRatioForSingleClass(matchingClassElement) {
        if (matchingClassElement.isEmpty()) {
            return null
        }
        def methodLines = findLineElementsWithBranches(matchingClassElement.methods.method.lines.line)
        def standaloneLines = findLineElementsWithBranches(matchingClassElement.lines.line)

        return getBranchCoverageRatio(methodLines) + getBranchCoverageRatio(standaloneLines)
    }

    private findLineElementsWithBranches(lines) {
        lines.findAll { line -> line.@branch == 'true' }
    }

    private Ratio getBranchCoverageRatio(linesElements) {
        final REGEX = /.*\((\d+)\/(\d+)\)/
        def ratio = Ratio.ZERO
        linesElements.each { line ->
            def conditionCoverageString = line.@'condition-coverage'
            def m = conditionCoverageString =~ REGEX
            assert m, "$conditionCoverageString does not match $REGEX"
            def covered = m[0][1] as int
            def total = m[0][2] as int
            ratio += new Ratio(covered, total)
        }
        return ratio
    }

}