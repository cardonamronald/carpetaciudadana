package co.edu.eafit.dis.st1607.carpetaciudadana.infrastructure.dto

case class RegistroCiudadanoDTO(id: Int,
                                name: String,
                                address: String,
                                email: String,
                                operatorId: Int = 0,
                                operatorName: String = "carpeta-ciudadana")

object RegistroCiudadanoDTO {}
