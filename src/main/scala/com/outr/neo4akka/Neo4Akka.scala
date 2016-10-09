package com.outr.neo4akka

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpHeader, HttpMethods, HttpRequest, HttpResponse, MediaTypes}
import akka.http.scaladsl.model.headers._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{BigIntegerNode, TextNode}

import scala.concurrent.ExecutionContext.Implicits.global
import rapture.json._
import rapture.json.jsonBackends.jackson._
import formatters.compact._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.experimental.macros

object Neo4Akka {
  def apply(host: String, port: Int, username: String, password: String): Future[Neo4Akka] = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val flow = Http().outgoingConnection(host = host, port = port)
    val authorization = Authorization(BasicHttpCredentials(username, password))
    val accept = Accept(MediaTypes.`application/json`)
    val headers = List(authorization, accept)
    val responseFuture = Source.single(HttpRequest(uri = "/db/data/", headers = headers)).via(flow).runWith(Sink.head)
    val f = responseFuture.map { response =>
      try {
        val data = new String(response.entity.asInstanceOf[HttpEntity.Strict].data.toArray)
        val value = Json.parse(data)
        val root = ServiceRoot(
          value.node.as[String],
          value.relationship.as[String],
          value.node_index.as[String],
          value.relationship_index.as[String],
          value.extensions_info.as[String],
          value.relationship_types.as[String],
          value.batch.as[String],
          value.cypher.as[String],
          value.indexes.as[String],
          value.constraints.as[String],
          value.transaction.as[String],
          value.node_labels.as[String],
          value.neo4j_version.as[String]
        )
        new Neo4Akka(root, headers, flow)
      } catch {
        case t: Throwable => {
          materializer.shutdown()
          system.terminate()
          throw t
        }
      }
    }
    f.onFailure {
      case t: Throwable => {
        materializer.shutdown()
        system.terminate()
        throw t
      }
    }
    f
  }

  private[neo4akka] def convertValue(value: Any): Json = value match {
    case d: Double => Json[Double](d)
    case i: Int => Json[Int](i)
    case b: Boolean => Json[Boolean](b)
    case l: Long => Json[Long](l)
    case s: String => Json[String](s)
    case null => Json.empty
  }

  private[neo4akka] def convertJson(json: Json): Any = json.$normalize match {
    case n: JsonNode => if (n.isBigDecimal) {
      n.asDouble()
    } else if (n.isBigInteger) {
      n.asInt()
    } else if (n.isBoolean) {
      n.asBoolean()
    } else if (n.isDouble) {
      n.asDouble()
    } else if (n.isInt) {
      n.asInt()
    } else if (n.isLong) {
      n.asLong()
    } else if (n.isNull) {
      null
    } else if (n.isTextual) {
      n.asText()
    }
  }

  private[neo4akka] def convertMap(map: Map[String, Json]): Map[String, Any] = {
    map.map {
      case (key, value) => key -> convertJson(value)
    }
  }
}

class Neo4Akka private(root: ServiceRoot, headers: List[HttpHeader], flow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]])
                      (implicit system: ActorSystem, materializer: ActorMaterializer) {
  def apply(query: CypherQuery): Future[ResultSet] = apply(query.query, query.args.map(t => t._1 -> t._2.jsonValue))

  def apply(query: String, params: Map[String, Any]): Future[ResultSet] = {
    val jsonParams = params.map {
      case (key, value) => key -> Neo4Akka.convertValue(value)
    }
    val content = json"""{
                            "query": $query,
                            "params": $jsonParams
                         }"""
    val entity = HttpEntity(ContentTypes.`application/json`, ByteString(Json.format(content)))
    val responseFuture = Source.single(HttpRequest(uri = root.cypher, headers = headers, method = HttpMethods.POST, entity = entity)).via(flow).runWith(Sink.head)
    responseFuture.flatMap { response =>
      response.entity.toStrict(15.seconds).map { e =>
        val jsonString = new String(e.data.toArray)
        val json = Json.parse(jsonString)
        val columns = json.columns.as[Vector[String]]
        val rows = json.data.as[Vector[Vector[Json]]]
        val dataMap = columns.map(c => c -> ListBuffer.empty[Result]).toMap
        rows.foreach { row =>
          row.zipWithIndex.foreach {
            case (columnJson, index) => {
              val dataEntry: Result = columnJson.metadata.as[Option[Json]] match {
                case Some(md) => {
                  val metaData = MetaData(md.id.as[Int], md.labels.as[List[String]])
                  val data = Neo4Akka.convertMap(columnJson.data.as[Map[String, Json]])
                  ObjectResult(metaData, data)
                }
                case None => {
                  val data = Neo4Akka.convertJson(columnJson)
                  FieldResult(data)
                }
              }
              dataMap(columns(index)) += dataEntry
            }
          }
        }
        ResultSet(dataMap.map {
          case (column, results) => ColumnResults(column, results.toVector)
        }.toVector)
      }
    }
  }

  def dispose(): Unit = {
    materializer.shutdown()
    system.terminate()
  }
}