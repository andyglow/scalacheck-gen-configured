import xerial.sbt.Sonatype._
import ReleaseTransformations._
import ScalaVer._

name := "scalacheck-gen-configured"

homepage := Some(new URL("http://github.com/andyglow/scalacheck-gen-configured"))

startYear := Some(2019)

organization := "com.github.andyglow"

organizationName := "com.github.andyglow"

publishTo := sonatypePublishTo.value

publishConfiguration := publishConfiguration.value.withOverwrite(true)

publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

scalaVersion := (ScalaVer.fromEnv getOrElse ScalaVer.default).full

crossScalaVersions := ScalaVer.values.map(_.full)

scalaV := ScalaVer.fromString(scalaVersion.value) getOrElse ScalaVer.default

scalacOptions := CompilerOptions(scalaV.value)

Compile / doc / scalacOptions ++= Seq(
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
  "org.scalacheck" %% "scalacheck" % "1.17.0",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "3.2.14" % Test)

Compile / sourceGenerators  += Def.task {
  val v = (Compile / scalaVersion).value
  val s = (Compile / sourceManaged).value

  Boiler.gen(s, v)
}

Compile / packageSrc / mappings ++= {
  val base = (Compile / sourceManaged).value
  (Compile / managedSources).value.map { file =>
    file -> file.relativeTo(base).get.getPath
  }
}