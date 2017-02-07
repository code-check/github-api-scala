organization := "io.code-check"

name := """github-api"""

version := "0.2.0-SNAPSHOT"

scalaVersion := "2.11.7"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.9.21" % "provided",
  "org.asynchttpclient" % "async-http-client" % "2.0.15" % "provided",
  "org.json4s" %% "json4s-jackson" % "3.4.2",
  "org.json4s" %% "json4s-ext" % "3.4.2",
  "joda-time" % "joda-time" % "2.8.1",
  "com.github.scopt" %% "scopt" % "3.3.0",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

val localRepo = "../sbt-repo"

publishTo := Some(Resolver.file("givery repo",file(localRepo))(Patterns(true, Resolver.mavenStyleBasePattern)))

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
