package runner.model.dto.format

data class FormatRequest(
val content: String,
val languageVersion: String? = "1.1"  // Default value
)