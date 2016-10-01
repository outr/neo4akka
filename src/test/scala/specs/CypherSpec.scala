package specs

import com.outr.neo4akka._

import org.scalatest.{Matchers, WordSpec}

class CypherSpec extends WordSpec with Matchers {
  "Cypher" should {
    "interpolate a valid query" in {
      val personName = "Tom Hanks"
      val q = cypher"MATCH (p: Person {name: $personName}) RETURN p"
      q should not be null
      q.query should equal("MATCH (p: Person {name: {arg1}}) RETURN p")
      q.args should be(Map("arg1" -> "Tom Hanks"))
    }
    "fail to compile with an invalid query interpolation" in {
      """cypher"MATCH BAD (p: Person {name: {personName}}) RETURN p"""" shouldNot compile
    }
  }
}