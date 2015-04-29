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

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.jetbrains.annotations.NotNull;

public class RefreshAction extends AnAction {
    private static final String BETTER_DOCS = "BetterDocs";
    private static final String ILLEGAL_FORMAT = "Please Provide valid numbers for distance and size in settings. Using default values for now";
    private static final String INFO = "Info";
    public static final String EMPTY_ES_URL = "Please set/modify proper esURL in idea settings";
    public static final String ES_URL = "esURL";
    public static final String DISTANCE = "distance";
    public static final String SIZE = "size";
    private static final String BETTERDOCS_SEARCH = "/betterdocs/_search?source=";
    public static final String ES_URL_DEFAULT = "http://labs.imaginea.com/betterdocs";
    public static final int DISTANCE_DEFAULT_VALUE = 10;
    public static final int SIZE_DEFAULT_VALUE = 30;

    static Project project;
    static JTree jTree;
    static Editor windowEditor;

    static int distance;
    static int size;
    static String esURL;

    public void setWindowEditor(Editor windowEditor) {
        this.windowEditor = windowEditor;
    }

    public void setTree(JTree jTree) {
        this.jTree = jTree;
    }

    public RefreshAction() {
        super(BETTER_DOCS, BETTER_DOCS, AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        setProject(anActionEvent.getProject());
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        try {
            this.distance = propertiesComponent.getOrInitInt(DISTANCE, DISTANCE_DEFAULT_VALUE);
            this.size = propertiesComponent.getOrInitInt(SIZE, SIZE_DEFAULT_VALUE);
            this.esURL = propertiesComponent.getValue(ES_URL, ES_URL_DEFAULT);
        } catch (NumberFormatException ne) {
            this.distance = DISTANCE_DEFAULT_VALUE;
            this.size = SIZE_DEFAULT_VALUE;
            Messages.showInfoMessage(String.format(ILLEGAL_FORMAT), INFO);
        }

        try {
            runAction(anActionEvent);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void runAction(final AnActionEvent e) throws IOException {
        final Editor projectEditor = DataKeys.EDITOR.getData(e.getDataContext());

        if (projectEditor != null) {

            Set<String> imports = EditorDocOps.getImports(projectEditor.getDocument());
            Set<String> lines = EditorDocOps.getLines(projectEditor);
            Set<String> importsInLines = EditorDocOps.importsInLines(lines, imports);

            DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            root.removeAllChildren();

            if (!importsInLines.isEmpty()) {
                jTree.setVisible(true);
                String esQueryJson = JSONUtils.getESQueryJson(importsInLines);
                String esResultJson = ESUtils.getESResultJson(esQueryJson, esURL + BETTERDOCS_SEARCH);

                if (!esResultJson.equals(EMPTY_ES_URL)) {
                    Map<String, String> fileTokensMap = ESUtils.getFileTokens(esResultJson);
                    Map<String, ArrayList<CodeInfo>> projectNodes = new HashMap<String, ArrayList<CodeInfo>>();

                    ProjectTree.updateProjectNodes(imports, fileTokensMap, projectNodes);
                    ProjectTree.updateRoot(root, projectNodes);

                    model.reload(root);
                    jTree.addTreeSelectionListener(ProjectTree.getTreeSelectionListener(root));
                } else {
                    Messages.showInfoMessage(EMPTY_ES_URL, INFO);
                }
            } else {
                jTree.updateUI();
            }
        }
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
