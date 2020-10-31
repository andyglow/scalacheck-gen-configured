import xerial.sbt.Sonatype._
import ReleaseTransformations._

name := "scalacheck-gen-configured"

homepage := Some(new URL("http://github.com/andyglow/scalacheck-gen-configured"))

startYear := Some(2019)

organization := "com.github.andyglow"

organizationName := "com.github.andyglow"

publishTo := sonatypePublishTo.value

publishConfiguration := publishConfiguration.value.withOverwrite(true)

publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.13.1", "2.12.10", "2.11.12")

scalacOptions ++= {
  val options = Seq(
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-Xfatal-warnings",
    "-Xlint",
    "-Ywarn-unused-import",
    "-Yno-adapted-args",
//    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
//    "-Xno-patmat-analysis",
    //      "-Xlog-implicits",
    //      "-Ytyper-debug",
    "-Xfuture",
    "-language:higherKinds")

  // WORKAROUND https://github.com/scala/scala/pull/5402
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 12)) => options.map {
      case "-Xlint"               => "-Xlint:-unused,_"
      case "-Ywarn-unused-import" => "-Ywarn-unused:imports,-patvars,-privates,-locals,-implicits"
      case other                  => other
    }
    case Some((2, n)) if n >= 13  => options.filterNot { opt =>
      opt == "-Yno-adapted-args" || opt == "-Xfuture"
    }.map {
      case "-Ywarn-unused-import" => "-Ywarn-unused:imports,-patvars,-privates,-locals,-implicits"
      case other                  => other
    } ++ Seq(
      "-Xsource:2.13",
      // parser code uses "return" to control the flow
      "-Xlint:-nonlocal-return,_")
    case _             => options
  }
}

scalacOptions in (Compile,doc) ++= Seq(
  "-groups",
  "-implicits",
  "-no-link-warnings")

licenses := Seq(("LGPL-3.0", url("https://www.gnu.org/licenses/lgpl-3.0.en.html")))

publishMavenStyle := true

sonatypeProjectHosting := Some(
  GitHubHosting(
    "andyglow",
    "scalacheck-gen-configured",
    "andyglow@gmail.com"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/andyglow/scalacheck-gen-configured"),
    "scm:git@github.com:andyglow/scalacheck-gen-configured.git"))

developers := List(
  Developer(
    id    = "andyglow",
    name  = "Andriy Onyshchuk",
    email = "andyglow@gmail.com",
    url   = url("https://ua.linkedin.com/in/andyglow")))

releaseCrossBuild := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true),
  pushChanges)

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.15.0",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "3.2.2" % Test)

sourceGenerators in Compile += Def.task {
  val v = (scalaVersion in Compile).value
  val s = (sourceManaged in Compile).value

  Boiler.gen(s, v)
}

mappings in (Compile, packageSrc) ++= {
  val base = (sourceManaged in Compile).value
  (managedSources in Compile).value.map { file =>
    file -> file.relativeTo(base).get.getPath
  }
}