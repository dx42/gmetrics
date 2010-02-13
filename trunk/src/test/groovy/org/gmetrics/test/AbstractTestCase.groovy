/*
 * Copyright 2008 the original author or authors.
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

/**
 * Abstract superclass for tests 
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbstractTestCase extends GroovyTestCase {

    /**
     * Assert that the specified closure should throw an exception whose message contains text
     * @param text - the text expected within the message; may be a single String or a List of Strings
     * @param closure - the Closure to execute
     */
    protected void shouldFailWithMessageContaining(text, Closure closure) {
        def message = shouldFail(closure)
        log("exception message=[$message]")
        def strings = text instanceof List ? text : [text]
        strings.each { string ->
            assert message.contains(string), "[$message] does not contain [$string]"
        }
    }

    /**
     * Return true if the text contains each of the specified strings
     * @param text - the text to search
     * @param strings - the Strings to check for
     */
    protected boolean containsAll(String text, strings) {
        strings.every { text.contains(it) }
    }

    /**
     * Assert that the text contains each of the specified strings
     * @param text - the text to search
     * @param strings - the Strings that must be present within text 
     */
    protected void assertContainsAll(String text, strings) {
        assert containsAll(text, strings), "text does not contain [$string]"
    }

    /**
     * Assert that the text contains each of the specified strings, in order
     * @param text - the text to search
     * @param strings - the Strings that must be present within text, and appear
     *      in the order specified; toString() is applied to each.
     */
    protected void assertContainsAllInOrder(String text, strings) {
        def startIndex = 0
        strings.each { string ->
            def index = text.indexOf(string.toString(), startIndex)
            assert index > -1, "text does not contain [$string]"
            startIndex = index + 1
        }
    }

    /**
     * Assert that the two collections have equal Sets of elements. In other words, assert that
     * the two collections are the same, ignoring ordering and duplicates.
     */
    protected void assertEqualSets(Collection collection1, Collection collection2) {
        assert collection1 as Set == collection2 as Set
    }

    protected void assertBothAreFalseOrElseNeitherIs(def object1, def object2) {
        assert object1 ? object2 : !object2, object1 ? 
            "Expected both true: $object1 AND $object2" :
            "Expected both false: $object1 AND $object2"
    }

    /**
     * Write out the specified log message, prefixing with the current class name.
     * @param message - the message to log; toString() is applied first
     */
    protected void log(message) {
        println "[${classNameNoPackage()}] ${message.toString()}"
    }

    protected String captureSystemOut(Closure closure) {
        def originalSystemOut = System.out
        def out = new ByteArrayOutputStream()
        try {
            System.out = new PrintStream(out)
            closure()
        }
        finally {
            System.out = originalSystemOut
        }
        return out.toString()
    }
    
    protected BigDecimal scale(number, int scale=1) {
        return number.setScale(scale, BigDecimal.ROUND_HALF_UP)
    }

    /**
     * @return true if the version of Groovy currently running/compiling is NOT 1.5.x.
     */
    protected boolean isNotGroovy15() {
        def version = new org.codehaus.groovy.runtime.InvokerHelper().version
        return !version.startsWith('1.5')
    }

    private String classNameNoPackage() {
        def className = getClass().name
        def index = className.lastIndexOf('.')
        return index > -1 ? className.substring(index+1) : className
    }

    //------------------------------------------------------------------------------------
    // Test Setup and Tear Down
    //------------------------------------------------------------------------------------

    void setUp() {
        println "-------------------------[ ${classNameNoPackage()}.${getName()} ]------------------------"
        super.setUp()
    }

}