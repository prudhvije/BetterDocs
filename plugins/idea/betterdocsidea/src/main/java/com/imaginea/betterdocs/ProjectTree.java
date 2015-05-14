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

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;

public class ProjectTree {
    private static final String OPEN_IN_NEW_TAB = "Open in New Tab";
    private WindowObjects windowObjects = WindowObjects.getInstance();
    private WindowEditorOps windowEditorOps = new WindowEditorOps();
    private ESUtils esUtils = new ESUtils();
    private JSONUtils jsonUtils = new JSONUtils();
    private EditorDocOps editorDocOps = new EditorDocOps();
    private static final String GITHUB_LINK = "https://github.com/";
    private static final String RIGHT_CLICK_MENU_ITEM_TEXT = "Go to GitHub";

    public final TreeSelectionListener getTreeSelectionListener(final TreeNode root) {
        return new TreeSelectionListener() {
            @Override
            public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)
                        windowObjects.getjTree().getLastSelectedPathComponent();

                if (selectedNode != null && selectedNode.isLeaf() && root.getChildCount() > 0) {
                    final CodeInfo codeInfo = (CodeInfo) selectedNode.getUserObject();
                    final Document windowEditorDocument = windowObjects.getWindowEditor().
                                                                        getDocument();
                    String fileName = codeInfo.getFileName();
                    String fileContents;
                    Map<String, String> fileNameContentsMap = windowObjects
                                                                .getFileNameContentsMap();

                    if (!fileNameContentsMap.containsKey(fileName)) {
                        fileContents = esUtils.getContentsForFile(fileName);
                        fileNameContentsMap.put(fileName, fileContents);
                    } else {
                        fileContents = fileNameContentsMap.get(fileName);
                    }

                    codeInfo.setContents(fileContents);

                    windowEditorOps.writeToDocument(codeInfo, windowEditorDocument);

                    final List<Integer> linesForFolding = codeInfo.getLineNumbers();
                    linesForFolding.add(windowEditorDocument.getLineCount() + 1);
                    java.util.Collections.sort(linesForFolding);
                    windowEditorOps.addFoldings(windowEditorDocument, linesForFolding);
                }
            }
        };
    }

    public final void updateProjectNodes(final Collection<String> imports,
                                         final Map<String, String> fileTokensMap,
                                         final Map<String, ArrayList<CodeInfo>> projectNodes) {
        for (Map.Entry<String, String> entry : fileTokensMap.entrySet()) {
            String fileName = entry.getKey();
            String tokens = entry.getValue();
            List<Integer> lineNumbers;

            if (!windowObjects.getFileNameNumbersMap().containsKey(fileName)) {
                lineNumbers = jsonUtils.getLineNumbers(imports, tokens);
                windowObjects.getFileNameNumbersMap().put(fileName, lineNumbers);
            } else {
                lineNumbers = windowObjects.getFileNameNumbersMap().get(fileName);
            }
            CodeInfo codeInfo = new CodeInfo(fileName, lineNumbers);

            //Taking projectName as name till 2nd '/'
            int startIndex = fileName.indexOf('/');
            int endIndex = fileName.indexOf('/', startIndex + 1);

            String projectName = fileName.substring(0, endIndex);

            if (projectNodes.containsKey(projectName)) {
                projectNodes.get(projectName).add(codeInfo);
            } else {
                projectNodes.put(projectName,
                                    new ArrayList<CodeInfo>(Collections.singletonList(codeInfo)));
            }
        }
    }

    public final DefaultMutableTreeNode updateRoot(final DefaultMutableTreeNode root,
                                                   final  Map<String,
                                                           ArrayList<CodeInfo>> projectNodes) {
        for (Map.Entry<String, ArrayList<CodeInfo>> entry : projectNodes.entrySet()) {
            root.add(this.getNodes(entry.getKey(), entry.getValue()));
        }
        return root;
    }

    protected final MutableTreeNode getNodes(final String projectName,
                                             final Iterable<CodeInfo> codeInfoCollection) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(projectName);
        Collection<String> fileNameSet = new HashSet<String>();
        for (CodeInfo codeInfo : codeInfoCollection) {
            if (!fileNameSet.contains(codeInfo.getFileName())) {
                node.add(new DefaultMutableTreeNode(codeInfo));
                fileNameSet.add(codeInfo.getFileName());
            }
        }
        return node;
    }

    public final MouseListener getMouseListener(final TreeNode root) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent mouseEvent) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)
                        windowObjects.getjTree().getLastSelectedPathComponent();
                String url = "";
                if (mouseEvent.isMetaDown() && selectedNode != null
                        && selectedNode.getParent() != null) {
                    if (!selectedNode.isLeaf()) {
                        url = selectedNode.getUserObject().toString(); // getting project name
                    } else if (root.getChildCount() > 0) {
                        final CodeInfo codeInfo = (CodeInfo) selectedNode.getUserObject();
                        url = codeInfo.getFileName();
                    }
                    final String gitUrl = url;
                    JPopupMenu menu = new JPopupMenu();

                    if (selectedNode.isLeaf()) {
                        final CodeInfo codeInfo = (CodeInfo) selectedNode.getUserObject();

                        menu.add(new JMenuItem(new AbstractAction() {
                            @Override
                            public void actionPerformed(final ActionEvent actionEvent) {
                                VirtualFile virtualFile =
                                        editorDocOps.getVirtualFile(codeInfo.toString(),
                                                codeInfo.getContents());
                                FileEditorManager.getInstance(windowObjects.getProject()).
                                        openFile(virtualFile, true, true);
                                Document document =
                                        EditorFactory.getInstance().createDocument(codeInfo.getContents());
                                editorDocOps.addHighlighting(codeInfo.getLineNumbers(), document);
                                editorDocOps.gotoLine(codeInfo.getLineNumbers().get(0), document);
                            }
                        })).setText(OPEN_IN_NEW_TAB);
                    }

                    menu.add(new JMenuItem(new AbstractAction() {
                        @Override
                        public void actionPerformed(final ActionEvent actionEvent) {
                            if (!gitUrl.isEmpty()) {
                                BrowserUtil.browse(GITHUB_LINK + gitUrl);
                            }
                        }
                    })).setText(RIGHT_CLICK_MENU_ITEM_TEXT);

                    menu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        };
    }
}

