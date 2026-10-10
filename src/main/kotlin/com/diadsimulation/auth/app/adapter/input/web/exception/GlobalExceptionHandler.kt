package com.diadsimulation.auth.app.adapter.input.web.exception

import com.diadsimulation.auth.core.domain.exceptions.UserAlreadyExistsException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleConflict(
        exception: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> =
        buildResponse(
            status = CONFLICT,
            message = exception.message ?: "Conflito ao processar a requisição",
            request = request
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val message = exception.bindingResult.fieldErrors
            .firstOrNull()
            ?.defaultMessage
            ?: "Dados da requisição inválidos"

        return buildResponse(
            status = BAD_REQUEST,
            message = message,
            request = request
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(
        exception: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> =
        buildResponse(
            status = BAD_REQUEST,
            message = "Corpo da requisição inválido",
            request = request
        )

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(
        exception: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> =
        buildResponse(
            status = INTERNAL_SERVER_ERROR,
            message = "Erro interno do servidor",
            request = request
        )

    private fun buildResponse(
        status: HttpStatus,
        message: String,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(status).body(
            ApiErrorResponse(
                status = status.value(),
                error = status.name,
                message = message,
                path = request.requestURI
            )
        )
}