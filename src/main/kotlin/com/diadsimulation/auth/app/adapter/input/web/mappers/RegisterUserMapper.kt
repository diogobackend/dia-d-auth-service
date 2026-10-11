package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.RegisterUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.RegisterUserResponse
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import com.diadsimulation.auth.core.domain.model.User

object RegisterUserMapper {
    fun toDomain(request: RegisterUserRequest): User =
        User(
            email = request.email,
            name = request.name,
            passwordHash = request.password,
            role = request.role,
            status = ACTIVE,
            phone = normalizeOptional(request.phone),
            address = normalizeOptional(request.address),
            cpf = normalizeOptional(request.cpf),
            rg = normalizeOptional(request.rg),
            birthDate = request.birthDate,
        )

    fun toResponse(user: User): RegisterUserResponse =
        RegisterUserResponse(
            userId = user.userId,
            name = user.name,
            email = user.email,
            role = user.role,
            status = user.status,
            phone = user.phone,
            address = user.address,
            cpf = user.cpf,
            rg = user.rg,
            birthDate = user.birthDate,
            createdAt = user.createdAt,
        )

    private fun normalizeOptional(value: String?): String? {
        if (value == null) {
            return null
        }

        if (value.length == 0) {
            return null
        }

        return value
    }
}