class ToolTipTreeCellRenderer implements TreeCellRenderer {
    private static final String REPO_STARS = "Repo Stars: ";
    private WindowObjects windowObjects = WindowObjects.getInstance();
    private JSONUtils jsonUtils = new JSONUtils();
    private ESUtils esUtils = new ESUtils();
    private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

    public Component getTreeCellRendererComponent(final JTree tree, final Object value,
                                                  final boolean selected, final boolean expanded,
                                                  final boolean leaf, final int row,
                                                  final boolean hasFocus) {
        renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            if (value != null && value instanceof DefaultMutableTreeNode) {
                if (!((DefaultMutableTreeNode) value).isLeaf()
                        && !((DefaultMutableTreeNode) value).isRoot()) {
                    String repoName = ((DefaultMutableTreeNode) value).getUserObject().toString();
                    int repoId = windowObjects.getRepoNameIdMap().get(repoName);
                    String stars;
                    if (windowObjects.getRepoStarsMap().containsKey(repoName)) {
                        stars = windowObjects.getRepoStarsMap().get(repoName).toString();
                    } else {
                        String repoStarsJson = jsonUtils.getRepoStarsJSON(repoId);
                        stars = esUtils.getRepoStars(repoStarsJson);
                        windowObjects.getRepoStarsMap().put(repoName, stars);
                    }
                    renderer.setToolTipText(REPO_STARS + stars);
                } else {
                    renderer.setToolTipText(null);
                }
            }
        return renderer;
    }
}
