package co.edu.eafit.dis.st1607.carpetaciudadana.domain.services
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.AppError
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.Ciudadano
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.dto.CiudadanoDTO

import scala.concurrent.Future

object CarpetaCiudadanaService {
  def registrarCiudadano(ciudadanoDTO: CiudadanoDTO): Future[Either[AppError, Ciudadano]] = ???

  def obtenerCiudadano(id: Int): Future[Either[AppError, Ciudadano]] = ???
}
