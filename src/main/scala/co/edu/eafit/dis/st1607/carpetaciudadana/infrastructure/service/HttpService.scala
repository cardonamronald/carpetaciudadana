package co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.service

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.HttpError
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.JsonSupport
import co.edu.eafit.dis.st1607.carpetaciudadana.server.WebServer.logSource
import spray.json.{JsonWriter, RootJsonFormat}

import scala.concurrent.Future

object HttpService extends JsonSupport {
  implicit val system           = ActorSystem("SingleRequest")
  implicit val executionContext = system.getDispatcher

  val logger = Logging(system, this)

  def get(uri: String): Future[Either[HttpError, Boolean]] = {
    logger.info(s"GET $uri")
    Http()
      .singleRequest(HttpRequest(method = HttpMethods.GET, uri = uri))
      .map { response =>
        logger.info(s"GET succeed withCode : ${response.status}")
        response.discardEntityBytes()
        response.status match {
          case StatusCodes.NoContent => Right(true)
          case StatusCodes.OK        => Right(true)
          case _                     => Right(false)
        }
      }
      .recover {
        case th =>
          logger.info(s"GET failed withMessage : ${th.getMessage}")
          Left(HttpError(th.getMessage, StatusCodes.InternalServerError.intValue))
      }
  }

  def post[T: RootJsonFormat](body: T, uri: String): Future[Either[HttpError, Boolean]] = {
    Marshal(body).to[RequestEntity] flatMap { request =>
      logger.info(s"POST ${implicitly[JsonWriter[T]].write(body).prettyPrint} URI: $uri")
      Http()
        .singleRequest(HttpRequest(method = HttpMethods.POST, uri, entity = request))
    } map { response =>
      logger.info(s"POST succeed withCode : ${response.status}")
      response.discardEntityBytes()
      response.status match {
        case StatusCodes.Created => Right(true)
        case _                   => Right(false)
      }
    } recover {
      case th =>
        logger.info(s"POST failed withMessage : ${th.getMessage}")
        Left(HttpError(th.getMessage, StatusCodes.InternalServerError.intValue))
    }
  }
}
