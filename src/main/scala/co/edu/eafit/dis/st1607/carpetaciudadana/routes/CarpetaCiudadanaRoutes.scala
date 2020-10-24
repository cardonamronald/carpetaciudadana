package co.edu.eafit.dis.st1607.carpetaciudadana.routes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.services.CarpetaCiudadanaService
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.JsonSupport

object CarpetaCiudadanaRoutes extends JsonSupport {
  def routes(config: CarpetaCiudadanaConfig): Route = {
    path("ciudadano") {
      concat(
        path("registrar") {
          post {
            handleWith(CarpetaCiudadanaService.registrarCiudadano)
          }
        } ~
        path(IntNumber) { id =>
          get {
            onSuccess(CarpetaCiudadanaService.obtenerCiudadano(id)) {
              case Right(ciudadano) => complete(ciudadano)
              case Left(error) => complete(error)
            }
          }
        }
      )
    }
  }
}
