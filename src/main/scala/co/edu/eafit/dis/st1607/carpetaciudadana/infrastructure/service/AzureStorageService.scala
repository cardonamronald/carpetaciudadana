package co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.service
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.Documento
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.JsonSupport
import com.azure.storage.blob._
import com.typesafe.config.{Config, ConfigFactory}

object AzureStorageService extends App with JsonSupport {
  private val conf: Config     = ConfigFactory.load
  private val connectionString = conf.getString("azure.connection")

  private val blobServiceClient: BlobServiceClient =
    new BlobServiceClientBuilder().connectionString(connectionString).buildClient

  def uploadFile(documento: Documento, tmpPath: String): Documento = {
    val blobContainerClient =
      blobServiceClient.createBlobContainer(documento.idCiudadano.toString)
    println("CONTAINER CREADOOOO")
    val client = blobContainerClient.getBlobClient(tmpPath)
    client.uploadFromFile(tmpPath, false)
    println("DONEEEEE <3")
    documento.copy(url = client.getBlobUrl)
  }

  val result = uploadFile(Documento("id", 1036785540, "", "cedula", false),
    "/Users/ronald/Desktop/scala-spiral.png")

  println(result.idCiudadano, result.id, result.url, result.autenticado, result.titulo)
}
