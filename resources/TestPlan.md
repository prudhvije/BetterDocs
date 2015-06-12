#### 1. [idea] Show initial message centered.
1. When plugin opened initially help message in all pane should be at center.
<img src='screenshots/centered_help_info.png'/> <br>
<img src='screenshots/es_no_results_info.png'/> <br>
<img src='screenshots/only_java.png'/> <br>

#### 2. [idea] Introduced async fetching of results from elastic search server.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/131'> #131 </a>
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/127'> #127 </a>
1. While fetching results IDE should not hang.

#### 3. [ideaplugin] show help info should autmatically bring the focus to main pane.
##### Display the content centered.
1. When help message is displayed all pane should get focus and help content should be in 			center.

#### 4. [ideaplugin] #141 Plugin throwing AIOBE has been fixed.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/141'> #141 </a>
1. Add this line 'import ' in your code and hit refresh buttion.
2. NullPointerException will not be thrown.

#### 5. [ideaplugin] fixed the white background if highlighted code in new tab in main editor  is selected
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/117'> #117 </a>
1. When results in all pane/featured pane opened as new tab in IDE, selecting highlighted code 
	should be visible.
<img src='screenshots/117_1.png'/> <br>
<img src='screenshots/117_2.png'/> <br>

#### 6. [ideaplugin] added git icon to tree nodes and goto github right click menu item
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/129'> #129 </a>
1. All Pane project tree non leaf nodes should have git icon.
2. All Pane project tree nodes right click "Go to Github" should have git icon.
<img src='screenshots/129_1.png'/> <br>
<img src='screenshots/129_2.png'/> <br>

#### 7. [ideaplugin]fixed editor window scroll
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/124'> #124 </a>
1. All Pane preview editor should be scrollable.<br>
2. <img src='screenshots/124.png'/>

#### 8. [ideaplugin] removed foldings from main pane editor
##### modified wrong esURL message
##### disposed tiny editors in code pane after usage
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/112'> #112 </a>
1. All Pane preview editor should not have code foldings
2. No 'Editor has not been released..' Exception after closing the plugin.<br>
<img src='screenshots/109_2.png'/> 

#### 9. [ideaplugin] Right click and open files in a tab. + Expand to full source in a tab.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/56'> #56 </a>
##### made files in editor readonly
##### added for jtree selection update preview pane, rightclick open in new tab
1. Right clicking project tree leaf nodes should have menu item as "Open in new tab" and clicking that should open file in new tab in IDE.
2. Opened file should be non editable.
3. Selecting leaf node in project tree should load it's preview in all pane preview editor.<br>
<img src='screenshots/109_1.png'/> <br>
<img src='screenshots/109_3.png'/> <br>
<img src='screenshots/109_2.png'/> <br>

#### 10. [ideaplugin] Ignored case while searching for import names in the selected text region.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/114'> #114 </a>
1. Covered in ExtractImportsInLinesSuite test.<br>
<img src='screenshots/114_1.png'/>
<img src='screenshots/114_2.png'/>

#### 11. [ideaplugin] Registered window editor with disposer to release after it's usage.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/67'> #67 </a>
1. No 'Editor has not been released..' Exception after closing the plugin in idea.log/console.
<img src='screenshots/107.png'/> 

#### 12. [ideaplugin] Taking number of tiny editors in featured pane through settings panel.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/174'> #174 </a>
1. Click on settings in toolbar. This should open Settings panel.
<img src='screenshots/174.png'/>
2. Change Featured Count to 10 and click on 'Apply' button followed by 'Ok' button
3. Hit refresh button.
4. In Featured Pane atmost 10 tiny editors should get populated.

#### 13. [ideaplugin] Tabbed UI alterations.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/150'> #150 </a>
1. Open the Betterdocs plugin, it should have two panes named Featured and all from left to right.
<img src='screenshots/150.png'/>
2. Hit refresh button to check search results in both panes.

#### 14. [ideaplugin] Plugin throwing exception if HTTP response code from elastic search is not 200.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/139'> #139 </a>
1. Open Settings Panel, change 'Elastic Search URL' to labs.imaginea.com.
<img src='screenshots/139.png'/>
2. Apply and click 'Ok'.
3. Hit refresh button.
4. All Pane Should have message "Connection Error: 404 Not Found".
<img src='screenshots/connection_error.png'/>
5. No Exception after closing the plugin in idea.log/console.

#### 15. [ideaplugin] Rename "Repo Stars" in Code Pane to "Score:" and make "Repo Name" clickable.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/151'> #151 </a>
1. Open Betterdocs plugin, select code and hit refresh action.
2. Check the featured pane tiny editors header, clicking on the project name should open project repo in github.com.
<img src='screenshots/151.png'/>

#### 16. [ideaplugin] Expire notification bubble in 3 or 4 seconds.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/157'> #157 </a>
1. Open Betterdocs plugin, select code and hit refresh action.
2. If ES Result returns no results notification should expire.
<img src='screenshots/157.png'/>

#### 17. [ideaplugin] NPE while using the plugin.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/135'> #135 </a>
1. Open Betterdocs plugin, select code from any non java file and hit refresh action.
2. No NullPointerException after closing the plugin in idea.log/console.

#### 18. [ideaplugin] Add UUID to request context and settins panel.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/168'> #168 </a>
1. Open Betterdocs plugin, click on settings and check the generated plugin ID.
<img src='screenshots/168.png'/>
2. Plugin ID added to request context as well.

#### 19. [ideaplugin] Add plugin and idea version to user-agent.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/194'> #194 </a>
1. Intellij idea version and plugin version added to user-agent.

#### 20. [ideaplugin] TinyEditors are not honoring soft wrap of intellij.
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/126'> #126 </a>
1. Open Betterdocs plugin, select code and hit refresh action.
2. Make sure soft wrap is checked in intellij editor settings.
<img src='screenshots/soft_wrap.png'/>
3. Check the featured pane tiny editors, try to resizing them.Tiny editors should honor intellij soft wrap.
<img src='screenshots/126.png'/>

#### 21. [idea] #191 Added icon to plugin tool window in plugin.xml instead of setting it in MainWindow
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/191'> #191 </a>
1. BetterDocs idea plugin should have icon even before you invoke it.<br>
<img src='screenshots/toolWindowIcon.png'/>

#### 22. [idea] #153 Added license agreement for using betterdocs for first time
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/153'> #153 </a>
1. Using BetterDocs for the first time should produce a Legal Notice dialog.
2. It should have two options "Accept" and "Decline"
3. On clicking "Accept" that dialog should disappear and allows you to use better docs.
4. On clicking "Decline" IDE should restart and betterdocs is disabled.<br>
<img src='screenshots/legalNotice.png'/>

#### 23. [idea] #180 clearing all pane preview editor before every new request
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/180> #180 </a>
1. After every new request(clicking refresh action button) contents of main window preview editor should be cleared.<br>
<img src='screenshots/clearMainPanePreviewEditor.png'/>

#### 24. [idea] #83 added keyboard shortcut to plugin
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/83> #83 </a>
1. BetterDocs idea plugin can be shown/hidden by hitting "alt" + 8

#### 25. [idea] #50 results will be updated on opening plugin window 
##### fixes <a href='https://github.com/Imaginea/BetterDocs/issues/50> #50 </a>
1. Every time plugin is opened(if previously it's hidden) results get automatically refreshed.