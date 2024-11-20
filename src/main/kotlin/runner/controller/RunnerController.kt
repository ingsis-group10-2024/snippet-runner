package runner.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import runner.model.dto.ExecutionResponse
import runner.model.dto.format.FormatResponse
import runner.model.dto.SnippetRequest
import runner.model.dto.lint.ValidationResponse
import runner.model.dto.format.FormatRequest
import runner.service.RunnerService
import java.security.Principal

@RestController
@RequestMapping("/runner")
class RunnerController(
    @Autowired private val runnerService: RunnerService,
) {

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
        principal: Principal,
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
                principal = principal,
            )
        return ResponseEntity.ok(lintResult)
    }

    @PostMapping("/format")
    fun formatSnippet(
        @RequestBody formatRequest: FormatRequest,
        principal: Principal,
    ): ResponseEntity<FormatResponse> {
        val content = formatRequest.content
        val formatResult =
            formatRequest.languageVersion?.let { runnerService.formatSnippet(content = content, version = it, principal = principal) }
        return ResponseEntity.ok(formatResult)
    }
}
