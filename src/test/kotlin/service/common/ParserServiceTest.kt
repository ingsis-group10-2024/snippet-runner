package service.common

import ast.ASTNode
import ingsis.runner.runner.model.dto.RuleDTO
import ingsis.runner.runner.service.common.ParserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.MockitoAnnotations
import kotlin.test.Test

class ParserServiceTest {
    private lateinit var parserService: ParserService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        parserService = ParserService()
    }

    @Test
    fun validateSnippet_validSnippet_returnsValidationResponse() {
        val name = "ValidSnippet"
        val content = "valid content;"
        val version = "1.0"
        val lintingRules = listOf<RuleDTO>()

        val response = parserService.validateSnippet(name, content, version, lintingRules)

        assertEquals(name, response.name)
        assertEquals(true, response.isValid)
        assertEquals(content, response.content)
        assertEquals(emptyList<String>(), response.errors)
    }

    @Test
    fun validateSnippet_invalidSnippet_throwsInvalidSnippetException() {
        val name = "InvalidSnippet"
        val content = "invalid content"
        val version = "1.0"
        val lintingRules = listOf<RuleDTO>()

        val exception =
            assertThrows<RuntimeException> {
                parserService.validateSnippet(name, content, version, lintingRules)
            }
    }

    @Test
    fun parse_emptyContent_returnsEmptyASTNodes() {
        val content = ""
        val version = "1.0"

        val astNodes = parserService.parse(content, version)

        assertEquals(emptyList<ASTNode>(), astNodes)
    }
}
