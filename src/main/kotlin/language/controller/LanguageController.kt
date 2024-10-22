package language.controller

import language.model.dto.*
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
    @PostMapping("/process")
    fun processSnippet(
        @RequestBody snippetRequest: SnippetRequest,
    ): ResponseEntity<SnippetProcessResponse> {
        val content = snippetRequest.content

        val executeResult = languageService.executeSnippet(content, version = snippetRequest.languageVersion)
        val lintResult = languageService.lintSnippet(content, version = snippetRequest.languageVersion)
        val formatResult = languageService.formatSnippet(content, version = snippetRequest.languageVersion)

        val response = SnippetProcessResponse(
            executeResult = executeResult,
            lintResult = lintResult,
            formatResult = formatResult
        )

        return ResponseEntity.ok(response)
    }
}
