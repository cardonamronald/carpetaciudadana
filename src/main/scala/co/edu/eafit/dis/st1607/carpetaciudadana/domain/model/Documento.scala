package co.edu.eafit.dis.st1607.carpetaciudadana.domain.model

case class Documento(
    id: String,
    idCiudadano: Int,
    url: String,
    titulo: String,
    autenticado: Boolean
)

object Documento {}
