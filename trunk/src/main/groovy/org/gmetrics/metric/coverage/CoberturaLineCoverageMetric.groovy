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
 * Metric for test code coverage by line (line-rate) from a Cobertura XML file.
 *
 * @see "http://cobertura.sourceforge.net/"
 *
 * @author Chris Mair
 */
class CoberturaLineCoverageMetric extends AbstractCoberturaCoverageMetric {

    final String name = 'CoberturaLineCoverage'
    final String attributeName = 'line-rate'

    @Override
    protected Ratio getCoverageRatioForSingleClass(matchingClassElement) {
        if (matchingClassElement.isEmpty()) {
            return null
        }
        def methodLines = matchingClassElement.methods.method.lines.line
        def standaloneLines = matchingClassElement.lines.line

        return getLinesCoverageRatio(methodLines) + getLinesCoverageRatio(standaloneLines)
    }

    private Ratio getLinesCoverageRatio(linesElements) {
        def numLines = linesElements.size()
        def numLinesCovered = linesElements.findAll { line -> line.@hits != '0' }.size()
        return new Ratio(numLinesCovered, numLines)
    }

}