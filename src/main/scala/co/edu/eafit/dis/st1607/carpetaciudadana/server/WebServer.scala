package co.edu.eafit.dis.st1607.carpetaciudadana.server

import akka.Done
import akka.actor.ActorSystem
import akka.event.{LogSource, Logging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.RoutingSettings
import akka.stream.ActorMaterializer
import co.edu.eafit.dis.st1607.carpetaciudadana.config.CarpetaCiudadanaConfig
import co.edu.eafit.dis.st1607.carpetaciudadana.routes.{
  CarpetaCiudadanaRoutes,
  ConfigRoutes,
  RootRoutes
}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object WebServer {

  def apply(): WebServer = new WebServer()

  def loadConfiguration(): CarpetaCiudadanaConfig = {
    val conf = ConfigFactory.load()

    val databaseDriver     = getConfigurationString("ssra.database.driver", conf)
    val databaseHost       = getConfigurationString("ssra.database.host", conf)
    val databasePort       = getConfigurationInt("ssra.database.port", conf)
    val databaseName       = getConfigurationString("ssra.database.name", conf)
    val databaseUser       = getConfigurationString("ssra.database.user", conf)
    val databasePassword   = getConfigurationString("ssra.database.password", conf)
    val databaseSslEnabled = getConfigurationBoolean("ssra.database.ssl_enabled", conf)
    val serverIp           = getConfigurationString("ssra.server.ip", conf)
    val serverPort         = getConfigurationInt("ssra.server.port", conf)

    CarpetaCiudadanaConfig(
      databaseDriver,
      databaseHost,
      databasePort,
      databaseName,
      databaseUser,
      databasePassword,
      databaseSslEnabled,
      serverIp,
      serverPort
    )
  }

  def getConfigurationString(configName: String, conf: Config): String =
    Try(conf.getString(configName)) match {
      case Success(s) => s
      case Failure(e) => throw e
    }

  def getConfigurationInt(configName: String, conf: Config): Int =
    Try(conf.getInt(configName)) match {
      case Success(s) => s
      case Failure(e) => throw e
    }

  def getConfigurationBoolean(configName: String, conf: Config): Boolean =
    Try(conf.getBoolean(configName)) match {
      case Success(s) => s
      case Failure(e) => throw e
    }

  implicit val logSource: LogSource[AnyRef] = new LogSource[AnyRef] {
    def genString(o: AnyRef): String           = o.getClass.getName
    override def getClazz(o: AnyRef): Class[_] = o.getClass
  }
}

class WebServer {
  implicit val actorSystem: ActorSystem =
    ActorSystem("SingleRequest", ConfigFactory.load().getConfig("ssra.web-server-pool"))
  implicit val mat: ActorMaterializer             = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  implicit val rs: RoutingSettings = RoutingSettings(actorSystem)

  val log = Logging(actorSystem, this)

  var (carpetaCiudadanaConfig, routes, bindingFuture) = start()

  def restart() {
    log.debug("Server restart")
    unbind()
    ConfigFactory.invalidateCaches()
    val (s, r, b) = start()
    this.carpetaCiudadanaConfig = s
    this.routes = r
    this.bindingFuture = b
  }

  def unbind(): Future[Done] = {
    log.debug("Server unbind")
    bindingFuture.flatMap(_.unbind())
  }

  def start(): Tuple3[CarpetaCiudadanaConfig, Route, Future[ServerBinding]] = {
    log.debug("Server started")
    var carpetaCiudadanaConfig = WebServer.loadConfiguration()
    var routes =
      ConfigRoutes.routes(carpetaCiudadanaConfig, this) ~
        CarpetaCiudadanaRoutes.routes(carpetaCiudadanaConfig) ~
        RootRoutes.routes
    var binding = Http()
      .newServerAt(carpetaCiudadanaConfig.serverIp, carpetaCiudadanaConfig.serverPort)
      .bindFlow(routes)

    (carpetaCiudadanaConfig, routes, binding)
  }

  def shutdown(): Unit = {
    log.debug("Server shutdown")
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => actorSystem.terminate()) // and shutdown when done
  }

}
