package com.diadsimulation.auth.app.adapter.input.web.exception

import com.diadsimulation.auth.core.common.Messages.UNAUTHORIZED_ACCESS
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.nio.charset.StandardCharsets.UTF_8

@Component
class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        response.status = UNAUTHORIZED.value()
        response.contentType = APPLICATION_JSON_VALUE
        response.characterEncoding = UTF_8.name()

        objectMapper.writeValue(
            response.outputStream,
            ApiErrorResponse(
                status = UNAUTHORIZED.value(),
                error = UNAUTHORIZED.name,
                message = UNAUTHORIZED_ACCESS,
                path = request.requestURI,
            ),
        )
    }
}
