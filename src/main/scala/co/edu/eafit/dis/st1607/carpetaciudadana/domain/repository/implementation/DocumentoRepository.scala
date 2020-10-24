package co.edu.eafit.dis.st1607.carpetaciudadana.domain.repository.implementation
import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.database.DatabaseConnection
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.{AppError, DatabaseError}
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.Documento
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.repository.DocumentoRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DocumentoRepository extends DocumentoRepository {
  override def insert(documento: Documento)(
      config: CarpetaCiudadanaConfig): Future[Either[AppError, Documento]] = {
    val connection = DatabaseConnection.databaseConnection(config)
    val statement  = connection.createStatement()

    Future(statement.execute("INSERT INTO documentos(id, idCiudadano, url, titulo, autenticado) " +
      s"VALUES (${documento.id}, ${documento.idCiudadano}, ${documento.url}, ${documento.titulo}, ${documento.autenticado})"))
      .map {
        case true => Right(documento)
        case false =>
          Left(
            DatabaseError(s"Error insertando el documento. idCiudadano: ${documento.idCiudadano}"))
      }
      .recover {
        case th => Left(DatabaseError(th.getMessage))
      }
  }

  override def update(documento: Documento)(
      config: CarpetaCiudadanaConfig): Future[Either[AppError, Documento]] = {
    val connection = DatabaseConnection.databaseConnection(config)
    val statement  = connection.createStatement()

    Future(
      statement.executeUpdate(
        "UPDATE documentos SET " +
          s"url = ${documento.url}, " +
          s"address = ${documento.autenticado} " +
          s"WHERE id = ${documento.id}"))
      .map { n =>
        if (n > 0) Right(documento)
        else
          Left(
            DatabaseError(
              s"No se pudo actualizar el documento ${documento.id}. Filas actualizadas: $n"))
      }
      .recover {
        case th => Left(DatabaseError(th.getMessage))
      }
  }
}
