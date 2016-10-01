package specs

import com.outr.neo4akka._

import org.scalatest.{Matchers, WordSpec}

class CypherSpec extends WordSpec with Matchers {
  "Cypher" should {
    "interpolate a valid query" in {
      val q = cypher"MATCH (p: Person {name: {personName}}) RETURN p"
      q should not be null
    }
    "interpolate an invalid query" in {
      val q = cypher"MATCH BAD (p: Person {name: {personName}}) RETURN p"
      q should not be null
    }
  }
}
