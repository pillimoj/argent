package argent.util

open class ApiException(val clientMessage: String,val logMessage: String, val statusCode: Int): Exception(logMessage)

class MissingParametersException private constructor(missingParametersString: String) : ApiException(missingParametersString, missingParametersString, 400){
    constructor(missingParameters: List<String>):this("Missing parameters: $missingParameters")
}

class InternalServerError(message: String) : ApiException("Internal server error", message, 500)
class BadRequestException(clientMessage: String) : ApiException(clientMessage, clientMessage, 400)
class NotFoundException(clientMessage: String) : ApiException(clientMessage, clientMessage, 404)
class MethodNotAllowedException() : ApiException("method not allowed", "method not allowed", 405)
