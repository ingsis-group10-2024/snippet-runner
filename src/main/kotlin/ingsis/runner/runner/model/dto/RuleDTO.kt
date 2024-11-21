package ingsis.runner.runner.model.dto

import ingsis.runner.runner.persistance.entity.Rule

data class RuleDTO(
    val id: String?,
    val name: String,
    val isActive: Boolean,
    val value: String? = null,
) {
    constructor(rule: Rule) : this(
        id = rule.id,
        name = rule.name,
        isActive = rule.isActive,
        value = rule.value,
    )
}
