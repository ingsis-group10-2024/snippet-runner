package ingsis.runner.runner.model.dto

import ingsis.runner.runner.model.dto.format.FormatResponse
import ingsis.runner.runner.model.dto.lint.ValidationResponse

data class SnippetProcessResponse(
    val executeResult: ExecutionResponse,
    val lintResult: ValidationResponse,
    val formatResult: FormatResponse,
)
