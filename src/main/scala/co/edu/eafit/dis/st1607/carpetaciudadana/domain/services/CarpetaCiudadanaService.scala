package co.edu.eafit.dis.st1607.carpetaciudadana.domain.services
import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.AppError
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.{Ciudadano, Documento}
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.dto.{CiudadanoDTO, DocumentoDTO}

import scala.concurrent.Future

trait CarpetaCiudadanaService {
  def registrarCiudadano(ciudadanoDTO: CiudadanoDTO)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]]

  def obtenerCiudadano(id: Int)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]]

  def registrarDocumento(documentoDTO: DocumentoDTO, path: String)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Documento]]
}
