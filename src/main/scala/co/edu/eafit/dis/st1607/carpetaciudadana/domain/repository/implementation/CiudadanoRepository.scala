package co.edu.eafit.dis.st1607.carpetaciudadana.domain.repository.implementation
import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.database.DatabaseConnection
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.{AppError, DatabaseError}
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.Ciudadano
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.repository.CiudadanoRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object CiudadanoRepository extends CiudadanoRepository {

  override def insertar(ciudadano: Ciudadano)(
      config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] = {
    val connection = DatabaseConnection.databaseConnection(config)
    val statement  = connection.createStatement()

    Future(statement.execute("INSERT INTO ciudadanos(id, name, address, email, valido) " +
      s"VALUES (${ciudadano.id}, ${ciudadano.name}, ${ciudadano.address}, ${ciudadano.email}, false)"))
      .map {
        case true  => Right(ciudadano)
        case false => Left(DatabaseError("Error insertando el ciudadano"))
      }
      .recover {
        case th => Left(DatabaseError(th.getMessage))
      }
  }

  override def actualizar(ciudadano: Ciudadano)(
      config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] = {
    val connection = DatabaseConnection.databaseConnection(config)
    val statement  = connection.createStatement()

    Future(
      statement.executeUpdate(
        "UPDATE ciudadanos " +
          s"SET name = ${ciudadano.name}, " +
          s"address = ${ciudadano.address}, " +
          s"email = ${ciudadano.email}, " +
          s"valido = ${ciudadano.valido} " +
          s"WHERE id = ${ciudadano.id}"))
      .map { n =>
        if (n > 0) Right(ciudadano)
        else
          Left(
            DatabaseError(
              s"No se pudo actualizar el ciudadano ${ciudadano.id}. Filas actualizadas: $n"))
      }
      .recover {
        case th => Left(DatabaseError(th.getMessage))
      }
  }
}
