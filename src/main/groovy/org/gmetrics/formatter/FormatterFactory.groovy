/*
 * Copyright 2012 the original author or authors.
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
 package org.gmetrics.formatter

/**
 * Factory for Formatter objects
 *
 * @author Chris Mair
 */
class FormatterFactory {

    /**
     * Create and return a Formatter instance based on a specification String, which is the class name.
     * @param formatterSpecification - the specification of the Formatter. It is of the form:
     *              "fully-qualified-class-name"
     *              e.g., "org.gmetrics.formatter.PercentageFormatter"
     * @return a Formatter instance
     */
    Formatter getFormatter(String formatterSpecification) {
        assert formatterSpecification

        def formatterClass = getClass().classLoader.loadClass(formatterSpecification)
        return formatterClass.newInstance()
    }
}
