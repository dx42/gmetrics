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
 package org.gmetrics.metric.coverage

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.util.io.ResourceFactory
import org.gmetrics.util.io.Resource
import java.util.concurrent.atomic.AtomicInteger

/**
 * Tests for CoberturaCoverageFile
 *
 * @author Chris Mair
 */
class CoberturaCoverageFileTest extends AbstractTestCase {

    // Note: The CoberturaCoverageFile class is primarily tested through the
    // CoberturaLineCoverageMetric/CoberturaBranchCoverageMetric tests

    private static final XML_BYTES = '<xml/>'.bytes
    private static final RESOURCE = [getInputStream:{ return new ByteArrayInputStream(XML_BYTES) }] as Resource
    private static final COBERTURA_XML_FILE = 'cobertura.xml'

    void testGetCoberturaXml_IsReentrant() {
        def loadedCount = new AtomicInteger()
        def coberturaCoverageFile = new CoberturaCoverageFile(COBERTURA_XML_FILE, null)
        coberturaCoverageFile.resourceFactory = [
            getResource:{ path ->
                assert path == COBERTURA_XML_FILE
                loadedCount.incrementAndGet()
                return RESOURCE
            }] as ResourceFactory
        def threads = []
        10.times {
            threads << Thread.start { coberturaCoverageFile.getCoberturaXml() }
        }
        threads.each { thread -> thread.join() }
        assert loadedCount.get() == 1, "The Cobertura coverage file was loaded [${loadedCount.get()}] times"


    }
}
