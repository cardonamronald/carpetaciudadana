package co.edu.eafit.dis.st1607.carpetaciudadana.routes
import java.nio.file.Paths

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.FileIO
import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.services.implementation.CarpetaCiudadanaService
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.JsonSupport
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.dto.{CiudadanoDTO, DocumentoDTO}

import scala.util.{Failure, Success}

object CarpetaCiudadanaRoutes extends JsonSupport {
  def routes(implicit config: CarpetaCiudadanaConfig): Route = {
    path("ciudadano") {
      concat(
        path("registrar") {
          post {
            entity(as[CiudadanoDTO]) { ciudadanoDTO =>
              onSuccess(CarpetaCiudadanaService.registrarCiudadano(ciudadanoDTO)) {
                case Left(error)      => complete(error)
                case Right(ciudadano) => complete(ciudadano)
              }
            }
          }
        } ~
          path(IntNumber) { id =>
            get {
              onSuccess(CarpetaCiudadanaService.obtenerCiudadano(id)) {
                case Left(error)      => complete(error)
                case Right(ciudadano) => complete(ciudadano)
              }
            }
          }
      )
    } ~ path("documento") {
      concat(
        path("registrar") {
          (post & entity(as[DocumentoDTO])) {
            documentoDTO =>
              extractRequestContext {
                ctx =>
                  {
                    implicit val materializer = ctx.materializer
                    implicit val ec           = ctx.executionContext
                    fileUpload("documento") {
                      case (fileInfo, fileStream) =>
                        val sink        = FileIO.toPath(Paths.get("/tmp") resolve fileInfo.fileName)
                        val writeResult = fileStream.runWith(sink)
                        onSuccess(writeResult) { result =>
                          result.status match {
                            case Success(_) =>
                              onSuccess(CarpetaCiudadanaService
                                .registrarDocumento(documentoDTO, "/tmp" + fileInfo.fileName)) {
                                case Left(error)      => complete(error)
                                case Right(documento) => complete(documento)
                              }
                            case Failure(e) => throw e
                          }
                        }
                    }
                  }
              }
          }
        }
      )
    }
  }
}
