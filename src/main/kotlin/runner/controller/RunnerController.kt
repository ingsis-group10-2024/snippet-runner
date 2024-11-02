package runner.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import runner.model.dto.ExecutionResponse
import runner.model.dto.FormatResponse
import runner.model.dto.SnippetProcessResponse
import runner.model.dto.SnippetRequest
import runner.model.dto.ValidationResponse
import runner.service.RunnerService
import org.springframework.security.core.Authentication
import java.security.Principal


@RestController
@RequestMapping("/runner")
class RunnerController(
    @Autowired private val runnerService: RunnerService,
) {
    @PostMapping("/process")
    fun processSnippet(
        @RequestBody snippetRequest: SnippetRequest,
    ): ResponseEntity<SnippetProcessResponse> {
        val content = snippetRequest.content

        val executeResult = runnerService.executeSnippet(content, version = snippetRequest.languageVersion)
        val lintResult = runnerService.lintSnippet(snippetRequest.name, content, version = snippetRequest.languageVersion)
        val formatResult = runnerService.formatSnippet(content, version = snippetRequest.languageVersion)

        val response =
            SnippetProcessResponse(
                executeResult = executeResult,
                lintResult = lintResult,
                formatResult = formatResult,
            )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/execute")
    fun executeSnippet(
        @RequestBody snippetRequest: SnippetRequest,
    ): ResponseEntity<ExecutionResponse> {
        val content = snippetRequest.content
        val executeResult = runnerService.executeSnippet(content, version = snippetRequest.languageVersion)
        return ResponseEntity.ok(executeResult)
    }

    @PostMapping("/lint")
    fun lintSnippet(
        @RequestBody snippetRequest: SnippetRequest
    ): ResponseEntity<ValidationResponse> {
        println("Linting snippet...")
        println("Name: ${snippetRequest.name}, Content: ${snippetRequest.content}, Language: ${snippetRequest.language}, Language Version: ${snippetRequest.languageVersion}")
        val lintResult = runnerService.lintSnippet(snippetRequest.name, snippetRequest.content, version = snippetRequest.languageVersion)
        return ResponseEntity.ok(lintResult)
    }

    @PostMapping("/format")
    fun formatSnippet(
        @RequestBody snippetRequest: SnippetRequest,
    ): ResponseEntity<FormatResponse> {
        val content = snippetRequest.content
        val formatResult = runnerService.formatSnippet(content, version = snippetRequest.languageVersion)
        return ResponseEntity.ok(formatResult)
    }
}
