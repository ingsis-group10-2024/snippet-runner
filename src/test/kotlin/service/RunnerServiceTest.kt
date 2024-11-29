package service

import ingsis.runner.runner.model.dto.RuleDTO
import ingsis.runner.runner.model.dto.format.FormatResponse
import ingsis.runner.runner.model.dto.lint.ValidationResponse
import ingsis.runner.runner.model.enums.RuleTypeEnum
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
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RunnerServiceTest {
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
        runnerService =
            RunnerService(
                parserService,
                formatterService,
                interpreterService,
                ruleService,
            )
    }

    @Test
    fun `lintSnippet should retrieve rules and validate snippet`() {
        // Arrange
        val name = "testSnippet"
        val content = "some code"
        val version = "1.0"
        val userId = "user123"
        val lintingRules = listOf(mock<RuleDTO>())
        val expectedValidationResponse = ValidationResponse(name, isValid = true, content, emptyList())

        whenever(ruleService.getRules(userId, RuleTypeEnum.LINT)).thenReturn(lintingRules)
        whenever(parserService.validateSnippet(name, content, version, lintingRules)).thenReturn(expectedValidationResponse)

        // Act
        val result = runnerService.lintSnippet(name, content, version, userId)

        // Assert
        assertEquals(expectedValidationResponse, result)
        verify(ruleService).getRules(userId, RuleTypeEnum.LINT)
        verify(parserService).validateSnippet(name, content, version, lintingRules)
    }

    @Test
    fun `formatSnippet should retrieve rules, parse, and format snippet`() {
        // Arrange
        val content = "some code"
        val version = "1.0"
        val userId = "user123"
        val formattingRules = listOf(mock<RuleDTO>())
        val mockAstNodes = parserService.parse(content, version)
        val expectedFormatResponse = FormatResponse(formattedContent = "formatted code")

        whenever(ruleService.getRules(userId, RuleTypeEnum.FORMAT)).thenReturn(formattingRules)
        whenever(formatterService.format(mockAstNodes, formattingRules)).thenReturn(expectedFormatResponse)

        // Act
        val result = runnerService.formatSnippet(content, version, userId)

        // Assert
        assertEquals(expectedFormatResponse, result)
        verify(ruleService).getRules(userId, RuleTypeEnum.FORMAT)
        verify(formatterService).format(mockAstNodes, formattingRules)
    }
}
