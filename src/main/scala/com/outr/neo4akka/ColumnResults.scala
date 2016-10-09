package com.outr.neo4akka

case class ColumnResults(column: String, data: Vector[Result]) {
  def apply[T]: Vector[T] = macro Macros.convert[T]
  def objectResults: Vector[ObjectResult] = data.asInstanceOf[Vector[ObjectResult]]
  def fieldResults[T]: Vector[FieldResult[T]] = data.asInstanceOf[Vector[FieldResult[T]]]
}
