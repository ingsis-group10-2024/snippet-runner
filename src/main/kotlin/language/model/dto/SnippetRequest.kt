package language.model.dto

data class SnippetRequest(
    val content: String,
    val languageVersion: String,
)
