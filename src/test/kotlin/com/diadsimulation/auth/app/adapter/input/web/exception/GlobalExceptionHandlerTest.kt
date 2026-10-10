package com.diadsimulation.auth.app.adapter.input.web.exception

import com.diadsimulation.auth.core.domain.exceptions.UserAlreadyExistsException
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.mock.http.MockHttpInputMessage
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    private val request = mockk<HttpServletRequest> {
        every { requestURI } returns "/users"
    }

    @Test
    fun `should return conflict when email already exists`() {
        val message = "Já existe um usuário cadastrado com o e-mail: candidate@diadsimulation.com"

        val response = handler.handleConflict(
            UserAlreadyExistsException(message),
            request
        )

        assertError(response, CONFLICT, message)
    }

    @Test
    fun `should return conflict when cpf already exists`() {
        val message = "Já existe um usuário cadastrado com o CPF: 123.456.789-00"

        val response = handler.handleConflict(
            UserAlreadyExistsException(message),
            request
        )

        assertError(response, CONFLICT, message)
    }

    @Test
    fun `should return conflict when rg already exists`() {
        val message = "Já existe um usuário cadastrado com o RG: 123456789"

        val response = handler.handleConflict(
            UserAlreadyExistsException(message),
            request
        )

        assertError(response, CONFLICT, message)
    }

    @Test
    fun `should return default conflict message when exception message is null`() {
        val response = handler.handleConflict(
            RuntimeException(),
            request
        )

        assertError(
            response,
            CONFLICT,
            "Conflito ao processar a requisição"
        )
    }

    @Test
    fun `should return bad request with field validation message`() {
        val exception = validationException(
            FieldError(
                "registerUserRequest",
                "email",
                "",
                false,
                null,
                null,
                "E-mail é obrigatório"
            )
        )

        val response = handler.handleValidation(exception, request)

        assertError(
            response,
            BAD_REQUEST,
            "E-mail é obrigatório"
        )
    }

    @Test
    fun `should return default message when field validation message is null`() {
        val exception = validationException(
            FieldError(
                "registerUserRequest",
                "email",
                null,
                false,
                null,
                null,
                null
            )
        )

        val response = handler.handleValidation(exception, request)

        assertError(
            response,
            BAD_REQUEST,
            "Dados da requisição inválidos"
        )
    }

    @Test
    fun `should return default message when no field errors exist`() {
        val exception = validationException()

        val response = handler.handleValidation(exception, request)

        assertError(
            response,
            BAD_REQUEST,
            "Dados da requisição inválidos"
        )
    }

    @Test
    fun `should return bad request when request body is invalid`() {
        val exception = HttpMessageNotReadableException(
            "Invalid JSON",
            MockHttpInputMessage(ByteArray(0))
        )

        val response = handler.handleMessageNotReadable(exception, request)

        assertError(
            response,
            BAD_REQUEST,
            "Corpo da requisição inválido"
        )
    }

    @Test
    fun `should return internal server error without exposing exception details`() {
        val exception = IllegalStateException(
            "Sensitive database information"
        )

        val response = handler.handleUnexpectedException(exception, request)

        assertError(
            response,
            INTERNAL_SERVER_ERROR,
            "Erro interno do servidor"
        )
    }

    private fun validationException(
        vararg errors: FieldError
    ): MethodArgumentNotValidException {
        val bindingResult = BeanPropertyBindingResult(
            Any(),
            "registerUserRequest"
        )

        errors.forEach(bindingResult::addError)

        val method = TestController::class.java.getDeclaredMethod(
            "register",
            String::class.java
        )

        return MethodArgumentNotValidException(
            MethodParameter(method, 0),
            bindingResult
        )
    }

    private fun assertError(
        response: ResponseEntity<ApiErrorResponse>,
        expectedStatus: HttpStatus,
        expectedMessage: String
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
}