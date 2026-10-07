package com.diadsimulation.auth.app.adapter.input.web.exception

import com.diadsimulation.auth.core.domain.exceptions.CpfAlreadyExistsException
import com.diadsimulation.auth.core.domain.exceptions.EmailAlreadyExistsException
import com.diadsimulation.auth.core.domain.exceptions.RgAlreadyExistsException
import jakarta.servlet.http.HttpServletRequest
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

    @ExceptionHandler(
        EmailAlreadyExistsException::class,
        CpfAlreadyExistsException::class,
        RgAlreadyExistsException::class
    )
    fun handleConflict(
        exception: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val status = CONFLICT

        val response = ApiErrorResponse(
            status = status.value(),
            error = status.name,
            message = exception.message ?: "Conflito ao processar a requisição",
            path = request.requestURI
        )

        return ResponseEntity
            .status(status)
            .body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val status = BAD_REQUEST
        val fieldErrors = exception.bindingResult.fieldErrors

        val message = if (fieldErrors.size > 0) {
            fieldErrors[0].defaultMessage ?: "Dados da requisição inválidos"
        } else {
            "Dados da requisição inválidos"
        }

        val response = ApiErrorResponse(
            status = status.value(),
            error = status.name,
            message = message,
            path = request.requestURI
        )

        return ResponseEntity
            .status(status)
            .body(response)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(
        exception: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val status = BAD_REQUEST

        val response = ApiErrorResponse(
            status = status.value(),
            error = status.name,
            message = "Corpo da requisição inválido",
            path = request.requestURI
        )

        return ResponseEntity
            .status(status)
            .body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(
        exception: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val status = INTERNAL_SERVER_ERROR

        val response = ApiErrorResponse(
            status = status.value(),
            error = status.name,
            message = "Erro interno do servidor",
            path = request.requestURI
        )

        return ResponseEntity
            .status(status)
            .body(response)
    }
}