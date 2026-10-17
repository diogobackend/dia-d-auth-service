package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.ListUsersResponse
import com.diadsimulation.auth.core.domain.model.User

object ListUsersMapper {
    fun toResponse(user: User): ListUsersResponse =
        ListUsersResponse(
            userId = user.userId,
            name = user.name,
            email = user.email.value,
            login = user.login.value,
            role = user.role,
            status = user.status,
            phone = user.phone?.value,
            cpf = user.cpf?.value,
            rg = user.rg?.value,
            address = user.address,
            birthDate = user.birthDate,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
        )
}
