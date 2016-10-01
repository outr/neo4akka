package com.outr

import scala.language.experimental.macros

package object neo4akka {
  implicit class CypherInterpolator(val sc: StringContext) extends AnyVal {
    def cypher(args: Any*): CypherQuery = macro Macros.cypher
  }
}