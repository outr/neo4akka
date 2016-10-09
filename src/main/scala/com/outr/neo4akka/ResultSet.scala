package com.outr.neo4akka

case class ResultSet(results: Vector[ColumnResults]) {
  lazy val map = results.map(r => r.column -> r).toMap

  def get(column: String): Option[ColumnResults] = map.get(column)
  def apply(column: String): ColumnResults = get(column).getOrElse(throw new NullPointerException(s"No column found by name '$column'. Columns: ${results.map(_.column).mkString(", ")}"))
}
