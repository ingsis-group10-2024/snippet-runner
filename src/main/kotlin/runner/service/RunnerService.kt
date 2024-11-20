package runner.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import runner.model.dto.ExecutionResponse
import runner.model.dto.format.FormatResponse
import runner.model.dto.lint.ValidationResponse
import runner.model.enums.RuleTypeEnum
import runner.service.common.FormatService
import runner.service.common.InterpreterService
import runner.service.common.ParserService
import java.security.Principal

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
        principal: Principal,
    ): ValidationResponse {
        val lintingRules = ruleService.getRules(userId = principal.name, ruleType = RuleTypeEnum.LINT)
        val validationResponse = parserService.validateSnippet(name, content, version, lintingRules)
        return validationResponse
    }

    fun formatSnippet(
        content: String,
        version: String,
        principal: Principal,
    ): FormatResponse {
        val formattingRules = ruleService.getRules(userId = principal.name, ruleType = RuleTypeEnum.FORMAT)
        val astNodes = parserService.parse(content, version)
        val formatterResponse = formatterService.format(astNodes, formattingRules)
        return formatterResponse
    }


}
