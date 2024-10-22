package language.service

import language.model.dto.ExecutionResponse
import language.model.dto.FormatResponse
import language.model.dto.ValidationResponse
import org.springframework.stereotype.Service

@Service
class LanguageService(
    private val parserService: ParserService,
) {
    fun validateSnippet(
        content: String,
        version: String,
    ): ValidationResponse = parserService.validateSnippet(content, version)

    fun executeSnippet(
        content: String,
        version: String,
    ): ExecutionResponse {
        // Lógica de ejecución del snippet
        val output = "Resultado de la ejecución" // Placeholder
        return ExecutionResponse(output, true)
    }

    fun formatSnippet(
        content: String,
        version: String,
    ): FormatResponse {
        // Lógica de formateo del snippet según las reglas del lenguaje y versión
        val formattedContent = content // Este es un ejemplo simple
        return FormatResponse(formattedContent)
    }
}
