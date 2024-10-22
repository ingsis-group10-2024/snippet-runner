package language.controller

import language.model.dto.ExecutionResponse
import language.model.dto.FormatResponse
import language.model.dto.SnippetRequest
import language.model.dto.ValidationResponse
import language.service.LanguageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/language")
class LanguageController(
    @Autowired private val languageService: LanguageService,
) {
    @PostMapping("/validate")
    fun validateSnippet(@RequestBody snippetRequest: SnippetRequest): ResponseEntity<ValidationResponse> {
        val response = languageService.validateSnippet(snippetRequest.content, snippetRequest.languageVersion)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/execute")
    fun executeSnippet(
        @RequestBody snippetRequest: SnippetRequest,
    ): ResponseEntity<ExecutionResponse> {
        val response = languageService.executeSnippet(snippetRequest.content, snippetRequest.languageVersion)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/format")
    fun formatSnippet(
        @RequestBody snippetRequest: SnippetRequest,
    ): ResponseEntity<FormatResponse> {
        val response = languageService.formatSnippet(snippetRequest.content, snippetRequest.languageVersion)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/process")
    fun processSnippet(
        @RequestBody snippetRequest: SnippetRequest,
    ): ResponseEntity<SnippetResponse> {
        val content = snippetRequest.content

        val executeResult = languageService.executeSnippet(content)
        val lintResult = languageService.lintSnippet(content)
        val formatResult = languageService.formatSnippet(content)


        val executionResponse = languageService.executeSnippet(snippetRequest.content, snippetRequest.languageVersion)
        return ResponseEntity.ok(executionResponse)
    }
}
