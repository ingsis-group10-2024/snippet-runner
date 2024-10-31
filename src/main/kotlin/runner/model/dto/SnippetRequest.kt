package runner.model.dto

import org.jetbrains.annotations.NotNull

data class SnippetRequest(
    @NotNull
    val content: String,
    @NotNull
    val language: String,
    val languageVersion: String,
)
