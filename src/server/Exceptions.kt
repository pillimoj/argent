package argent.server

open class ApiException(val clientMessage: String, val logMessage: String, val statusCode: Int) : Exception(logMessage)

class MissingParametersException private constructor(missingParametersString: String) :
    ApiException(missingParametersString, missingParametersString, 400) {
    constructor(missingParameters: List<String>) : this("Missing parameters: $missingParameters")
}

class InternalServerError(message: String) : ApiException("Internal server error", message, 500)
class BadRequestException(clientMessage: String = "Bad Request", logMessage: String? = null) : ApiException(clientMessage, logMessage ?: clientMessage, 400)
class NotFoundException(clientMessage: String = "Not found", logMessage: String? = null) : ApiException(clientMessage, logMessage ?: clientMessage, 404)
class MethodNotAllowedException() : ApiException("method not allowed", "method not allowed", 405)
