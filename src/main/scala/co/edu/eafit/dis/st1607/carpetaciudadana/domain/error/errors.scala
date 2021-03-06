package co.edu.eafit.dis.st1607.carpetaciudadana.domain.error

trait AppError

// Domain Errors

sealed trait DomainErrorMessage extends AppError

case class CiudadanoNoValido(message: String) extends DomainErrorMessage

case class CiudadanoYaExiste(message: String) extends DomainErrorMessage

// Infrastructure Errors

sealed trait InfrastructureErrorMessage extends AppError

case class HttpError(message: String, statusCode: Int) extends InfrastructureErrorMessage

case class DatabaseError(message: String, trace: String) extends InfrastructureErrorMessage

case class CouldNotUploadFile(message: String) extends InfrastructureErrorMessage
