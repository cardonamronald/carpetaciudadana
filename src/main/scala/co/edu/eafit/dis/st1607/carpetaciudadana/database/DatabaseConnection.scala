package co.edu.eafit.dis.st1607.carpetaciudadana.database
import java.sql.{Connection, DriverManager}
import java.util.Properties

import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig

object DatabaseConnection {

  def databaseConnection(config: CarpetaCiudadanaConfig): Connection = {
    Class.forName(config.databaseDriver)
    val dbUrl =
      s"jdbc:postgresql://${config.databaseHost}:${config.databasePort}/${config.databaseName}"
    var properties = new Properties()
    properties.setProperty("user", config.databaseUser)
    properties.setProperty("password", config.databasePassword)
    properties.setProperty("ssl", config.databaseSslEnabled.toString)
    DriverManager.getConnection(dbUrl, properties)
  }
}
