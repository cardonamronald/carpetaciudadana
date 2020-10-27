package co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.HttpError

import scala.concurrent.Future

object HttpService {
  implicit val system           = ActorSystem("SingleRequest")
  implicit val executionContext = system.getDispatcher
  implicit val materializer     = ActorMaterializer()

  def get(uri: String): Future[Either[HttpError, Boolean]] = {
    Http()
      .singleRequest(HttpRequest(method = HttpMethods.GET, uri = uri))
      .map(response => Right(response.status.isFailure()))
      .recover { case th => Left(HttpError(th.getMessage, StatusCodes.InternalServerError.intValue)) }
  }

  def post[T <: AnyRef: Manifest, U <: AnyRef: Manifest](
      body: T,
      uri: String): Future[Either[HttpError, U]] =
    for {
      request <- Marshal(body)
      response <- Http()
        .singleRequest(HttpRequest(method = HttpMethods.POST, uri = uri, entity = request))
      answer <- Unmarshal(response.entity).to[String]
    } yield answer
}
