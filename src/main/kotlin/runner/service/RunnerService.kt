package runner.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import runner.model.dto.ExecutionResponse
import runner.model.dto.format.FormatResponse
import runner.model.dto.lint.ValidationResponse
import runner.model.enums.RuleTypeEnum
import runner.redis.model.SnippetsValidationMessage
import runner.service.common.FormatService
import runner.service.common.InterpreterService
import runner.service.common.ParserService

@Service
class RunnerService(
    @Autowired private val parserService: ParserService,
    @Autowired private val formatterService: FormatService,
    @Autowired private val interpreterService: InterpreterService,
    @Autowired private val ruleService: RuleService,
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
        userId: String,
    ): ValidationResponse {
        val lintingRules = ruleService.getRules(userId = userId, ruleType = RuleTypeEnum.LINT)
        val validationResponse = parserService.validateSnippet(name, content, version, lintingRules)
        return validationResponse
    }

    fun validateOrFormatSnippets(snippetsValidationMessage: SnippetsValidationMessage) {
        val snippets = snippetsValidationMessage.snippets
        val ruleType = snippetsValidationMessage.ruleType

        if (ruleType == RuleTypeEnum.FORMAT.name) {
            snippets.forEach { snippet ->
                println("Formatting snippet with ID: ${snippet.id}, Language: ${snippet.language}")

                formatSnippet(
                    content = snippet.content,
                    version = snippet.language,
                    userId = snippet.userId,
                )
            }
            return
        }
        snippets.forEach { snippet ->
            println("Validating snippet with ID: ${snippet.id}, Language: ${snippet.language}")

            lintSnippet(
                name = snippet.name,
                content = snippet.content,
                version = snippet.language,
                userId = snippet.userId,
            )
        }
    }

    fun formatSnippet(
        content: String,
        version: String,
        userId: String,
    ): FormatResponse {
        val formattingRules = ruleService.getRules(userId = userId, ruleType = RuleTypeEnum.FORMAT)
        val astNodes = parserService.parse(content, version)
        val formatterResponse = formatterService.format(astNodes, formattingRules)
        return formatterResponse
    }
}
