package runner.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(InvalidSnippetException::class)
    fun handleInvalidSnippetException(
        ex: InvalidSnippetException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorDetails =
            ErrorResponse(
                message = "Snippet is invalid",
                details = ex.errors, // List of StaticCodeAnalyzerErrors
            )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(
        ex: RuntimeException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorDetails =
            ErrorResponse(
                message = "Snippet processing failed",
                details = ex.message ?: "Internal server error",
            )
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

// Model for the error response
data class ErrorResponse(
    val message: String,
    val details: Any,
)
