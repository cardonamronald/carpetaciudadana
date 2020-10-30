package co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.HttpError
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.JsonSupport
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.dto.RegistroCiudadanoDTO

import scala.concurrent.Future

object HttpService extends JsonSupport {
  implicit val system           = ActorSystem("SingleRequest")
  implicit val executionContext = system.getDispatcher

  def get(uri: String): Future[Either[HttpError, Boolean]] = {

    Http()
      .singleRequest(HttpRequest(method = HttpMethods.GET, uri = uri))
      .map(response => Right(response.status.isFailure()))
      .recover {
        case th => Left(HttpError(th.getMessage, StatusCodes.InternalServerError.intValue))
      }
  }

  def post(body: RegistroCiudadanoDTO, uri: String): Future[Either[HttpError, String]] =
    Marshal(body).to[RequestEntity] flatMap { request =>
      Http()
        .singleRequest(HttpRequest(method = HttpMethods.POST, uri, entity = request))
    } flatMap { response =>
      Unmarshal(response.entity).to[String] map Right.apply
    } recover {
      case th => Left(HttpError(th.getMessage, StatusCodes.InternalServerError.intValue))
    }
}
