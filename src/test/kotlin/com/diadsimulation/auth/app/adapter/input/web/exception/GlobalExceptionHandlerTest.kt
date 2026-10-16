package com.diadsimulation.auth.app.adapter.input.web.exception

import com.diadsimulation.auth.builders.buildFieldError
import com.diadsimulation.auth.core.common.Messages.ACCESS_DENIED
import com.diadsimulation.auth.core.common.Messages.BIRTH_DATE_INVALID_FORMAT
import com.diadsimulation.auth.core.common.Messages.EMAIL_REQUIRED
import com.diadsimulation.auth.core.common.Messages.FILTER_PARAMETERS_REQUIRED
import com.diadsimulation.auth.core.common.Messages.INTERNAL_SERVER_ERROR_MESSAGE
import com.diadsimulation.auth.core.common.Messages.INVALID_CREDENTIALS
import com.diadsimulation.auth.core.common.Messages.INVALID_REQUEST_DATA
import com.diadsimulation.auth.core.common.Messages.NAME_REQUIRED
import com.diadsimulation.auth.core.common.Messages.REGISTER_USER_REQUEST
import com.diadsimulation.auth.core.common.Messages.REQUEST_CONFLICT
import com.diadsimulation.auth.core.common.Messages.SENSITIVE_DATABASE_INFORMATION
import com.diadsimulation.auth.core.common.Messages.USER_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.USER_NOT_ACTIVE
import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.domain.exceptions.UserAlreadyExistsException
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.hibernate.internal.util.collections.CollectionHelper.listOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.mock.http.MockHttpInputMessage
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import tools.jackson.databind.exc.InvalidFormatException
import java.util.NoSuchElementException
import java.util.stream.Stream

class GlobalExceptionHandlerTest {
    private val handler = GlobalExceptionHandler()

    private val request =
        mockk<HttpServletRequest> {
            every { requestURI } returns "/users"
        }

    @ParameterizedTest
    @MethodSource("conflictProvider")
    fun `should return conflict for existing user data`(message: String) {
        val response =
            handler.handleConflict(
                UserAlreadyExistsException(message),
                request,
            )

        assertError(response, CONFLICT, message)
    }

    @Test
    fun `should return default conflict message when exception message is null`() {
        val response =
            handler.handleConflict(
                RuntimeException(),
                request,
            )

        assertError(response, CONFLICT, REQUEST_CONFLICT)
    }

    @ParameterizedTest
    @MethodSource("validationProvider")
    fun `should handle field validation errors`(
        field: String,
        validationMessage: String?,
        expectedMessage: String,
    ) {
        val exception =
            validationException(
                buildFieldError(
                    field = field,
                    message = validationMessage,
                ),
            )

        val response = handler.handleValidation(exception, request)

        assertError(response, BAD_REQUEST, expectedMessage)
    }

    @Test
    fun `should return default message when no field errors exist`() {
        val response =
            handler.handleValidation(
                validationException(),
                request,
            )

        assertError(response, BAD_REQUEST, INVALID_REQUEST_DATA)
    }

    @ParameterizedTest
    @MethodSource("illegalArgumentProvider")
    fun `should handle illegal argument exceptions`(
        message: String?,
        expectedMessage: String,
    ) {
        val response =
            handler.handleIllegalArgument(
                IllegalArgumentException(message),
                request,
            )

        assertError(response, BAD_REQUEST, expectedMessage)
    }

    @Test
    fun `should return bad request when request body is invalid`() {
        val response =
            handler.handleMessageNotReadable(
                unreadableException(INVALID_REQUEST_DATA),
                request,
            )

        assertError(response, BAD_REQUEST, INVALID_REQUEST_DATA)
    }

    @Test
    fun `should return name required when unreadable exception references name`() {
        val exception =
            unreadableException(
                message = INVALID_REQUEST_DATA,
                cause = IllegalArgumentException("Invalid property: name"),
            )

        val response = handler.handleMessageNotReadable(exception, request)

        assertError(response, BAD_REQUEST, NAME_REQUIRED)
    }

    @Test
    fun `should return default message when unreadable exception has unrelated cause`() {
        val exception =
            unreadableException(
                message = INVALID_REQUEST_DATA,
                cause = IllegalArgumentException("Invalid property: email"),
            )

        val response = handler.handleMessageNotReadable(exception, request)

        assertError(response, BAD_REQUEST, INVALID_REQUEST_DATA)
    }

    @Test
    fun `should return invalid birth date format when jackson error references birthDate`() {
        val jacksonException = mockk<InvalidFormatException>()

        every { jacksonException.path } returns
            listOf(
                mockk {
                    every { propertyName } returns "birthDate"
                },
            )

        val exception =
            unreadableException(
                message = INVALID_REQUEST_DATA,
                cause = jacksonException,
            )

        val response = handler.handleMessageNotReadable(exception, request)

        assertError(
            response,
            BAD_REQUEST,
            BIRTH_DATE_INVALID_FORMAT,
        )
    }

    @Test
    fun `should return default message when jackson error references another field`() {
        val jacksonException = mockk<InvalidFormatException>()

        every { jacksonException.cause } returns null
        every { jacksonException.path } returns
            listOf(
                mockk {
                    every { propertyName } returns "email"
                },
            )
        every { jacksonException.message } returns INVALID_REQUEST_DATA

        val exception =
            unreadableException(
                message = INVALID_REQUEST_DATA,
                cause = jacksonException,
            )

        val response = handler.handleMessageNotReadable(exception, request)

        assertError(
            response,
            BAD_REQUEST,
            INVALID_REQUEST_DATA,
        )
    }

