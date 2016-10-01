package com.outr.neo4akka

import org.neo4j.cypher.internal.frontend.v3_0.ast.Statement
import org.neo4j.cypher.internal.frontend.v3_0.parser.CypherParser

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Test {
  def main(args: Array[String]): Unit = {
//    val query = cypher"MATCH (p: Person {name: {personName}}) RETURN p"
//    println(s"Statement: ${query}")

    /*val r = Neo4Akka("localhost", 7474, "neo4j", "neo4j").flatMap { session =>
      val f = session(query, Map("personName" -> "Tom Hanks"))
      f.onComplete { result =>
        session.dispose()
      }
      f
    }
    val result = Await.result(r, Duration.Inf)
    println(s"Result: $result")*/

//    val parser = new CypherParser
//    val statement = parser.parse(query)
//    println(s"Statement: $statement")
  }
}