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

/**
 * CodeNarc RuleSet for RunCodeNarcAgainstSourceCodeTest
 */

ruleset {

    ruleset('rulesets/basic.xml')
    ruleset('rulesets/braces.xml')
    ruleset('rulesets/exceptions.xml') {
        CatchThrowable {
            doNotApplyToClassNames = 'GMetricsTask'
        }
    }
    ruleset('rulesets/imports.xml')
    ruleset('rulesets/junit.xml')
    ruleset('rulesets/logging.xml') {
        Println {
            doNotApplyToClassNames = 'AbstractTestCase, ResultsNodeTestUtil'
        }
    }
    ruleset('rulesets/naming.xml') {
        VariableName {
            regex = /[a-z][a-zA-Z0-9_]*/
        }
    }
    ruleset('rulesets/size.xml') {
        // Re-enable once CodeNarc is updated to use new metricResult['total'] and metricResult['average'] syntax
        AbcComplexity(enabled:false)
        CyclomaticComplexity(enabled:false)
    }
    ruleset('rulesets/unused.xml')

}
