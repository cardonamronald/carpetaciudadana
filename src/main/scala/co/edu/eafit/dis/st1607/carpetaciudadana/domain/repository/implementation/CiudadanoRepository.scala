package co.edu.eafit.dis.st1607.carpetaciudadana.domain.repository.implementation
import java.sql.ResultSet

import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.database.DatabaseConnection
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.{AppError, DatabaseError}
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.{Ciudadano, Documento}
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.repository.CiudadanoRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object CiudadanoRepository extends CiudadanoRepository {

  override def insertar(ciudadano: Ciudadano)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] = {
    val connection = DatabaseConnection.databaseConnection(config)
    val statement  = connection.createStatement()

    Future(statement.execute("INSERT INTO ciudadanos(id, name, address, email, valido) " +
      s"VALUES (${ciudadano.id}, ${ciudadano.name}, ${ciudadano.address}, ${ciudadano.email}, ${ciudadano.valido}])"))
      .map {
        case true  => Right(ciudadano)
        case false => Left(DatabaseError("Error insertando el ciudadano"))
      }
      .recover {
        case th => Left(DatabaseError(th.getMessage))
      }
  }

  override def actualizar(ciudadano: Ciudadano)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] = {
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

  override def obtener(id: Int)(
      implicit config: CarpetaCiudadanaConfig): Future[Either[AppError, Ciudadano]] = {
    val connection = DatabaseConnection.databaseConnection(config)
    val statement  = connection.createStatement()

    Future(
      statement.executeQuery(
        s"SELECT * FROM ciudadanos WHERE id = $id"
      )
    ) map { resultSet =>
      Ciudadano(
        resultSet.getInt("id"),
        resultSet.getString("name"),
        resultSet.getString("address"),
        resultSet.getString("email"),
        resultSet.getBoolean("valido"),
        List.empty
      )
    } flatMap { ciudadano =>
      obtenerDocumentos(ciudadano.id).map { docs =>
        Right(ciudadano.copy(documentos = docs))
      }
    } recover {
      case th => Left(DatabaseError(th.getMessage))
    }
  }

  def obtenerDocumentos(idCiudadano: Int)(
      implicit config: CarpetaCiudadanaConfig): Future[List[Documento]] = {
    val connection = DatabaseConnection.databaseConnection(config)
    val statement  = connection.createStatement()

    def helper(list: List[Documento], resultSet: ResultSet): List[Documento] = {
      if (resultSet.next()) {
        helper(
          list :+ Documento(
            resultSet.getString("id"),
            idCiudadano,
            resultSet.getString("url"),
            resultSet.getString("titulo"),
            resultSet.getBoolean("autenticado")
          ),
          resultSet
        )
      } else list
    }

    Future(
      statement.executeQuery(s"SELECT * FROM documentos WHERE idCiudadano = $idCiudadano")
    ) map { resultSet =>
      helper(List.empty, resultSet)
    }
  }
}
