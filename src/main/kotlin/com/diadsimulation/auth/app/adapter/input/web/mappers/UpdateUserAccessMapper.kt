package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.UpdateUserAccessUserResponse
import com.diadsimulation.auth.core.domain.model.User

object UpdateUserAccessMapper {
    fun toResponse(user: User): UpdateUserAccessUserResponse =
        UpdateUserAccessUserResponse(
            userId = user.userId,
            name = user.name,
            email = user.email.value,
            role = user.role,
            status = user.status,
            birthDate = user.birthDate,
            updatedAt = user.updatedAt,
        )
}
