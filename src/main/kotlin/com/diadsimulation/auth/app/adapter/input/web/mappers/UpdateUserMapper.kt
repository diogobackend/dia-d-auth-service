package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.UpdateUserResponse
import com.diadsimulation.auth.core.domain.model.User

object UpdateUserMapper {
    fun toResponse(user: User): UpdateUserResponse =
        UpdateUserResponse(
            userId = user.userId,
            name = user.name,
            email = user.email.value,
            phone = user.phone?.value,
            address = user.address,
            cpf = user.cpf?.value,
            updatedAt = user.updatedAt,
        )
}
