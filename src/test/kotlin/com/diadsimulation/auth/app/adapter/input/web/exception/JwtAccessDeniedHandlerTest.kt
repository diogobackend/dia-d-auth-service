package com.diadsimulation.auth.app.adapter.input.web.exception

import com.diadsimulation.auth.core.common.Messages.ACCESS_DENIED
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.AccessDeniedException
import tools.jackson.databind.ObjectMapper
import java.io.OutputStream
import java.nio.charset.StandardCharsets.UTF_8

@ExtendWith(MockKExtension::class)
class JwtAccessDeniedHandlerTest {
    @MockK
    private lateinit var objectMapper: ObjectMapper

    @MockK
    private lateinit var request: HttpServletRequest

    @MockK(relaxed = true)
    private lateinit var response: HttpServletResponse

    @InjectMockKs
    private lateinit var jwtAccessDeniedHandler: JwtAccessDeniedHandler

    private fun mockRequest(path: String) {
        every { request.requestURI } returns path
    }

    private fun mockObjectMapper() {
        every {
            objectMapper.writeValue(
                any<OutputStream>(),
                any<ApiErrorResponse>(),
            )
        } just Runs
    }

    private fun executeHandler() {
        jwtAccessDeniedHandler.handle(
            request = request,
            response = response,
            accessDeniedException = AccessDeniedException(ACCESS_DENIED),
        )
    }

    @ParameterizedTest
    @MethodSource("requestPaths")
    fun `should return forbidden response with correct error body`(path: String) {
        val outputStream = mockk<ServletOutputStream>()

        mockRequest(path)

        every { response.outputStream } returns outputStream
        mockObjectMapper()

        executeHandler()

        verify(exactly = 1) {
            response.status = FORBIDDEN_STATUS
            response.contentType = APPLICATION_JSON_VALUE
            response.characterEncoding = UTF_8.name()

            objectMapper.writeValue(
                outputStream,
                match<ApiErrorResponse> {
                    it.status == FORBIDDEN_STATUS &&
                        it.error == FORBIDDEN.name &&
                        it.message == ACCESS_DENIED &&
                        it.path == path
                },
            )
        }
    }

    @Test
    fun `should write error response to servlet output stream`() {
        val outputStream = mockk<ServletOutputStream>()

        mockRequest(ACCESS_DENIED_PATH)

        every { response.outputStream } returns outputStream
        mockObjectMapper()

        executeHandler()

        verify(exactly = 1) {
            response.outputStream
        }

        verify(exactly = 1) {
            objectMapper.writeValue(
                outputStream,
                any<ApiErrorResponse>(),
            )
        }
    }

    companion object {
        private const val ACCESS_DENIED_PATH = "/api/users/123"
        private const val ADMIN_PATH = "/api/admin"
        private const val FORBIDDEN_STATUS = 403

        @JvmStatic
        fun requestPaths() =
            listOf(
                ACCESS_DENIED_PATH,
                ADMIN_PATH,
            )
    }
}
