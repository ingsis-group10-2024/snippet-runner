package runner.model.dto.lint

import sca.StaticCodeAnalyzerError

data class ValidationResponse(
    val name: String,
    val isValid: Boolean,
    val content: String,
    val errors: List<StaticCodeAnalyzerError>?,
)
