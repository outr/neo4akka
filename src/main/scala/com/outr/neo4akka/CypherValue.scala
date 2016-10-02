package com.outr.neo4akka

trait CypherValue {
  def jsonValue: Any


  override def equals(obj: scala.Any): Boolean = obj match {
    case cv: CypherValue => cv.jsonValue == jsonValue
    case _ => false
  }

  override def toString: String = s"CypherValue($jsonValue)"
}