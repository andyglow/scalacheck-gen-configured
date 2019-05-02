import xerial.sbt.Sonatype._

name := "scalacheck-gen-configured"

homepage := Some(new URL("http://github.com/andyglow/scalacheck-gen-configured"))

startYear := Some(2017)

organizationName := "andyglow"

publishTo := sonatypePublishTo.value

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.12.8", "2.11.12")

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  //  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture")

scalacOptions in (Compile,doc) ++= Seq(
  "-groups",
  "-implicits",
  "-no-link-warnings")

licenses := Seq(("LGPL-3.0", url("https://www.gnu.org/licenses/lgpl-3.0.en.html")))

sonatypeProfileName := "com.github.andyglow"

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

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.14.0",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
