package service.common

import ast.ASTNode
import ingsis.runner.runner.service.common.InterpreterService
import ingsis.runner.runner.service.common.ParserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mockito.MockitoAnnotations
import kotlin.test.Test

class InterpreterServiceTest {
    private lateinit var parserService: ParserService

    private lateinit var interpreterService: InterpreterService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        parserService = ParserService()
        interpreterService = InterpreterService()
    }

    @Test
    fun interpretValidInput() {
        val input = "println('wololo');"
        val expectedOutput = "wololo"
        val astNodes = parserService.parse(input, "1.0")
        val result = interpreterService.execute(astNodes, "1.0").output

        assertEquals(expectedOutput, result.get(0))
    }

    @Test
    fun interpretEmptyAstNodesReturnsEmptyOutput() {
        val astNodes = emptyList<ASTNode>()
        val result = interpreterService.execute(astNodes, "1.0").output

        assertEquals(0, result.size)
    }

    @Test
    fun interpretAstNodesWithMultipleOutputs() {
        val input = "println('hello'); println('world');"
        val expectedOutput = listOf("hello", "world")
        val astNodes = parserService.parse(input, "1.0")
        val result = interpreterService.execute(astNodes, "1.0").output

        assertEquals(expectedOutput, result)
    }

    @Test
    fun interpretAstNodesWithDifferentVersion() {
        val input = "println('version test');"
        val expectedOutput = "version test"
        val astNodes = parserService.parse(input, "1.1")
        val result = interpreterService.execute(astNodes, "1.1").output

        assertEquals(expectedOutput, result.get(0))
    }
}
