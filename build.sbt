name := "neo4akka"

organization := "com.outr"

version := "1.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

sbtVersion := "0.13.11"

fork := true

libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "2.4.11"

libraryDependencies += "com.propensive" %% "rapture-json-jackson" % "2.0.0-M7"

libraryDependencies += "org.neo4j" % "neo4j-cypher-frontend-3.0" % "3.0.6"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.0" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF")