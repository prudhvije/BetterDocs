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

package com.imaginea.kodebeagle.ui;

import com.imaginea.kodebeagle.util.WindowEditorOps;
import com.imaginea.kodebeagle.action.CollapseProjectTreeAction;
import com.imaginea.kodebeagle.action.EditSettingsAction;
import com.imaginea.kodebeagle.action.ExpandProjectTreeAction;
import com.imaginea.kodebeagle.action.RefreshAction;
import com.imaginea.kodebeagle.object.WindowObjects;

import com.intellij.ide.actions.ActivateToolWindowAction;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.treeStructure.Tree;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;

public class MainWindow implements ToolWindowFactory {

    private static final String PROJECTS = "Projects";
    public static final String JAVA = "java";
    private static final double DIVIDER_LOCATION = 0.8;
    private static final String ALL_TAB = "All";
    private static final String FEATURED_TAB = "Top";
    private static final int EDITOR_SCROLL_PANE_WIDTH = 200;
    private static final int EDITOR_SCROLL_PANE_HEIGHT = 300;
    private static final String KODEBEAGLE = "KodeBeagle";
    private static final String IDEA_PLUGIN = "Idea-Plugin";
    private static final String PLUGIN_ID = "kodebeagleidea";
    private static final String OS_NAME = "os.name";
    private static final String OS_VERSION = "os.version";
    private static final int UNIT_INCREMENT = 16;
    private static final String ALT = "alt ";
    private static final int NUM_KEY = 8;
    private WindowEditorOps windowEditorOps = new WindowEditorOps();
    private WindowObjects windowObjects = WindowObjects.getInstance();
    private PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

    @Override
    public final void createToolWindowContent(final Project project, final ToolWindow toolWindow) {

        initSystemInfo();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(PROJECTS);

        JTree jTree = new Tree(root);
        jTree.setRootVisible(false);
        jTree.setAutoscrolls(true);

        if (!propertiesComponent.isValueSet(SettingsPanel.BEAGLE_ID)) {
            windowObjects.setBeagleId(UUID.randomUUID().toString());
            propertiesComponent.setValue(SettingsPanel.BEAGLE_ID, windowObjects.getBeagleId());
        } else {
            windowObjects.setBeagleId(propertiesComponent.getValue(SettingsPanel.BEAGLE_ID));
        }

        Document document = EditorFactory.getInstance().createDocument("");
        Editor windowEditor = EditorFactory.getInstance().
                createEditor(document, project, FileTypeManager.getInstance().
                        getFileTypeByExtension(JAVA), false);
        //Dispose the editor once it's no longer needed
        windowEditorOps.releaseEditor(project, windowEditor);

        final RefreshAction refreshAction = new RefreshAction();

        Keymap keymap = KeymapManager.getInstance().getActiveKeymap();
        if (keymap != null) {
            KeyboardShortcut defShortcut =
                    new KeyboardShortcut(KeyStroke.getKeyStroke(ALT + NUM_KEY), null);
            String actionId = ActivateToolWindowAction.getActionIdForToolWindow(KODEBEAGLE);
            keymap.addShortcut(actionId, defShortcut);
        }

        EditSettingsAction editSettingsAction = new EditSettingsAction();
        ExpandProjectTreeAction expandProjectTreeAction = new ExpandProjectTreeAction();
        CollapseProjectTreeAction collapseProjectTreeAction = new CollapseProjectTreeAction();

        windowObjects.setTree(jTree);
        windowObjects.setWindowEditor(windowEditor);

        DefaultActionGroup group = new DefaultActionGroup();
        group.add(refreshAction);
        group.addSeparator();
        group.add(expandProjectTreeAction);
        group.add(collapseProjectTreeAction);
        group.addSeparator();
        group.add(editSettingsAction);
        final JComponent toolBar = ActionManager.getInstance().
                createActionToolbar(KODEBEAGLE, group, true).
                getComponent();
        toolBar.setBorder(BorderFactory.createCompoundBorder());

        toolBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, toolBar.getMinimumSize().height));
        toolBar.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if (toolBar.isShowing()) {
                    try {
                        refreshAction.init();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        });



        JBScrollPane jTreeScrollPane = new JBScrollPane();
        jTreeScrollPane.getViewport().setBackground(JBColor.white);
        jTreeScrollPane.setAutoscrolls(true);
        jTreeScrollPane.setBackground(JBColor.white);
        windowObjects.setJTreeScrollPane(jTreeScrollPane);


        final JSplitPane jSplitPane = new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT, jTreeScrollPane, windowEditor.getComponent());
        jSplitPane.setResizeWeight(DIVIDER_LOCATION);

        JPanel editorPanel = new JPanel();
        editorPanel.setOpaque(true);
        editorPanel.setBackground(JBColor.white);
        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));

        final JBScrollPane editorScrollPane = new JBScrollPane();
        editorScrollPane.getViewport().setBackground(JBColor.white);
        editorScrollPane.setViewportView(editorPanel);
        editorScrollPane.setAutoscrolls(true);
        editorScrollPane.setPreferredSize(new Dimension(EDITOR_SCROLL_PANE_WIDTH,
                EDITOR_SCROLL_PANE_HEIGHT));
        editorScrollPane.getVerticalScrollBar().setUnitIncrement(UNIT_INCREMENT);
        editorScrollPane.setHorizontalScrollBarPolicy(JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        windowObjects.setPanel(editorPanel);

        final JTabbedPane jTabbedPane = new JBTabbedPane();
        jTabbedPane.add(FEATURED_TAB, editorScrollPane);
        jTabbedPane.add(ALL_TAB, jSplitPane);
        refreshAction.setJTabbedPane(jTabbedPane);
        // Display initial help information here.
        refreshAction.showHelpInfo(RefreshAction.HELP_MESSAGE);
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout((new BoxLayout(mainPanel, BoxLayout.Y_AXIS)));
        mainPanel.add(toolBar);
        mainPanel.add(jTabbedPane);

        if (!LegalNotice.isLegalNoticeAccepted()) {
            new LegalNotice(project).showLegalNotice();
        }

        toolWindow.getComponent().getParent().add(mainPanel);
    }

    private void initSystemInfo() {
        windowObjects.setOsInfo(System.getProperty(OS_NAME) + "/"
                + System.getProperty(OS_VERSION));
        windowObjects.setApplicationVersion(ApplicationInfo.getInstance().getVersionName()
                + "/" + ApplicationInfo.getInstance().getBuild().toString());
        IdeaPluginDescriptor codeBeagleVersion =
                            PluginManager.getPlugin(PluginId.getId(PLUGIN_ID));

        if (codeBeagleVersion != null) {
            windowObjects.setPluginVersion(IDEA_PLUGIN + "/" + codeBeagleVersion.getVersion());
        }
    }
}
