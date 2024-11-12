package runner.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import runner.model.dto.ExecutionResponse
import runner.model.dto.FormatResponse
import runner.model.dto.RuleDto
import runner.model.dto.ValidationResponse
import runner.service.common.FormatService
import runner.service.common.InterpreterService
import runner.service.common.ParserService

@Service
class RunnerService(
    @Autowired private val parserService: ParserService,
    @Autowired private val formatterService: FormatService,
    @Autowired private val interpreterService: InterpreterService,
    @Autowired private val restTemplate: RestTemplate
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
        authorizationHeader: String,
        ): ValidationResponse {
        val lintingRules = getUserRules(authorizationHeader, "http://snippet-permission:8080/rules/lint")
        val validationResponse = parserService.validateSnippet(name, content, version, lintingRules)
        return validationResponse
    }

    fun formatSnippet(
        content: String,
        version: String,
        authorizationHeader: String,
        ): FormatResponse {
        val formattingRules = getUserRules(authorizationHeader, "http://snippet-permission:8080/rules/format")
        val astNodes = parserService.parse(content, version)
        val formatterResponse = formatterService.format(astNodes, formattingRules)
        return formatterResponse
    }

    // Method to make a REST call to get user's linting&formatting rules
    private fun getUserRules(authorizationHeader: String, url: String): List<RuleDto> {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("Authorization", authorizationHeader)
        headers.add("Content-Type", "application/json")

        val requestEntity = HttpEntity(null, headers)

        val response: ResponseEntity<List<RuleDto>> =
            restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                object : ParameterizedTypeReference<List<RuleDto>>() {}
            )
        return response.body ?: emptyList()

    }
}
