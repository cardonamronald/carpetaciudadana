package co.edu.eafit.dis.st1607.carpetaciudadana.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.services.ConfigService
import co.edu.eafit.dis.st1607.carpetaciudadana.server.WebServer

object ConfigRoutes {
  def routes(conf: CarpetaCiudadanaConfig, server: WebServer) = {
    get {
      path("config" / "reload") {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, ConfigService.reload(server)))
      }
    } ~
      get {
        path("config") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, ConfigService.serve(conf)))

        }
      } ~
      get {
        path("version") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, ConfigService.version(conf)))

        }
      }
  }
}
