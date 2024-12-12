package controller

import ingsis.runner.runner.controller.RunnerController
import ingsis.runner.runner.model.dto.ExecuteRequest
import ingsis.runner.runner.model.dto.ExecutionResponse
import ingsis.runner.runner.model.dto.SnippetRequest
import ingsis.runner.runner.model.dto.format.FormatRequest
import ingsis.runner.runner.model.dto.format.FormatResponse
import ingsis.runner.runner.model.dto.lint.ValidationResponse
import ingsis.runner.runner.service.RunnerService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.ResponseEntity
import sca.StaticCodeAnalyzerError
import java.security.Principal

@ExtendWith(MockitoExtension::class)
class RunnerControllerTest {
    @Mock
    private lateinit var runnerService: RunnerService

    @Mock
    private lateinit var principal: Principal

    @InjectMocks
    private lateinit var runnerController: RunnerController

    private val authHeader = "Bearer token"

    @Test
    fun `execute snippet with empty content`() {
        val executeRequest = ExecuteRequest("", "3.9")
        val expectedResponse = ExecutionResponse(emptyList(), listOf("Error: No content to execute"))
        whenever(runnerService.executeSnippet("", "3.9")).thenReturn(expectedResponse)

        val response = runnerController.executeSnippet(executeRequest)

        assertEquals(ResponseEntity.ok(expectedResponse), response)
        verify(runnerService).executeSnippet("", "3.9")
    }

    @Test
    fun `lint snippet with invalid language version`() {
        whenever(principal.name).thenReturn("testUser")
        val snippetRequest = SnippetRequest("test", "code", "python", "invalid_version")
        val expectedResponse = ValidationResponse("test", false, "code", listOf(StaticCodeAnalyzerError("Error: Invalid language version")))
        whenever(runnerService.lintSnippet("test", "code", "invalid_version", "testUser", authHeader)).thenReturn(expectedResponse)

        val response = runnerController.lintSnippet(snippetRequest, principal, authHeader)

        assertEquals(ResponseEntity.ok(expectedResponse), response)
        verify(runnerService).lintSnippet("test", "code", "invalid_version", "testUser", authHeader)
    }

    @Test
    fun `execute snippet with valid content`() {
        val executeRequest = ExecuteRequest("print('Hello, World!')", "3.9")
        val expectedResponse = ExecutionResponse(listOf("Hello, World!"), emptyList())
        whenever(runnerService.executeSnippet("print('Hello, World!')", "3.9")).thenReturn(expectedResponse)

        val response = runnerController.executeSnippet(executeRequest)

        assertEquals(ResponseEntity.ok(expectedResponse), response)
        verify(runnerService).executeSnippet("print('Hello, World!')", "3.9")
    }

    @Test
    fun `format snippet with valid content and version`() {
        whenever(principal.name).thenReturn("testUser")
        val formatRequest = FormatRequest("code", "3.9")
        val expectedResponse = FormatResponse("formatted code")
        whenever(runnerService.formatSnippet("code", "3.9", "testUser", authHeader)).thenReturn(expectedResponse)

        val response = runnerController.formatSnippet(formatRequest, principal, authHeader)

        assertEquals(ResponseEntity.ok(expectedResponse), response)
        verify(runnerService).formatSnippet("code", "3.9", "testUser", authHeader)
    }
}
