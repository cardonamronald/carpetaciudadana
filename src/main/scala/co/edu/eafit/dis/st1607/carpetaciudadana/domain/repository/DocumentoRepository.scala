package co.edu.eafit.dis.st1607.carpetaciudadana.domain.repository
import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.AppError
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.Documento

import scala.concurrent.Future

trait DocumentoRepository {
  def insert(documento: Documento)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Documento]]

  def update(documento: Documento)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Documento]]
}
