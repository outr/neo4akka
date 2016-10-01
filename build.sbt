name := "neo4akka"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

sbtVersion := "0.13.11"

libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "2.4.7"

libraryDependencies += "com.lihaoyi" %% "upickle" % "0.4.1"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.3.0"

libraryDependencies += "org.neo4j" % "neo4j-cypher-frontend-3.0" % "3.0.6"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"