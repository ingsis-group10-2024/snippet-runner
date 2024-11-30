package ingsis.runner.runner.service

import ingsis.runner.runner.model.dto.ExecutionResponse
import ingsis.runner.runner.model.dto.RuleDTO
import ingsis.runner.runner.model.dto.format.FormatResponse
import ingsis.runner.runner.model.dto.lint.ValidationResponse
import ingsis.runner.runner.model.enums.RuleTypeEnum
import ingsis.runner.runner.redis.model.SnippetToValidate
import ingsis.runner.runner.service.common.FormatService
import ingsis.runner.runner.service.common.InterpreterService
import ingsis.runner.runner.service.common.ParserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class RunnerService(
    @Autowired private val parserService: ParserService,
    @Autowired private val formatterService: FormatService,
    @Autowired private val interpreterService: InterpreterService,
    @Autowired private val restTemplate: RestTemplate,
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
        authorizationHeader: String,
    ): ValidationResponse {
        val lintingRules = getRules(userId = userId, ruleTypeEnum = RuleTypeEnum.LINT, authorizationHeader = authorizationHeader)
        val validationResponse = parserService.validateSnippet(name, content, version, lintingRules)
        return validationResponse
    }

    private fun getRules(
        userId: String,
        ruleTypeEnum: RuleTypeEnum,
        authorizationHeader: String,
    ): List<RuleDTO> {
        logger.info("Getting rules for user: $userId and rule type: $ruleTypeEnum")
        val url: String =
            if (ruleTypeEnum == RuleTypeEnum.FORMAT) {
                logger.info("Calling rule manager to get format rules")
                "http://snippet-manager:8080/manager/rules/format"
            } else {
                logger.info("Calling rule manager to get lint rules")
                "http://snippet-manager:8080/manager/rules/lint"
            }
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("Authorization", authorizationHeader)
        headers.add("Content-Type", "application/json")
        val requestEntity = HttpEntity(null, headers)
        val response: ResponseEntity<List<RuleDTO>> =
            restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                object : ParameterizedTypeReference<List<RuleDTO>>() {},
            )
        return response.body ?: emptyList()
    }

    fun validateOrFormatSnippet(snippetToValidate: SnippetToValidate) {
        logger.info("Consuming the message to validate or format snippet for rule type: ${snippetToValidate.ruleType}")
        logger.info("Snippet: $snippetToValidate")
        val ruleType = snippetToValidate.ruleType

        if (ruleType == RuleTypeEnum.FORMAT.name) {
            formatSnippet(
                content = snippetToValidate.content,
                version = snippetToValidate.language,
                userId = snippetToValidate.authorId,
                authorizationHeader = snippetToValidate.authorizationHeader,
            )
        } else {
            lintSnippet(
                name = snippetToValidate.name,
                content = snippetToValidate.content,
                version = snippetToValidate.language,
                userId = snippetToValidate.authorId,
                authorizationHeader = snippetToValidate.authorizationHeader,
            )
        }
    }

    fun formatSnippet(
        content: String,
        version: String,
        userId: String,
        authorizationHeader: String,
    ): FormatResponse {
        logger.info("Formatting snippet with version: $version and content: $content")
        val formattingRules = getRules(userId = userId, ruleTypeEnum = RuleTypeEnum.FORMAT, authorizationHeader = authorizationHeader)
        val astNodes = parserService.parse(content, version)
        val formatterResponse = formatterService.format(astNodes, formattingRules)
        return formatterResponse
    }
}
