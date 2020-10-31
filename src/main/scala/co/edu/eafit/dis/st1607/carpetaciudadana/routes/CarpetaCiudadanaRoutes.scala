package co.edu.eafit.dis.st1607.carpetaciudadana.routes
import java.nio.file.Paths

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.FileIO
import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error._
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.services.implementation.CarpetaCiudadanaService
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.JsonSupport
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.dto.{CiudadanoDTO, DocumentoDTO}

import scala.util.{Failure, Success}

object CarpetaCiudadanaRoutes extends JsonSupport {
  def routes(implicit config: CarpetaCiudadanaConfig): Route = {
    path("ciudadano" / "registrar") {
      post {
        entity(as[CiudadanoDTO]) { ciudadanoDTO =>
          onSuccess(CarpetaCiudadanaService.registrarCiudadano(ciudadanoDTO)) {
            case Left(error) =>
              error match {
                case CiudadanoNoValido(_)     => complete(StatusCodes.NotAcceptable, error)
                case HttpError(_, statusCode) => complete(statusCode, error)
                case DatabaseError(_, _)      => complete(StatusCodes.InternalServerError, error)
                case CiudadanoYaExiste(_)     => complete(StatusCodes.InternalServerError, error)
                case _                        => complete(StatusCodes.InternalServerError, error)
              }
            case Right(ciudadano) => complete(ciudadano)
          }
        }
      }
    } ~
      path("ciudadano" / IntNumber) { id =>
        get {
          onSuccess(CarpetaCiudadanaService.obtenerCiudadano(id)) {
            case Left(error) =>
              error match {
                case CiudadanoNoValido(_)     => complete(StatusCodes.NotAcceptable, error)
                case HttpError(_, statusCode) => complete(statusCode, error)
                case DatabaseError(_, _)      => complete(StatusCodes.InternalServerError, error)
                case _                        => complete(StatusCodes.InternalServerError, error)
              }
            case Right(ciudadano) => complete(ciudadano)
          }
        }
      } ~
      path("documento" / "registrar") {
        extractRequestContext { ctx =>
          {
            implicit val materializer = ctx.materializer
            implicit val ec           = ctx.executionContext
            formFields("idCiudadano".as[Int], "titulo") { (idCiudadano, titulo) =>
              fileUpload("documento") {
                case (fileInfo, fileStream) =>
                  val sink        = FileIO.toPath(Paths.get("/tmp") resolve fileInfo.fileName)
                  val writeResult = fileStream.runWith(sink)
                  onSuccess(writeResult) { result =>
                    result.status match {
                      case Success(_) =>
                        onSuccess(
                          CarpetaCiudadanaService
                            .registrarDocumento(DocumentoDTO(idCiudadano, titulo),
                                                "/tmp/" + fileInfo.fileName)) {
                          case Left(error) =>
                            error match {
                              case CiudadanoNoValido(_) =>
                                complete(StatusCodes.NotAcceptable, error)
                              case HttpError(_, statusCode) => complete(statusCode, error)
                              case DatabaseError(_, _) =>
                                complete(StatusCodes.InternalServerError, error)
                              case _ => complete(StatusCodes.InternalServerError, error)
                            }
                          case Right(documento) => complete(documento)
                        }

                      case Failure(e) =>
                        complete(StatusCodes.BadRequest, CouldNotUploadFile(e.getMessage))
                    }
                  }
              }
            }
          }
        }
      }
  }
}
