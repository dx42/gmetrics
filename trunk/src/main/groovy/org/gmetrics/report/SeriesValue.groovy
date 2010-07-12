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

/**
 * Holder for a single data item within a series
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class SeriesValue {

    final String name
    final Object value

    SeriesValue(String name, Object value) {
        this.name = name
        this.value = value
    }

    String toString() {
        return "{$name:$value}"
    }
}
