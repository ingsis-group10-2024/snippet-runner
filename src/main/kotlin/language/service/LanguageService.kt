package language.service

import implementation.Formatter
import language.model.dto.ExecutionResponse
import language.model.dto.FormatResponse
import language.model.dto.ValidationResponse
import language.service.common.FormatService
import language.service.common.ParserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LanguageService(
    @Autowired private val parserService: ParserService,
    @Autowired private val formatterService: FormatService,
) {
    fun executeSnippet(content: String, version: String): ExecutionResponse {
        //TODO METER LA LOGICA DE EJECUTAR UN SNIPPET
        return ExecutionResponse("Success", true)
    }

    fun lintSnippet(content: String, version: String): ValidationResponse {
        val validationResponse = parserService.validateSnippet(content, version)
        return validationResponse
    }

    fun formatSnippet(content: String, version: String): FormatResponse{
        val astNodes = parserService.parse(content, version)
        val formatterResponse = formatterService.format(astNodes)
        return formatterResponse
    }
}
