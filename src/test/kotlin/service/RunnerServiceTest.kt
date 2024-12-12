package service

import ingsis.runner.runner.service.RunnerService
import ingsis.runner.runner.service.common.FormatService
import ingsis.runner.runner.service.common.InterpreterService
import ingsis.runner.runner.service.common.ParserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.client.RestTemplate

class RunnerServiceTest {
    @Mock
    private lateinit var parserService: ParserService

    @Mock
    private lateinit var formatterService: FormatService

    @Mock
    private lateinit var interpreterService: InterpreterService

    @Mock
    private lateinit var restTemplate: RestTemplate

    private val authHeader = "Bearer token"

    private lateinit var runnerService: RunnerService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        runnerService =
            RunnerService(
                parserService,
                formatterService,
                interpreterService,
                restTemplate,
            )
    }

    @Test
    fun executeSnippet_invalidContent_throwsException() {
        val content = "invalid content"
        val version = "1.0"

        whenever(parserService.parse(content, version)).thenThrow(RuntimeException("Parsing error"))

        val exception =
            assertThrows<RuntimeException> {
                runnerService.executeSnippet(content, version)
            }

        assertEquals("Parsing error", exception.message)
        verify(parserService).parse(content, version)
    }
}
