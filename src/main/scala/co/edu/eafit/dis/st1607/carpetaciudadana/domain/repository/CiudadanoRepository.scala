package co.edu.eafit.dis.st1607.carpetaciudadana.domain.repository
import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.AppError
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.Ciudadano

import scala.concurrent.Future

trait CiudadanoRepository {
  def insertar(ciudadano: Ciudadano)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]]

  def actualizar(ciudadano: Ciudadano)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]]

  def obtener(id: Int)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]]
}
