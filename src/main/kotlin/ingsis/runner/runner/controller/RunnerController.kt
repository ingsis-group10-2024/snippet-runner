package ingsis.runner.runner.controller

import ingsis.runner.runner.model.dto.ExecuteRequest
import ingsis.runner.runner.model.dto.ExecutionResponse
import ingsis.runner.runner.model.dto.SnippetRequest
import ingsis.runner.runner.model.dto.format.FormatRequest
import ingsis.runner.runner.model.dto.format.FormatResponse
import ingsis.runner.runner.model.dto.lint.ValidationResponse
import ingsis.runner.runner.service.RunnerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/runner")
class RunnerController(
    @Autowired private val runnerService: RunnerService,
) {
    @PostMapping("/execute")
    fun executeSnippet(
        @RequestBody executeRequest: ExecuteRequest,
    ): ResponseEntity<ExecutionResponse> {
        println("Executing snippet...")
        val executeResult = runnerService.executeSnippet(content = executeRequest.content, version = executeRequest.languageVersion)
        println("Execution result: $executeResult")
        return ResponseEntity.ok(executeResult)
    }

    @PostMapping("/lint")
    fun lintSnippet(
        @RequestBody snippetRequest: SnippetRequest,
        principal: Principal,
        @RequestHeader("Authorization") authorizationHeader: String,
    ): ResponseEntity<ValidationResponse> {
        println("Linting snippet...")
        println(
            "Name: ${snippetRequest.name}, Content: ${snippetRequest.content}, Language: ${snippetRequest.language}, Language Version: ${snippetRequest.languageVersion}",
        )
        val lintResult =
            runnerService.lintSnippet(
                name = snippetRequest.name,
                content = snippetRequest.content,
                version = snippetRequest.languageVersion,
                userId = principal.name,
                authorizationHeader = authorizationHeader
            )
        return ResponseEntity.ok(lintResult)
    }

    @PostMapping("/format")
    fun formatSnippet(
        @RequestBody formatRequest: FormatRequest,
        principal: Principal,
        @RequestHeader("Authorization") authorizationHeader: String,
    ): ResponseEntity<FormatResponse> {
        val content = formatRequest.content
        val formatResult =
            formatRequest.languageVersion?.let { runnerService.formatSnippet(
                content = content,
                version = it,
                userId = principal.name,
                authorizationHeader = authorizationHeader) }
        return ResponseEntity.ok(formatResult)
    }
}
