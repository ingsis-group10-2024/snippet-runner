package language.model.dto

data class ExecutionResponse(
    val output: List<String>,
    val errors: List<String>,
)
