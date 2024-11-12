package runner.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import runner.model.dto.ExecutionResponse
import runner.model.dto.FormatResponse
import runner.model.dto.SnippetProcessResponse
import runner.model.dto.SnippetRequest
import runner.model.dto.ValidationResponse
import runner.service.RunnerService
import java.security.Principal

@RestController
@RequestMapping("/runner")
class RunnerController(
    @Autowired private val runnerService: RunnerService,
) {
    @PostMapping("/process")
    fun processSnippet(
        @RequestBody snippetRequest: SnippetRequest,
        @RequestHeader("Authorization") authorizationHeader: String,
        ): ResponseEntity<SnippetProcessResponse> {
        val content = snippetRequest.content

        val executeResult = runnerService.executeSnippet(content, version = snippetRequest.languageVersion)
        val lintResult = runnerService.lintSnippet(snippetRequest.name, content, version = snippetRequest.languageVersion, authorizationHeader)
        val formatResult = runnerService.formatSnippet(content, version = snippetRequest.languageVersion, authorizationHeader)

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
        println("Executing snippet...")
        val executeResult = runnerService.executeSnippet(content = snippetRequest.content, version = snippetRequest.languageVersion)
        println("Execution result: $executeResult")
        return ResponseEntity.ok(executeResult)
    }

    @PostMapping("/lint")
    fun lintSnippet(
        @RequestBody snippetRequest: SnippetRequest,
        @RequestHeader("Authorization") authorizationHeader: String,
        ): ResponseEntity<ValidationResponse> {
        println("Linting snippet...")
        println(
            "Name: ${snippetRequest.name}, Content: ${snippetRequest.content}, Language: ${snippetRequest.language}, Language Version: ${snippetRequest.languageVersion}",
        )
        val lintResult = runnerService.lintSnippet(snippetRequest.name, snippetRequest.content, version = snippetRequest.languageVersion, authorizationHeader)
        return ResponseEntity.ok(lintResult)
    }

    @PreAuthorize("hasAuthority('create:snippet')")
    @PostMapping("/format")
    fun formatSnippet(
        @RequestBody snippetRequest: SnippetRequest,
        @RequestHeader("Authorization") authorizationHeader: String,
    ): ResponseEntity<FormatResponse> {
        val content = snippetRequest.content
        val formatResult = runnerService.formatSnippet(content, version = snippetRequest.languageVersion, authorizationHeader)
        return ResponseEntity.ok(formatResult)
    }
}
