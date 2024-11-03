package runner.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import runner.model.dto.ExecutionResponse
import runner.model.dto.FormatResponse
import runner.model.dto.ValidationResponse
import runner.service.common.FormatService
import runner.service.common.InterpreterService
import runner.service.common.ParserService

@Service
class RunnerService(
    @Autowired private val parserService: ParserService,
    @Autowired private val formatterService: FormatService,
    @Autowired private val interpreterService: InterpreterService,
) {
    fun executeSnippet(
        content: String,
        version: String,
    ): ExecutionResponse {
        val astNodes = parserService.parse(content, version)
        val executionResponse = interpreterService.execute(astNodes, version)
        return executionResponse
    }

    fun lintSnippet(
        name: String,
        content: String,
        version: String,
    ): ValidationResponse {
        val validationResponse = parserService.validateSnippet(name, content, version)
        return validationResponse
    }

    fun formatSnippet(
        content: String,
        version: String,
    ): FormatResponse {
        val astNodes = parserService.parse(content, version)
        val formatterResponse = formatterService.format(astNodes)
        return formatterResponse
    }
}
