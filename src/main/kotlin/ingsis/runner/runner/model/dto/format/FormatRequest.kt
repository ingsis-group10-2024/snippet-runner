package ingsis.runner.runner.model.dto.format

data class FormatRequest(
    val content: String,
    // Default value is "1.1"
    val languageVersion: String? = "1.1",
)
