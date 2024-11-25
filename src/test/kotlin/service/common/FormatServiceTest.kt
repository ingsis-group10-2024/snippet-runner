package service.common

import ast.ASTNode
import ingsis.runner.runner.model.dto.RuleDTO
import ingsis.runner.runner.service.RuleService
import ingsis.runner.runner.service.RunnerService
import ingsis.runner.runner.service.common.FormatService
import ingsis.runner.runner.service.common.InterpreterService
import ingsis.runner.runner.service.common.ParserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class FormatServiceTest {
    @Mock
    private lateinit var parserService: ParserService

    @Mock
    private lateinit var formatterService: FormatService

    @Mock
    private lateinit var interpreterService: InterpreterService

    @Mock
    private lateinit var ruleService: RuleService

    private lateinit var runnerService: RunnerService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        parserService = mock(ParserService::class.java)
        formatterService = FormatService()
    }

    @Test
    fun formatStringWithValidInput() {
        val astNodes = parserService.parse("println('wololo')       ;", "1.0")
        `when`(parserService.parse("println('wololo')       ;", "1.0")).thenReturn(astNodes)
        val rule = RuleDTO(id = "ruleId", name = "TestRule", isActive = true, value = "testValue")

        val result = formatterService.format(astNodes, listOf(rule)).formattedContent
        assertEquals("", result)
    }

    @Test
    fun formatStringWithEmptyRules() {
        val astNodes = parserService.parse("wololo", "1.0")
        `when`(parserService.parse("wololo", "1.0")).thenReturn(astNodes)

        val result = formatterService.format(astNodes, emptyList()).formattedContent
        assertEquals("", result)
    }

    @Test
    fun formatStringWithEmptyAstNodes() {
        val astNodes = emptyList<ASTNode>()
        `when`(parserService.parse("", "1.0")).thenReturn(astNodes)
        val rule = RuleDTO(id = "ruleId", name = "TestRule", isActive = true, value = "testValue")

        val result = formatterService.format(astNodes, listOf(rule)).formattedContent
        assertEquals("", result)
    }

    @Test
    fun formatStringWithNullAstNodes() {
        val astNodes: List<ASTNode>? = null
        `when`(parserService.parse("wololo", "1.0")).thenReturn(astNodes)
        val rule = RuleDTO(id = "ruleId", name = "TestRule", isActive = true, value = "testValue")

        val result = formatterService.format(astNodes ?: emptyList(), listOf(rule)).formattedContent
        assertEquals("", result)
    }
}
