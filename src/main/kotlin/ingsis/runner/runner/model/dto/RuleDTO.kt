package ingsis.runner.runner.model.dto

data class RuleDTO(
    val id: String?,
    val name: String,
    val isActive: Boolean,
    val value: String? = null,
)
