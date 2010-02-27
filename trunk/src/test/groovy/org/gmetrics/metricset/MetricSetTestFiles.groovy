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
package org.gmetrics.metricset

/**
 * Constants for MetricSet test files
 *
 * @author Chris Mair
 * @version $Revision: 24 $ - $Date: 2009-12-10 21:17:05 -0500 (Thu, 10 Dec 2009) $
 */
class MetricSetTestFiles {

    // groovy files are not on classpath; have to use *.txt
    public static final METRICSET1 = 'metricsets/GroovyMetricSet1.txt'
    public static final METRICSET2 = 'metricsets/GroovyMetricSet2.txt'
    public static final METRICSET3 = 'metricsets/GroovyMetricSet3.txt'
    public static final METRICSET1_RELATIVE_PATH = 'file:src/test/resources/' + METRICSET1

}
