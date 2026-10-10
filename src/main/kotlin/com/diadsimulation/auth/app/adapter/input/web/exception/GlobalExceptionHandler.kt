package com.diadsimulation.auth.app.adapter.input.web.exception

import com.diadsimulation.auth.core.common.Messages.INTERNAL_SERVER_ERROR_MESSAGE
import com.diadsimulation.auth.core.common.Messages.INVALID_REQUEST_DATA
import com.diadsimulation.auth.core.common.Messages.REQUEST_CONFLICT
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
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> =
        buildResponse(
            status = CONFLICT,
            message = exception.message ?: REQUEST_CONFLICT,
            request = request,
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> {
        val message =
            exception.bindingResult.fieldErrors
                .firstOrNull()
                ?.defaultMessage
                ?: INVALID_REQUEST_DATA

        return buildResponse(
            status = BAD_REQUEST,
            message = message,
            request = request,
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(
        exception: HttpMessageNotReadableException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> =
        buildResponse(
            status = BAD_REQUEST,
            message = INVALID_REQUEST_DATA,
            request = request,
        )

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(
        exception: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> =
        buildResponse(
            status = INTERNAL_SERVER_ERROR,
            message = INTERNAL_SERVER_ERROR_MESSAGE,
            request = request,
        )

    private fun buildResponse(
        status: HttpStatus,
        message: String,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(status).body(
            ApiErrorResponse(
                status = status.value(),
                error = status.name,
                message = message,
                path = request.requestURI,
            ),
        )
}