    @Test
    fun `should return internal server error without exposing exception details`() {
        val response =
            handler.handleUnexpectedException(
                IllegalStateException(SENSITIVE_DATABASE_INFORMATION),
                request,
            )

        assertError(
            response,
            INTERNAL_SERVER_ERROR,
            INTERNAL_SERVER_ERROR_MESSAGE,
        )
    }

    @Test
    fun `should return default message when exception cause message is null`() {
        val exception =
            unreadableException(
                message = INVALID_REQUEST_DATA,
                cause = IllegalArgumentException(),
            )

        val response = handler.handleMessageNotReadable(exception, request)

        assertError(
            response,
            BAD_REQUEST,
            INVALID_REQUEST_DATA,
        )
    }

    @ParameterizedTest
    @MethodSource("notFoundProvider")
    fun `should return not found when user does not exist`(
        message: String?,
        expectedMessage: String,
    ) {
        val response =
            handler.handleNotFound(
                NoSuchElementException(message),
                request,
            )

        assertError(response, NOT_FOUND, expectedMessage)
    }

    @ParameterizedTest
    @MethodSource("badCredentialsProvider")
    fun `should return unauthorized for invalid credentials`(
        message: String?,
        expectedMessage: String,
    ) {
        val response =
            handler.handleBadCredentials(
                BadCredentialsException(message),
                request,
            )

        assertError(response, UNAUTHORIZED, expectedMessage)
    }

    @ParameterizedTest
    @MethodSource("illegalStateProvider")
    fun `should return forbidden for illegal state`(
        message: String?,
        expectedMessage: String,
    ) {
        val response =
            handler.handleIllegalState(
                IllegalStateException(message),
                request,
            )

        assertError(response, FORBIDDEN, expectedMessage)
    }

    @ParameterizedTest
    @MethodSource("accessDeniedProvider")
    fun `should return forbidden for access denied`(
        message: String?,
        expectedMessage: String,
    ) {
        val exception =
            mockk<AccessDeniedException> {
                every { this@mockk.message } returns message
            }

        val response =
            handler.handleAccessDenied(
                exception,
                request,
            )

        assertError(response, FORBIDDEN, expectedMessage)
    }

    private fun unreadableException(
        message: String,
        cause: Throwable? = null,
    ): HttpMessageNotReadableException {
        val input = MockHttpInputMessage(ByteArray(0))

        return if (cause == null) {
            HttpMessageNotReadableException(message, input)
        } else {
            HttpMessageNotReadableException(message, cause, input)
        }
    }

    private fun validationException(vararg errors: FieldError): MethodArgumentNotValidException {
        val bindingResult =
            BeanPropertyBindingResult(
                Any(),
                REGISTER_USER_REQUEST,
            )

        errors.forEach(bindingResult::addError)

        val method =
            TestController::class.java.getDeclaredMethod(
                "register",
                String::class.java,
            )

        return MethodArgumentNotValidException(
            MethodParameter(method, 0),
            bindingResult,
        )
    }

    private fun assertError(
        response: ResponseEntity<ApiErrorResponse>,
        expectedStatus: HttpStatus,
        expectedMessage: String,
    ) {
        val body = response.body

        assertNotNull(body)
        assertEquals(expectedStatus.value(), response.statusCode.value())
        assertEquals(expectedStatus.value(), body?.status)
        assertEquals(expectedStatus.name, body?.error)
        assertEquals(expectedMessage, body?.message)
        assertEquals("/users", body?.path)
        assertNotNull(body?.timestamp)
    }

    private class TestController {
        fun register(body: String) = body
    }

    companion object {
        @JvmStatic
        fun conflictProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of("${USER_ALREADY_EXISTS}e-mail: candidate@diadsimulation.com"),
                Arguments.of("${USER_ALREADY_EXISTS}CPF: 123.456.789-00"),
                Arguments.of("${USER_ALREADY_EXISTS}RG: 123456789"),
            )

        @JvmStatic
        fun validationProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of("email", EMAIL_REQUIRED, EMAIL_REQUIRED),
                Arguments.of("email", null, INVALID_REQUEST_DATA),
            )

        @JvmStatic
        fun illegalArgumentProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(FILTER_PARAMETERS_REQUIRED, FILTER_PARAMETERS_REQUIRED),
                Arguments.of(null, INVALID_REQUEST_DATA),
            )

        @JvmStatic
        fun notFoundProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(USER_NOT_FOUND, USER_NOT_FOUND),
                Arguments.of(null, USER_NOT_FOUND),
            )

        @JvmStatic
        fun badCredentialsProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(INVALID_CREDENTIALS, INVALID_CREDENTIALS),
                Arguments.of(null, INVALID_CREDENTIALS),
            )

        @JvmStatic
        fun illegalStateProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(USER_NOT_ACTIVE, USER_NOT_ACTIVE),
                Arguments.of(null, ACCESS_DENIED),
            )

        @JvmStatic
        fun accessDeniedProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(ACCESS_DENIED, ACCESS_DENIED),
                Arguments.of(null, ACCESS_DENIED),
            )
    }
}
