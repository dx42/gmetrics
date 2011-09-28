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
package org.gmetrics.source

import org.codehaus.groovy.ast.ModuleNode

/**
 * Represents a unit of source code to be analyzed
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
interface SourceCode {

    /**
     * Get the logical name for this source code. If this object is a file, then the name
     * is the filename, without a path.
     * @return the name for this source; may be null
     */
    String getName()

    /**
     * Get the logical path for this source code. If this object is a file, then the name
     * is the full path in the filesystem. File separators are normalized to forward slash (/).
     * @return the name for this source; may be null
     */
    String getPath()

    /**
     * @return the full text of the source code
     */
    String getText()

    /**
     * @return the List of lines of the source code (with line terminators removed)
     */
    List getLines()

    /**
     * Get the trimmed line at the specified index
     * @param lineNumber - the line number; may be negative
     * @return the trimmed line at the specified index, or null if lineNumber is not valid
     */
    String line(int lineNumber)

    /**
     * Return the Groovy AST (Abstract Syntax Tree) for this source file
     * @return the ModuleNode representing the AST for this source file
     */
    ModuleNode getAst()
    
    /**
     * Return true if and only if the source code can be successfully compiled
     * @return true only if the source code is valid
     */
    boolean isValid()

    /**
     * Return the line index for the line containing the character at the specified index within the source code.
     * @param charIndex - the index of the character within the source code (zero-based)
     * @return the line number (one-based) containing the specified character; Return -1 if charIndex is not valid.
     */
    int getLineNumberForCharacterIndex(int charIndex)
    
}