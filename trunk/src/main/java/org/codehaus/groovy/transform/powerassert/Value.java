/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.transform.powerassert;

/**
 * A value recorded during evaluation of an assertion, along with the column it
 * is associated with in the assertion's normalized source text.
 *
 * @author Peter Niederwieser
 */
public class Value {
    private final Object value;
    private final int column;

    public Value(Object value, int column) {
        this.value = value;
        this.column = column;
    }

    public Object getValue() {
        return value;
    }

    public int getColumn() {
        return column;
    }
}