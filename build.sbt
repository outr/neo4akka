name := "neo4akka"
organization := "com.outr"
version := "1.1.0-SNAPSHOT"
scalaVersion := "2.12.1"
crossScalaVersions := Seq("2.12.1", "2.11.8")
sbtVersion := "0.13.13"
fork := true

libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "10.0.4"
libraryDependencies += "com.propensive" %% "rapture-json-jackson" % "2.0.0-M8"
libraryDependencies += "org.neo4j" % "neo4j-cypher-frontend-3.1" % "3.1.2"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF")