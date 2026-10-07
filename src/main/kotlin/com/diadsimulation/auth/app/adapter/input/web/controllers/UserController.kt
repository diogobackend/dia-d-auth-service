package com.diadsimulation.auth.app.adapter.input.web.controllers

import com.diadsimulation.auth.app.adapter.input.web.dtos.RegisterUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.RegisterUserResponse
import com.diadsimulation.auth.app.adapter.input.web.mappers.RegisterUserMapper
import com.diadsimulation.auth.app.adapter.input.web.swagger.UserApi
import com.diadsimulation.auth.core.port.input.RegisterUserUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val registerUserUseCase: RegisterUserUseCase
) : UserApi {

    @PostMapping
    override fun register(
        request: RegisterUserRequest
    ): ResponseEntity<RegisterUserResponse> {
        val user = RegisterUserMapper.toDomain(request)
        val registeredUser = registerUserUseCase.execute(user)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(RegisterUserMapper.toResponse(registeredUser))
    }
}