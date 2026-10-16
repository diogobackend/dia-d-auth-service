package com.diadsimulation.auth.app.adapter.input.web.exception

import com.diadsimulation.auth.core.common.Messages.UNAUTHORIZED_ACCESS
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
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.authentication.BadCredentialsException
import tools.jackson.databind.ObjectMapper
import java.io.OutputStream
import java.nio.charset.StandardCharsets.UTF_8

@ExtendWith(MockKExtension::class)
class JwtAuthenticationEntryPointTest {
    @MockK
    private lateinit var objectMapper: ObjectMapper

    @MockK
    private lateinit var request: HttpServletRequest

    @MockK(relaxed = true)
    private lateinit var response: HttpServletResponse

    @InjectMockKs
    private lateinit var jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint

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

    private fun executeEntryPoint() {
        jwtAuthenticationEntryPoint.commence(
            request = request,
            response = response,
            authException = BadCredentialsException(UNAUTHORIZED_ACCESS),
        )
    }

    @ParameterizedTest
    @MethodSource("requestPaths")
    fun `should return unauthorized response with correct error body`(path: String) {
        val outputStream = mockk<ServletOutputStream>()

        mockRequest(path)

        every { response.outputStream } returns outputStream
        mockObjectMapper()

        executeEntryPoint()

        verify(exactly = 1) {
            response.status = UNAUTHORIZED_STATUS
            response.contentType = APPLICATION_JSON_VALUE
            response.characterEncoding = UTF_8.name()

            objectMapper.writeValue(
                outputStream,
                match<ApiErrorResponse> {
                    it.status == UNAUTHORIZED_STATUS &&
                        it.error == UNAUTHORIZED.name &&
                        it.message == UNAUTHORIZED_ACCESS &&
                        it.path == path
                },
            )
        }
    }

    @Test
    fun `should write error response to servlet output stream`() {
        val outputStream = mockk<ServletOutputStream>()

        mockRequest(USERS_PATH)

        every { response.outputStream } returns outputStream
        mockObjectMapper()

        executeEntryPoint()

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
        private const val USERS_PATH = "/api/users/123"
        private const val ADMIN_PATH = "/api/admin"
        private const val UNAUTHORIZED_STATUS = 401

        @JvmStatic
        fun requestPaths() =
            listOf(
                USERS_PATH,
                ADMIN_PATH,
            )
    }
}
