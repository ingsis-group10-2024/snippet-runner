package language.model.dto

data class SnippetProcessResponse(
    val executeResult: ExecutionResponse,
    val lintResult: ValidationResponse,
    val formatResult: FormatResponse,
)
