package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.FindUserResponse
import com.diadsimulation.auth.core.domain.model.User

object FindUserMapper {
    fun toResponse(user: User): FindUserResponse =
        FindUserResponse(
            userId = user.userId,
            name = user.name,
            email = user.email.value,
            role = user.role.name,
            status = user.status.name,
            phone = user.phone,
            cpf = user.cpf.toString(),
            rg = user.rg,
            address = user.address,
            birthDate = user.birthDate,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
        )
}
