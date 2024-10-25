package language.model.dto

import org.jetbrains.annotations.NotNull

data class SnippetRequest(
    @NotNull
    val content: String,
    @NotNull
    val languageVersion: String,
)
