package co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.service

import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.{AppError, CiudadanoNoValido}
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.{Ciudadano, Documento}
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.dto.RegistroCiudadanoDTO
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object GovCarpetaService {
  private val conf: Config = ConfigFactory.load

  private val base                 = conf.getString("govcarpeta.base")
  private val registerCitizen      = base + conf.getString("govcarpeta.registerCitizen")
  private val authenticateDocument = base + conf.getString("govcarpeta.authenticateDocument")
  private val validateCitizen      = base + conf.getString("govcarpeta.validateCitizen")
  private val operatorId           = conf.getInt("carpetaciudadana.operatorId")
  private val operatorName         = conf.getString("carpetaciudadana.operatorName")

  def validarCiudadano(ciudadano: Ciudadano)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] =
    HttpService.get(String.format(validateCitizen, ciudadano.id.toString)) map { either =>
      either map (
          valido => ciudadano.copy(valido = valido)
      )
    }

  def autenticarDocumento(documento: Documento)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Documento]] =
    HttpService.get(
      String.format(authenticateDocument, documento.id, documento.url, documento.titulo)) map {
      either =>
        either map (
            autenticado => documento.copy(autenticado = autenticado)
        )
    }

  def registrarCiudadano(ciudadano: Ciudadano)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] =
    HttpService.post(RegistroCiudadanoDTO(ciudadano.id,
                                          ciudadano.name,
                                          ciudadano.address,
                                          ciudadano.email,
                                          operatorId,
                                          operatorName),
                     registerCitizen) map (either =>
      either flatMap { result =>
        if (result) Right(ciudadano)
        else Left(CiudadanoNoValido("No se pudo registrar el ciudadano"))
      })
}
