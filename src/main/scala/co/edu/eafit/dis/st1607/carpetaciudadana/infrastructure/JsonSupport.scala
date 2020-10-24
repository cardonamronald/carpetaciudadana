package co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.{AppError, DatabaseError, HttpError}
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.{Ciudadano, Documento}
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.dto.CiudadanoDTO
import spray.json.{DefaultJsonProtocol, JsValue, _}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  // DTO
  implicit val ciudadanoDTOFormat = jsonFormat4(CiudadanoDTO.apply)

  // Domain
  implicit val documentoFormat = jsonFormat5(Documento.apply)
  implicit val ciudadanoFormat = jsonFormat6(Ciudadano.apply)

  // Errors
  implicit val databaseErrorFormat = jsonFormat1(DatabaseError)
  implicit val httpErrorFortmat    = jsonFormat2(HttpError)

  implicit val appErrorFormat: RootJsonFormat[AppError] = new RootJsonFormat[AppError] {
    override def read(json: JsValue): AppError = JsonReader[AppError]

    override def write(obj: AppError): JsValue = obj match {
      case a: DatabaseError => a.toJson
      case b: HttpError     => b.toJson
    }
  }
}
