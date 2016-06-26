package com.outr.neo4akka

import scala.concurrent.Await
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

object Test {
  def main(args: Array[String]): Unit = {
    val r = Neo4Akka("localhost", 7474, "neo4j", "neo4j").flatMap { session =>
      val f = session("MATCH (p: Person {name: {personName}}) RETURN p", Map("personName" -> "Tom Hanks"))
      f.onComplete { result =>
        session.dispose()
      }
      f
    }
    val result = Await.result(r, Duration.Inf)
    println(s"Result: $result")
  }
}