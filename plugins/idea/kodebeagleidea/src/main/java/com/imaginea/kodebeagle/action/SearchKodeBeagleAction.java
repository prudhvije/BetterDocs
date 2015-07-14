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

package com.imaginea.kodebeagle.action;

import com.imaginea.kodebeagle.ui.MainWindow;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.ContentRootPanel;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.io.IOException;

public class SearchKodeBeagleAction extends AnAction {

    public static final String ACTIVATE_KODE_BEAGLE_TOOL_WINDOW = "ActivateKodeBeagleToolWindow";

    public void actionPerformed(final AnActionEvent e) {
        final ActionManager actionManager = ActionManager.getInstance();
        final AnAction showAction = actionManager.getAction(ACTIVATE_KODE_BEAGLE_TOOL_WINDOW);
        final ToolWindow toolWindow = new MainWindow().getToolWindow();

        actionManager.tryToExecute(showAction, e.getInputEvent(), null, e.getPlace(), false).createSetDoneRunnable().

        /*actionManager.tryToExecute(showAction, e.getInputEvent(), null, e.getPlace(), true).processOnDone(new Runnable() {
            @Override
            public void run() {

            }
        }, true);

        */

        /*try {
            new RefreshAction().init();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }*/
        /*if (toolWindow == null) {
            actionManager.tryToExecute(showAction, e.getInputEvent(), null, e.getPlace(), true);
        } else if (toolWindow.isVisible()) {
            try {
                new RefreshAction().init();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (!toolWindow.isVisible()) {
            toolWindow.show(new Runnable() {
                @Override
                public void run() {

                }
            });
        }*/
    }
}
