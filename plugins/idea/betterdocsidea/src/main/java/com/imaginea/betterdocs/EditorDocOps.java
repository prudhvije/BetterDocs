/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imaginea.betterdocs;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import java.util.HashSet;
import java.util.Set;

public class EditorDocOps {
    private static final String IMPORT = "import ";
    public static final char DOT = '.';

    public static Set<String> importsInLines(Iterable<String> lines, Iterable<String> imports) {
        Set<String> importsInLines = new HashSet<String>();

        for (String line : lines) {
            for (String nextImport : imports) {
                if (line.contains(nextImport.substring(nextImport.lastIndexOf(DOT) + 1))) {
                    importsInLines.add(nextImport);
                }
            }
        }
        return importsInLines;
    }

    public static Set<String> getLines(Editor projectEditor) {
        Set<String> lines = new HashSet<String>();
        Document document = projectEditor.getDocument();
        int startLine;
        int endLine;
        int distance = RefreshAction.distance;

        if (projectEditor.getSelectionModel().hasSelection()) {
            startLine = document.getLineNumber(projectEditor.getSelectionModel().getSelectionStart());
            endLine = document.getLineNumber(projectEditor.getSelectionModel().getSelectionEnd());
        } else {
            int currentLine = document.getLineNumber(projectEditor.getCaretModel().getOffset());
            startLine = currentLine - distance >= 0 ? currentLine - distance : 0;
            endLine = currentLine + distance <= document.getLineCount() - 1 ? currentLine + distance : document.getLineCount() - 1;
        }

        for (int i = startLine; i <= endLine; i++) {
            String line = document.getCharsSequence().subSequence(document.getLineStartOffset(i), document.getLineEndOffset(i)).toString();
            if (!line.contains(IMPORT)) {
                lines.add(line);
            }
        }
        return lines;
    }

    public static Set<String> getImports(Document document) {
        int startLine = 0;
        int endLine = document.getLineCount() - 1;
        Set<String> imports = new HashSet<String>();
        for (int i = startLine; i <= endLine; i++) {
            String line = document.getCharsSequence().subSequence(document.getLineStartOffset(i), document.getLineEndOffset(i) + document.getLineSeparatorLength(i)).toString();
            if (line.contains(IMPORT) && !line.contains("*")) {
                imports.add(line.replace(IMPORT, "").replace(";", "").trim());
            }
        }
        return imports;
    }
}
