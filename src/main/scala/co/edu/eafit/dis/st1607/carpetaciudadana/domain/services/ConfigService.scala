package co.edu.eafit.dis.st1607.carpetaciudadana.domain.services
import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.server.WebServer

object ConfigService {

  def serve(conf: CarpetaCiudadanaConfig): String = {
    s"""${conf.toString}"""
  }

  def reload(server: WebServer): String = {
    server.restart
    "OK"
  }

  def version(config: CarpetaCiudadanaConfig): String = {
    s"""${config.appVersion}"""
  }
}
