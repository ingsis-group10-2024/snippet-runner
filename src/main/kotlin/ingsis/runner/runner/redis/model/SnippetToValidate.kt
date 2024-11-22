package ingsis.runner.runner.redis.model

data class SnippetToValidate(
    val name: String,
    val userId: String,
    val id: String,
    val content: String,
    val language: String,
    val languageVersion: String,
)
