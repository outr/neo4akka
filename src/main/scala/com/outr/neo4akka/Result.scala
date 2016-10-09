package com.outr.neo4akka

sealed trait Result

case class ObjectResult(metaData: MetaData, data: Map[String, Any]) extends Result

case class FieldResult[T](data: T) extends Result