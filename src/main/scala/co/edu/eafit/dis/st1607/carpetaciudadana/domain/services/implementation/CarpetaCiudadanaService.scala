package co.edu.eafit.dis.st1607.carpetaciudadana.domain.services.implementation
import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.AppError
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.{Ciudadano, Documento}
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.repository.implementation.CiudadanoRepository
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.services.CarpetaCiudadanaService
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.dto.{CiudadanoDTO, DocumentoDTO}
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.service.HttpService
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object CarpetaCiudadanaService extends CarpetaCiudadanaService {
  private val conf: Config = ConfigFactory.load

  private val base                 = conf.getString("govcarpeta.base")
  private val registerCitizen      = base + conf.getString("govcarpeta.registerCitizen")
  private val authenticateDocument = base + conf.getString("govcarpeta.authenticateDocument")
  private val validateCitizen      = base + conf.getString("govcarpeta.validateCitizen")

  override def registrarCiudadano(ciudadanoDTO: CiudadanoDTO)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] =
    insertarCiudadano(ciudadanoDTO) flatMap { either =>
      either.fold(
        error => Future.successful(Left(error)),
        ciudadano => validarCiudadano(ciudadano) // TODO falta llamar al registerCitizen
      )
    }

  private def insertarCiudadano(ciudadanoDTO: CiudadanoDTO)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] =
    CiudadanoRepository.insertar(
      Ciudadano(ciudadanoDTO.id,
                ciudadanoDTO.name,
                ciudadanoDTO.address,
                ciudadanoDTO.email,
                valido = false,
                List.empty))

  private def validarCiudadano(ciudadano: Ciudadano)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] =
    HttpService.get(String.format(validateCitizen, ciudadano.id.toString)) flatMap { either =>
      either fold (
        error => Future.successful(Left(error)),
        valido => CiudadanoRepository.actualizar(ciudadano.copy(valido = valido))
      )
    }

  override def obtenerCiudadano(id: Int)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] =
    CiudadanoRepository.obtener(id)

  override def registrarDocumento(documentoDTO: DocumentoDTO, path: String)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Documento]] =
    ???





  // 1. Guardar documento en S3
  // 2. Guardar link en la base de datos
  // 3. Autenticarlo

}
