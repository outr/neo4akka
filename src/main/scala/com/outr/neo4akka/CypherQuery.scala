package com.outr.neo4akka

case class CypherQuery(query: String, args: Map[String, String])