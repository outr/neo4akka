package com.outr.neo4akka

import org.neo4j.cypher.internal.frontend.v3_0.parser.CypherParser

import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

@compileTimeOnly("Enable macro paradise to expand macro annotations")
object Macros {
  def cypher(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[CypherQuery] = {
    import c.universe._

    c.prefix.tree match {
      case Apply(_, List(Apply(_, rawParts))) => {
        val parts = rawParts map { case t @ Literal(Constant(const: String)) => (const, t.pos) }

        val b = new StringBuilder
        parts.zipWithIndex.foreach {
          case ((raw, pos), index) => {
            if (index > 0) {
              b.append(s"{arg$index}")
            }
            b.append(raw)
          }
        }
        val argsMap = args.zipWithIndex.map {
          case (value, index) => {
            s"arg${index + 1}" -> value
          }
        }.toMap
        val parser = new CypherParser
        parser.parse(b.toString())
        c.Expr[CypherQuery](q"""CypherQuery(${b.toString()}, $argsMap)""")
      }
      case _ => c.abort(c.enclosingPosition, "Bad usage of cypher interpolation 2.")
    }
  }

  def convert[T](c: blackbox.Context)(implicit t: c.WeakTypeTag[T]): c.Expr[Vector[T]] = {
    import c.universe._

    val columnResults = c.prefix.tree
    val tpe = weakTypeOf[T]
    val members = tpe.decls
    val fields = members.filter(s => s.asTerm.isVal && s.asTerm.isCaseAccessor)
    val fieldNames = fields.map(_.name.decodedName.toString.trim)
    val fieldTypes = fields.map(_.info)
    val conversion = fieldNames.zip(fieldTypes).map {
      case (fieldName, fieldType) => q"""r.data($fieldName).asInstanceOf[$fieldType]"""
    }
    val items = q"$columnResults.objectResults.map(r => new $t(..$conversion))"
    c.Expr[Vector[T]](items)
  }
}