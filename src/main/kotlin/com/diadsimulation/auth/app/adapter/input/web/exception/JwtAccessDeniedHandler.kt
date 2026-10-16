package com.diadsimulation.auth.app.adapter.input.web.exception

import com.diadsimulation.auth.core.common.Messages.ACCESS_DENIED
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.nio.charset.StandardCharsets.UTF_8

@Component
class JwtAccessDeniedHandler(
    private val objectMapper: ObjectMapper,
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        response.status = FORBIDDEN.value()
        response.contentType = APPLICATION_JSON_VALUE
        response.characterEncoding = UTF_8.name()

        objectMapper.writeValue(
            response.outputStream,
            ApiErrorResponse(
                status = FORBIDDEN.value(),
                error = FORBIDDEN.name,
                message = ACCESS_DENIED,
                path = request.requestURI,
            ),
        )
    }
}
