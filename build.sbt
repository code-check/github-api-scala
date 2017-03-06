organization := "io.code-check"

name := """github-api"""

version := "0.2.0-SNAPSHOT"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.10.6", scalaVersion.value, "2.12.1")

description := "The GitHub API from Scala with Async HTTP Client (Netty)"

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

homepage := Some(url("http://github.com/code-check/github-api-scala"))

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

pomExtra := (
  <url>http://github.com/code-check/github-api-scala</url>
  <licenses>
    <license>
      <name>MIT</name>
      <url>http://opensource.org/licenses/MIT</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:code-check/github-api-scala.git</url>
    <connection>scm:git@github.com:code-check/github-api-scala.git</connection>
  </scm>
  <developers>
    <developer>
      <id>shunjikonishi</id>
      <name>Shunji Konishi</name>
      <url>http://qiita.com/shunjikonishi</url>
      </developer>
  </developers>)

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

val localRepo = "../sbt-repo"

publishTo := Some(Resolver.file("givery repo",file(localRepo))(Patterns(true, Resolver.mavenStyleBasePattern)))

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
