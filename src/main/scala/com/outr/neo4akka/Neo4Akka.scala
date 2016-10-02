package com.outr.neo4akka

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpHeader, HttpMethods, HttpRequest, HttpResponse, MediaTypes}
import akka.http.scaladsl.model.headers._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import org.json4s.jackson.Serialization

import scala.concurrent.ExecutionContext.Implicits.global
import upickle.default

import scala.concurrent.Future
import scala.concurrent.duration._

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
        val root = default.read[ServiceRoot](data)
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
}

class Neo4Akka private(root: ServiceRoot, headers: List[HttpHeader], flow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]])
                      (implicit system: ActorSystem, materializer: ActorMaterializer) {
  implicit val formats = org.json4s.DefaultFormats

  def apply(query: CypherQuery): Future[String] = apply(query.query, query.args.map(t => t._1 -> t._2.jsonValue))

  def apply(query: String, params: Map[String, Any]): Future[String] = {
    val jsonMap = Map(
      "query" -> query,
      "params" -> params
    )
    val json = Serialization.write(jsonMap)
    val entity = HttpEntity(ContentTypes.`application/json`, ByteString(json))
    val responseFuture = Source.single(HttpRequest(uri = root.cypher, headers = headers, method = HttpMethods.POST, entity = entity)).via(flow).runWith(Sink.head)
    responseFuture.flatMap { response =>
      response.entity.toStrict(15.seconds).map { e =>
        new String(e.data.toArray)
      }
    }
  }

  def dispose(): Unit = {
    materializer.shutdown()
    system.terminate()
  }
}