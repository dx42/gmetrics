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
package org.gmetrics.test

import groovy.ant.AntBuilder
import org.junit.jupiter.api.Test

/**
 * Run CodeNarc against the project source code. Fail on configured rule violations
 *
 * @author Chris Mair
 */
class RunCodeNarcAgainstSourceCodeTest extends AbstractTestCase {

    private static final GROOVY_FILES = '**/*.groovy'
    private static final RULESET_FILES = 'codenarc/CodeNarcRuleSet.txt'

    @Test
	void testRunCodeNarc() {
        def ant = new AntBuilder()

        ant.taskdef(name:'codenarc', classname:'org.codenarc.ant.CodeNarcTask')

        ant.codenarc(ruleSetFiles:RULESET_FILES,
                maxPriority1Violations:0,
                maxPriority2Violations:0,
                maxPriority3Violations:0) {

           fileset(dir:'src/main/groovy') {
               include(name:GROOVY_FILES)
           }
           fileset(dir:'src/test/groovy') {
               include(name:GROOVY_FILES)
           }

           report(type:'ide')
        }
    }

}
