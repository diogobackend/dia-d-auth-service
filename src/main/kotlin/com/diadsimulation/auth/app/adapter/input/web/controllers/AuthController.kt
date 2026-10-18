package com.diadsimulation.auth.app.adapter.input.web.controllers

import AuthApi
import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.LoginRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.LoginResponse
import com.diadsimulation.auth.core.port.input.AuthenticateUserInputPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class AuthController(
    private val authenticateUserInputPort: AuthenticateUserInputPort,
) : AuthApi {
    @PostMapping
    override fun login(request: LoginRequest): ResponseEntity<LoginResponse> {
        val token =
            authenticateUserInputPort.execute(
                email = request.email,
                password = request.password,
                login = request.login,
            )

        return ResponseEntity.ok(
            LoginResponse(
                accessToken = token,
                tokenType = "Bearer",
            ),
        )
    }
}
