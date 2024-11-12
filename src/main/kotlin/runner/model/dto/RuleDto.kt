package runner.model.dto

data class RuleDto(
    val id: String?,
    val name: String,
    val isActive: Boolean,
    val value: String? = null,
)
