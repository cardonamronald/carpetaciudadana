package co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.service
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.Documento
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.JsonSupport
import com.azure.core.util.Context
import com.azure.storage.blob._
import com.azure.storage.blob.models.PublicAccessType
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.io.FilenameUtils

object AzureStorageService extends JsonSupport {
  private val conf: Config     = ConfigFactory.load
  private val connectionString = conf.getString("azure.connection")
  private val blobServiceClient: BlobServiceClient =
    new BlobServiceClientBuilder().connectionString(connectionString).buildClient

  def uploadFile(documento: Documento, tmpPath: String): Documento = {
    val client: BlobClient = getOrCreateContainer(documento).getBlobClient(
      s"${documento.titulo}.${FilenameUtils.getExtension(tmpPath)}")
    client.uploadFromFile(tmpPath, true)
    documento.copy(url = client.getBlobUrl)
  }

  private def getOrCreateContainer(documento: Documento): BlobContainerClient =
    if (blobServiceClient.getBlobContainerClient(documento.idCiudadano.toString).exists())
      blobServiceClient.getBlobContainerClient(documento.idCiudadano.toString)
    else
      blobServiceClient
        .createBlobContainerWithResponse(documento.idCiudadano.toString,
                                         null,
                                         PublicAccessType.BLOB,
                                         Context.NONE)
        .getValue
}
