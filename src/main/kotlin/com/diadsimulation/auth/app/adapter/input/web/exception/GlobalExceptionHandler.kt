package com.diadsimulation.auth.app.adapter.input.web.exception

import com.diadsimulation.auth.core.common.Messages.ACCESS_DENIED
import com.diadsimulation.auth.core.common.Messages.BIRTH_DATE_INVALID_FORMAT
import com.diadsimulation.auth.core.common.Messages.INTERNAL_SERVER_ERROR_MESSAGE
import com.diadsimulation.auth.core.common.Messages.INVALID_CREDENTIALS
import com.diadsimulation.auth.core.common.Messages.INVALID_REQUEST_DATA
import com.diadsimulation.auth.core.common.Messages.NAME_REQUIRED
import com.diadsimulation.auth.core.common.Messages.REQUEST_CONFLICT
import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.domain.exceptions.UserAlreadyExistsException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import tools.jackson.databind.exc.InvalidFormatException
import java.util.NoSuchElementException

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
    ): ResponseEntity<ApiErrorResponse> {
        val isBirthDateError =
            generateSequence(exception as Throwable?) { it.cause }
                .filterIsInstance<InvalidFormatException>()
                .any { mappingException ->
                    mappingException.path.any { it.propertyName == "birthDate" }
                }

        val message =
            when {
                isBirthDateError -> BIRTH_DATE_INVALID_FORMAT
                exception.mostSpecificCause.message?.contains("name") == true -> NAME_REQUIRED
                else -> INVALID_REQUEST_DATA
            }

        return buildResponse(
            status = BAD_REQUEST,
            message = message,
            request = request,
        )
    }

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

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        exception: IllegalArgumentException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> =
        buildResponse(
            status = BAD_REQUEST,
            message = exception.message ?: INVALID_REQUEST_DATA,
            request = request,
        )

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(
        exception: NoSuchElementException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> =
        buildResponse(
            status = NOT_FOUND,
            message = exception.message ?: USER_NOT_FOUND,
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

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(
        exception: BadCredentialsException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> =
        buildResponse(
            status = UNAUTHORIZED,
            message = exception.message ?: INVALID_CREDENTIALS,
            request = request,
        )

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(
        exception: IllegalStateException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> =
        buildResponse(
            status = FORBIDDEN,
            message = exception.message ?: ACCESS_DENIED,
            request = request,
        )

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(
        exception: AccessDeniedException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiErrorResponse> =
        buildResponse(
            status = FORBIDDEN,
            message = exception.message ?: ACCESS_DENIED,
            request = request,
        )
}
