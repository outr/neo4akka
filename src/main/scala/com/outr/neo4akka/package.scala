package com.outr

import scala.language.experimental.macros
import scala.language.implicitConversions

package object neo4akka {
  implicit def stringCypherValue(s: String): CypherValue = new CypherValue {
    override def jsonValue: Any = s
  }
  implicit def booleanCypherValue(b: Boolean): CypherValue = new CypherValue {
    override def jsonValue: Any = b
  }
  implicit def intCypherValue(i: Int): CypherValue = new CypherValue {
    override def jsonValue: Any = i
  }
  implicit def longCypherValue(l: Long): CypherValue = new CypherValue {
    override def jsonValue: Any = l
  }
  implicit def doubleCypherValue(d: Double): CypherValue = new CypherValue {
    override def jsonValue: Any = d
  }

  implicit class CypherInterpolator(val sc: StringContext) extends AnyVal {
    def cypher(args: Any*): CypherQuery = macro Macros.cypher
  }
}