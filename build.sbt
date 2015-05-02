organization := "io.code-check"

name := """github-api"""

version := "0.1.2-SNAPSHOT"

scalaVersion := "2.11.5"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.8.15",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  "joda-time" % "joda-time" % "2.7",
  "ch.qos.logback" % "logback-classic" % "1.0.7",
  "com.github.scopt" %% "scopt" % "3.3.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

val localRepo = "../sbt-repo"

publishTo := Some(Resolver.file("givery repo",file(localRepo))(Patterns(true, Resolver.mavenStyleBasePattern)))

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
