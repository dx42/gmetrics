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
package org.gmetrics.analyzer

/**
 * Integration tests for FilesystemSourceAnalyzer. These tests access the real filesystem
 * and use a real Metric implementation.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class FilesystemSourceAnalyzer_IntegrationTest extends AbstractSourceAnalyzer_IntegrationTest {

    protected SourceAnalyzer createSourceAnalyzer() {
        return new FilesystemSourceAnalyzer(baseDirectory:BASE_DIR)
    }

    protected void initializeSourceAnalyzerForEmptyDirectory() {
        analyzer.baseDirectory = BASE_DIR + '/empty'
    }

    protected void initializeSourceAnalyzerForDirectoryWithNoMatchingFiles() {
        analyzer.baseDirectory = BASE_DIR + '/no_matching_files'
    }
    
}