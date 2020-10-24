package co.edu.eafit.dis.st1607.carpetaciudadana.domain.model

case class Ciudadano(
    id: Int,
    name: String,
    address: String,
    email: String,
    valido: Boolean,
    documentos: List[Documento]
)

object Ciudadano {}


