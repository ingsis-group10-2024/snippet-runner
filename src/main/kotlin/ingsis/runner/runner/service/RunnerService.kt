package ingsis.runner.runner.service

import ingsis.runner.runner.model.dto.ExecutionResponse
import ingsis.runner.runner.model.dto.format.FormatResponse
import ingsis.runner.runner.model.dto.lint.ValidationResponse
import ingsis.runner.runner.model.enums.RuleTypeEnum
import ingsis.runner.runner.redis.model.SnippetsValidationMessage
import ingsis.runner.runner.service.common.FormatService
import ingsis.runner.runner.service.common.InterpreterService
import ingsis.runner.runner.service.common.ParserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RunnerService(
    @Autowired private val parserService: ParserService,
    @Autowired private val formatterService: FormatService,
    @Autowired private val interpreterService: InterpreterService,
    @Autowired private val ruleService: RuleService,
) {

    private val logger: Logger = LoggerFactory.getLogger(RunnerService::class.java)

    fun executeSnippet(
        content: String,
        version: String,
    ): ExecutionResponse {
        logger.info("Executing snippet with version: $version and content: $content")
        val astNodes = parserService.parse(content, version)
        val executionResponse = interpreterService.execute(astNodes, version)
        logger.info("Snippet executed successfully.")
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
                formatSnippet(
                    content = snippet.content,
                    version = snippet.language,
                    userId = snippet.userId,
                )
            }
            return
        }
        snippets.forEach { snippet ->
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
        logger.info("Formatting snippet with version: $version and content: $content")
        val formattingRules = ruleService.getRules(userId = userId, ruleType = RuleTypeEnum.FORMAT)
        val astNodes = parserService.parse(content, version)
        val formatterResponse = formatterService.format(astNodes, formattingRules)
        return formatterResponse
    }
}
