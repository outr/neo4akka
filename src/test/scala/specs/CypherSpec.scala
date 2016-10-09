package specs

import com.outr.neo4akka._
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scala.concurrent.ExecutionContext.Implicits.global

class CypherSpec extends WordSpec with Matchers {
  "Cypher" should {
    "interpolate a valid query" in {
      val q = cypher"MATCH (p: Person) RETURN p"
      q should not be null
      q.query should equal("MATCH (p: Person) RETURN p")
      q.args should be(Map.empty)
    }
    "interpolate a valid query with an argument" in {
      val personName = "Tom Hanks"
      val q = cypher"MATCH (p: Person {name: $personName}) RETURN p"
      q should not be null
      q.query should equal("MATCH (p: Person {name: {arg1}}) RETURN p")
      q.args should be(Map("arg1" -> stringCypherValue("Tom Hanks")))
    }
    "fail to compile with an invalid query interpolation" in {
      """cypher"MATCH BAD (p: Person {name: {personName}}) RETURN p"""" shouldNot compile
    }
    "execute a query against a local neo4j instance" in {
      val personName = "Tom Hanks"
      val query = cypher"MATCH (p: Person {name: $personName}) RETURN p"
      val r = Neo4Akka("localhost", 7474, "neo4j", "password").flatMap { session =>
        val f = session(query)
        f.onComplete { result =>
          session.dispose()
        }
        f
      }
      val response = Await.result(r, Duration.Inf)
      response.columns should be(List("p"))
      response.data.length should be(1)
      response.data.head.length should be(1)
      val entry = response.data.head.head
      entry.metaData.labels should be(List("Person"))
      entry.data should be(Map("name" -> "Tom Hanks", "born" -> 1956))
    }
    "create a person 'John Doe'" in {
      val name = "John Doe"
      val born = 1901
      val query = cypher"CREATE (p: Person { name: $name, born: $born }) RETURN p"
      val r = Neo4Akka("localhost", 7474, "neo4j", "password").flatMap { session =>
        val f = session(query)
        f.onComplete { result =>
          session.dispose()
        }
        f
      }
      val result = Await.result(r, Duration.Inf)
      result shouldNot be("")
    }
    "query the person 'John Doe'" in {
      val name = "John Doe"
      val query = cypher"MATCH (p: Person { name: $name }) RETURN p"
      val r = Neo4Akka("localhost", 7474, "neo4j", "password").flatMap { session =>
        val f = session(query)
        f.onComplete { result =>
          session.dispose()
        }
        f
      }
      val result = Await.result(r, Duration.Inf)
      val p = result[Person]("p")
      p should be(Vector(Person("John Doe", 1901)))
    }
    "delete the person 'John Doe'" in {
      val name = "John Doe"
      val query = cypher"MATCH (p: Person { name: $name }) DELETE p"
      val r = Neo4Akka("localhost", 7474, "neo4j", "password").flatMap { session =>
        val f = session(query)
        f.onComplete { result =>
          session.dispose()
        }
        f
      }
      val result = Await.result(r, Duration.Inf)
      result shouldNot be("")
    }
  }
}

case class Person(name: String, born: Int)