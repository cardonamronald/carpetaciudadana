package co.edu.eafit.dis.st1607.carpetaciudadana.domain.services.implementation
import java.util.UUID

import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.AppError
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.{Ciudadano, Documento}
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.repository.implementation.{
  CiudadanoRepository,
  DocumentoRepository
}
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.services.CarpetaCiudadanaService
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.dto.{CiudadanoDTO, DocumentoDTO}
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.service.{
  AzureStorageService,
  GovCarpetaService
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object CarpetaCiudadanaService extends CarpetaCiudadanaService {
  override def registrarCiudadano(ciudadanoDTO: CiudadanoDTO)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] = {
    validarCiudadano(
      Ciudadano(ciudadanoDTO.id,
                ciudadanoDTO.name,
                ciudadanoDTO.address,
                ciudadanoDTO.email,
                valido = false,
                List.empty)) flatMap { either =>
      either.fold(
        error => Future.successful(Left(error)),
        ciudadano => GovCarpetaService.registrarCiudadano(ciudadano)
      )
    }
  }

  private def validarCiudadano(ciudadano: Ciudadano)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] =
    GovCarpetaService.validarCiudadano(ciudadano) flatMap { either =>
      either fold (
        error => Future.successful(Left(error)),
        CiudadanoRepository.insertar
      )
    }

  override def registrarDocumento(documentoDTO: DocumentoDTO, path: String)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Documento]] =
    cargarDocumento(documentoDTO, path) flatMap (either =>
      either.fold(
        appError => Future.successful(Left(appError)),
        doc =>
          DocumentoRepository.insert(doc) flatMap { either =>
            either fold (
              error => Future.successful(Left(error)),
              autenticarDocumento
            )
        }
      ))

  private def cargarDocumento(documentoDTO: DocumentoDTO, path: String)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Documento]] = {
    obtenerCiudadano(Integer.parseInt(documentoDTO.idCiudadano)) map { either =>
      either.map(
        ciudadano =>
          AzureStorageService.uploadFile(Documento(UUID.randomUUID().toString,
                                                   ciudadano.id,
                                                   "",
                                                   documentoDTO.titulo,
                                                   autenticado = false),
                                         path)
      )
    }
  }

  override def obtenerCiudadano(id: Int)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] =
    CiudadanoRepository.obtener(id)

  private def autenticarDocumento(documento: Documento)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Documento]] =
    GovCarpetaService.autenticarDocumento(documento) flatMap { either =>
      either fold (
        error => Future.successful(Left(error)),
        DocumentoRepository.update
      )
    }
}
