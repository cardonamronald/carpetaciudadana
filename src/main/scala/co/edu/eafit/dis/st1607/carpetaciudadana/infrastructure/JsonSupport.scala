package co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.error.{AppError, DatabaseError, HttpError}
import co.edu.eafit.dis.st1607.carpetaciudadana.domain.model.{Ciudadano, Documento}
import co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.dto.{CiudadanoDTO, DocumentoDTO, RegistroCiudadanoDTO}
import spray.json.{DefaultJsonProtocol, JsValue, _}


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  // DTO
  implicit val ciudadanoDTOFormat = jsonFormat4(CiudadanoDTO.apply)
  implicit val documentoDTOFormat = jsonFormat2(DocumentoDTO.apply)
  implicit val registroCiudadanoDTO = jsonFormat6(RegistroCiudadanoDTO.apply)

  // Domain
  implicit val documentoFormat = jsonFormat5(Documento.apply)
  implicit val ciudadanoFormat = jsonFormat6(Ciudadano.apply)

  // Errors
  implicit val databaseErrorFormat = jsonFormat1(DatabaseError)
  implicit val httpErrorFortmat    = jsonFormat2(HttpError)

  implicit val appErrorFormat: RootJsonFormat[AppError] = new RootJsonFormat[AppError] {
    override def read(json: JsValue): AppError = json.asInstanceOf[AppError]

    override def write(obj: AppError): JsValue = obj match {
      case a: DatabaseError => a.toJson
      case b: HttpError     => b.toJson
    }
  }
}
