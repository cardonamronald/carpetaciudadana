package co.edu.eafit.dis.st1607.carpetaciudadana.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.services.RootService

object RootRoutes {
  val routes: Route = get {
    path("") {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, RootService.serve()))
    }
  }
}
