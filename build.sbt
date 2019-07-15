organization := "io.code-check"

name := """github-api"""

version := "0.2.1-SNAPSHOT"

scalaVersion := "2.12.8"

crossScalaVersions := Seq("2.10.7", "2.11.12", scalaVersion.value)

description := "The GitHub API from Scala with Async HTTP Client (Netty)"

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

homepage := Some(url("http://github.com/code-check/github-api-scala"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/code-check/github-api-scala"),
    "scm:git@github.com:code-check/github-api-scala.git"
  )
)

developers := List(
  Developer(
    id    = "shunjikonishi",
    name  = "Shunji Konishi",
    email = "@shunjikonishi",
    url   = url("http://qiita.com/shunjikonishi")
  )
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.9.21" % "provided",
  "org.asynchttpclient" % "async-http-client" % "2.0.15" % "provided",
  "org.json4s" %% "json4s-jackson" % "3.4.2",
  "org.json4s" %% "json4s-ext" % "3.4.2",
  "joda-time" % "joda-time" % "2.8.1",
  "com.github.scopt" %% "scopt" % "3.5.0",
  "org.slf4j" % "slf4j-nop" % "1.7.22" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
