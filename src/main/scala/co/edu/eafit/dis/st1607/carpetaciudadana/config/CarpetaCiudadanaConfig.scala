package co.edu.eafit.dis.st1607.carpetaciudadana.config

class CarpetaCiudadanaConfig(
    val databaseDriver: String,
    val databaseHost: String,
    val databasePort: Int,
    val databaseName: String,
    val databaseUser: String,
    val databasePassword: String,
    val databaseSslEnabled: Boolean,
    val serverIp: String,
    val serverPort: Int,
    val appVersion: String
) {
  override def toString: String = {
    f"""databaseDriver: $databaseDriver
        databaseHost: $databaseHost
        databasePort: $databasePort
        databaseName: $databaseName
        databaseUser: $databaseUser
        databasePassword: $databasePassword
        databaseSslEnabled: $databaseSslEnabled
        serverIp: $serverIp
        serverPort: $serverPort
        appVersion: $appVersion
      """
  }
}

object CarpetaCiudadanaConfig {
  val DatabaseEndpoint = "database_endpoint"
  val DatabasePassword = "database_password"

  var config: Option[CarpetaCiudadanaConfig] = None

  def apply(
      databaseDriver: String,
      databaseHost: String,
      databasePort: Int,
      databaseName: String,
      databaseUser: String,
      databasePassword: String,
      databaseSslEnabled: Boolean,
      serverIp: String,
      serverPort: Int,
      appVersion: String
  ): CarpetaCiudadanaConfig =
    new CarpetaCiudadanaConfig(
      databaseDriver,
      databaseHost,
      databasePort,
      databaseName,
      databaseUser,
      databasePassword,
      databaseSslEnabled,
      serverIp,
      serverPort,
      appVersion
    )
}
