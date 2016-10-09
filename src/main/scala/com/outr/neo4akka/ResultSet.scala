package com.outr.neo4akka

case class ResultSet(results: Vector[ColumnResults]) {
  lazy val map = results.map(r => r.column -> r).toMap

  def get(column: String, distinct: Boolean = true): Option[ColumnResults] = {
    val results = map.get(column)
    if (distinct) {
      results.map(cr => cr.copy(data = cr.data.distinct))
    } else {
      results
    }
  }
  def apply(column: String, distinct: Boolean = true): ColumnResults = get(column, distinct).getOrElse(throw new NullPointerException(s"No column found by name '$column'. Columns: ${results.map(_.column).mkString(", ")}"))
}
