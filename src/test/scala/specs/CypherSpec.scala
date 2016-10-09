package specs

import com.outr.neo4akka._
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scala.concurrent.ExecutionContext.Implicits.global

class CypherSpec extends WordSpec with Matchers {
  "Cypher" should {
    val sessionFuture = Neo4Akka("localhost", 7474, "neo4j", "password")

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
      val r = sessionFuture.flatMap { session =>
        session(query)
      }
      val resultSet = Await.result(r, Duration.Inf)
      resultSet.results.length should be(1)
      val people = resultSet("p").objectResults
      people.size should be(1)
      people.head.data should be(Map("name" -> "Tom Hanks", "born" -> 1956))
    }
    "execute a query for a specific value" in {
      val personName = "Tom Hanks"
      val query = cypher"MATCH (p: Person {name: $personName}) RETURN p.name"
      val r = sessionFuture.flatMap { session =>
        session(query)
      }
      val resultSet = Await.result(r, Duration.Inf)
      val names = resultSet("p.name").fieldResults[String]
      names.size should be(1)
      names.head.data should be("Tom Hanks")
    }
    "execute a query for two specific values and an object" in {
      val personName = "Tom Hanks"
      val query = cypher"MATCH (p: Person {name: $personName}) RETURN p.name, p.born, p"
      val r = sessionFuture.flatMap { session =>
        session(query)
      }
      val resultSet = Await.result(r, Duration.Inf)
      val names = resultSet("p.name").fieldResults[String]
      val born = resultSet("p.born").fieldResults[Int]
      val people = resultSet("p").objectResults
      names.size should be(1)
      names.head.data should be("Tom Hanks")
      born.size should be(1)
      born.head.data should be(1956)
      people.size should be(1)
      people.head.data should be(Map("name" -> "Tom Hanks", "born" -> 1956))
    }
    "create a person 'John Doe'" in {
      val name = "John Doe"
      val born = 1901
      val query = cypher"CREATE (p: Person { name: $name, born: $born }) RETURN p"
      val r = sessionFuture.flatMap { session =>
        session(query)
      }
      val result = Await.result(r, Duration.Inf)
      result("p").objectResults.head.data should be(Map("name" -> name, "born" -> born))
    }
    "query the person 'John Doe'" in {
      val name = "John Doe"
      val query = cypher"MATCH (p: Person { name: $name }) RETURN p"
      val r = sessionFuture.flatMap { session =>
        session(query)
      }
      val resultSet = Await.result(r, Duration.Inf)
      val people = resultSet("p")[Person]
      people should be(Vector(Person("John Doe", 1901)))
    }
    "delete the person 'John Doe'" in {
      val name = "John Doe"
      val query = cypher"MATCH (p: Person { name: $name }) DELETE p"
      val r = sessionFuture.flatMap { session =>
        session(query)
      }
      Await.result(r, Duration.Inf)
    }
    "query multiple movie titles from the 1990s" in {
      val year = 1990
      val query = cypher"MATCH (movie: Movie) WHERE movie.released > $year AND movie.released < 2000 RETURN movie.title LIMIT 3"
      val request = sessionFuture.flatMap { session =>
        session(query)
      }
      val resultSet = Await.result(request, Duration.Inf)
      val titles = resultSet("movie.title").fieldResults[String].map(_.data)
      titles should be(Vector("The Matrix", "The Devil's Advocate", "A Few Good Men"))
    }
    "query 'Tom Hanks' and three oldest movies he acted in" in {
      val name = "Tom Hanks"
      val query = cypher"MATCH (people: Person { name: $name })-[:ACTED_IN]->(movies: Movie) RETURN people, movies ORDER BY movies.released LIMIT 3"
      val request = sessionFuture.flatMap { session =>
        session(query)
      }
      val resultSet = Await.result(request, Duration.Inf)
      val people = resultSet("people")[Person]
      people.size should be(1)
      people.head should be(Person("Tom Hanks", 1956))
      val movies = resultSet("movies")[Movie]
      movies.size should be(3)
      movies.map(_.title) should be(Vector("Joe Versus the Volcano", "A League of Their Own", "Sleepless in Seattle"))
    }
    "dispose the session" in {
      sessionFuture.map(_.dispose())
    }
  }
}

case class Person(name: String, born: Int)

case class Movie(title: String, tagline: String, released: Int)