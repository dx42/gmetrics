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

import java.util.ArrayList;
import java.util.List;

/**
 * Records values produced during evaluation of an assertion statement's truth
 * expression.
 *
 * @author Peter Niederwieser
 */
public class ValueRecorder {
    // used for code generation
    public static final String RECORD_METHOD_NAME = "record";
    // used for code generation
    public static final String CLEAR_METHOD_NAME = "clear";

    private final List<Value> values = new ArrayList<Value>();

    public void clear() {
        values.clear();
    }

    public Object record(Object value, int anchor) {
        values.add(new Value(value, anchor));
        return value;
    }

    public List<Value> getValues() {
        return values;
    }
}