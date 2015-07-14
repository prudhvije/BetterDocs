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

import com.typesafe.sbt.SbtGit.git
import com.typesafe.sbt.{GitBranchPrompt, GitVersioning}
import sbt._
import sbt.Keys._
import de.johoop.findbugs4sbt.FindBugs._
import de.johoop.cpd4sbt.CopyPasteDetector._
import de.johoop.cpd4sbt.Language

object KodeBeagleBuild extends Build {

  lazy val root = Project(
    id = "kodebeagle",
    base = file("."),
    settings = kodebeagleSettings,
    aggregate = aggregatedProjects
  ).enablePlugins(GitVersioning).enablePlugins(GitBranchPrompt)

  lazy val core = Project("core", file("core"), settings = coreSettings)

  lazy val ideaPlugin = Project("ideaPlugin", file("plugins/idea/kodebeagleidea"), settings =
    pluginSettingsFull ++ findbugsSettings ++ codequality.CodeQualityPlugin.Settings)

  lazy val pluginTests = Project("pluginTests", file("plugins/idea/pluginTests"), settings =
    pluginTestSettings) dependsOn ideaPlugin

  val scalacOptionsList = Seq("-encoding", "UTF-8", "-unchecked", "-optimize", "-deprecation",
    "-feature")

  // This is required for plugin devlopment.
  val ideaLib = sys.env.get("IDEA_LIB").orElse(sys.props.get("idea.lib"))

  def aggregatedProjects: Seq[ProjectReference] = {
    if (ideaLib.isDefined) {
      Seq(core, ideaPlugin, pluginTests)
    } else {
      println("""[warn] Plugin project disabled. To enable append -Didea.lib="idea/lib" to JVM 
        params in SBT settings or while invoking sbt (incase it is called from commandline.). """)
      Seq(core)
    }
  }

  def pluginSettings = kodebeagleSettings ++ (if (ideaLib.isEmpty) Seq() else
    cpdSettings ++ Seq(
    name := "KodeBeagleIdeaPlugin",
    libraryDependencies ++= Dependencies.ideaPlugin,
    autoScalaLibrary := false,
    cpdLanguage := Language.Java,
    cpdMinimumTokens := 30,
    unmanagedBase := file(ideaLib.get)
    ))

  def pluginSettingsFull = pluginSettings ++ (if (ideaLib.isEmpty) Seq() else Seq(
    resourceGenerators in Compile <+=
      (resourceDirectory in Compile, version, git.gitCurrentTags) map { (dir, v, tags) =>
        val file = dir / "META-INF" / "plugin.xml"
        IO.write(file, IO.read(file).replaceAll("VERSION_STRING", v))
        Seq(file)
      }
  ))

  def pluginTestSettings = pluginSettings ++ Seq (
    name := "plugin-test",
    libraryDependencies ++= Dependencies.ideaPluginTest,
    autoScalaLibrary := true,
    scalaVersion := "2.11.6"
    )

  def coreSettings = kodebeagleSettings ++ Seq(libraryDependencies ++= Dependencies.kodebeagle)

  def kodebeagleSettings =
    Defaults.coreDefaultSettings ++ Seq (
      name := "KodeBeagle",
      organization := "com.kodebeagle",
      git.baseVersion := "0.1.0",
      scalaVersion := "2.11.6",
      git.useGitDescribe := true,
      scalacOptions := scalacOptionsList,
      updateOptions := updateOptions.value.withCachedResolution(true),
      updateOptions := updateOptions.value.withLatestSnapshots(false),
      crossPaths := false,
      fork := true,
      javacOptions ++= Seq("-source", "1.7"),
      javaOptions += "-Xmx2048m",
      javaOptions += "-XX:+HeapDumpOnOutOfMemoryError"
    )

}

object Dependencies {

  val spark = "org.apache.spark" %% "spark-core" % "1.3.1"  % "provided" //Provided makes it not run through sbt run.
  val parserCombinator = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3"
  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % "test" 
  val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.7.2"
  val javaparser = "com.github.javaparser" % "javaparser-core" % "2.0.0"
  val json4s = "org.json4s" %% "json4s-ast" % "3.2.10"
  val json4sJackson = "org.json4s" %% "json4s-jackson" % "3.2.10"
  val httpClient = "commons-httpclient" % "commons-httpclient" % "3.1"
  val config = "com.typesafe" % "config" % "1.2.1"
  val jgit = "org.eclipse.jgit" % "org.eclipse.jgit" % "3.7.0.201502260915-r"
  val commonsIO = "commons-io" % "commons-io" % "2.4"

  val kodebeagle = Seq(spark, parserCombinator, scalaTest, slf4j, javaparser, json4s, config,
    json4sJackson, jgit, commonsIO)
  val ideaPluginTest = Seq(scalaTest, commonsIO)
  val ideaPlugin = Seq()

  // transitively uses
  // commons-compress-1.4.1

}
