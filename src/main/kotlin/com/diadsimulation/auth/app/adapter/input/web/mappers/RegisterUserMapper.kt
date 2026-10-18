package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.RegisterUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.RegisterUserResponse
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.valueobject.Cpf
import com.diadsimulation.auth.core.domain.valueobject.Email
import com.diadsimulation.auth.core.domain.valueobject.Login
import com.diadsimulation.auth.core.domain.valueobject.Phone
import com.diadsimulation.auth.core.domain.valueobject.Rg

object RegisterUserMapper {
    fun toDomain(request: RegisterUserRequest): User =
        User(
            email = Email(request.email),
            name = request.name,
            login = Login(request.login),
            passwordHash = request.password,
            role = CANDIDATE,
            status = ACTIVE,
            phone = normalizeOptional(request.phone)?.let(::Phone),
            address = normalizeOptional(request.address),
            cpf = normalizeOptional(request.cpf)?.let(::Cpf),
            rg = normalizeOptional(request.rg)?.let(::Rg),
            birthDate = request.birthDate,
        )

    fun toResponse(user: User): RegisterUserResponse =
        RegisterUserResponse(
            userId = user.userId,
            name = user.name,
            email = user.email.value,
            login = user.login.value,
            role = user.role,
            status = user.status,
            phone = user.phone?.value,
            address = user.address,
            cpf = user.cpf?.value,
            rg = user.rg?.value,
            birthDate = user.birthDate,
            createdAt = user.createdAt,
        )

    private fun normalizeOptional(value: String?): String? = value?.takeIf { it.isNotBlank() }
}
