package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.RegisterUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.RegisterUserResponse
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.valueobject.Email
import com.diadsimulation.auth.core.domain.valueobject.Phone

object RegisterUserMapper {
    fun toDomain(request: RegisterUserRequest): User =
        User(
            email = Email(request.email),
            name = request.name,
            passwordHash = request.password,
            role = request.role,
            status = ACTIVE,
            phone = normalizeOptional(request.phone)?.let(::Phone),
            address = normalizeOptional(request.address),
            cpf = normalizeOptional(request.cpf),
            rg = normalizeOptional(request.rg),
            birthDate = request.birthDate,
        )

    fun toResponse(user: User): RegisterUserResponse =
        RegisterUserResponse(
            userId = user.userId,
            name = user.name,
            email = user.email.value,
            role = user.role,
            status = user.status,
            phone = user.phone?.value,
            address = user.address,
            cpf = user.cpf,
            rg = user.rg,
            birthDate = user.birthDate,
            createdAt = user.createdAt,
        )

    private fun normalizeOptional(value: String?): String? = value?.takeIf { it.isNotBlank() }
}
