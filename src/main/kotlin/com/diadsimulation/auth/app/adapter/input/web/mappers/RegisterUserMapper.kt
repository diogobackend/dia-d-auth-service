package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.app.adapter.input.web.dtos.RegisterUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.RegisterUserResponse
import com.diadsimulation.auth.core.domain.enums.UserStatus
import com.diadsimulation.auth.core.domain.model.User

object RegisterUserMapper {

    fun toDomain(request: RegisterUserRequest): User =
        User(
            email = request.email,
            passwordHash = request.password,
            role = request.role,
            status = UserStatus.ACTIVE,
            phone = request.phone,
            address = request.address,
            cpf = request.cpf,
            rg = request.rg
        )

    fun toResponse(user: User): RegisterUserResponse =
        RegisterUserResponse(
            userId = user.userId,
            email = user.email,
            role = user.role,
            status = user.status,
            phone = user.phone,
            address = user.address,
            cpf = user.cpf,
            rg = user.rg,
            createdAt = user.createdAt
        )
}