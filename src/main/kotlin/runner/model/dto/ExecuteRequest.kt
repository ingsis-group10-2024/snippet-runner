package runner.model.dto

import org.jetbrains.annotations.NotNull

data class ExecuteRequest(
    @NotNull
    val content: String,
    val languageVersion: String = "1.1",
)
