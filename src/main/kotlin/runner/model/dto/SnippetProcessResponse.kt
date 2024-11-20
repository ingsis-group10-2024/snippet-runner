package runner.model.dto

import runner.model.dto.format.FormatResponse
import runner.model.dto.lint.ValidationResponse

data class SnippetProcessResponse(
    val executeResult: ExecutionResponse,
    val lintResult: ValidationResponse,
    val formatResult: FormatResponse,
)
