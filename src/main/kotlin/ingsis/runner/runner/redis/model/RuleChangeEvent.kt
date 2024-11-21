package ingsis.runner.runner.redis.model

data class RuleChangeEvent(
    val ruleType: String,
    val userId: String,
    val timestamp: Long,
)
