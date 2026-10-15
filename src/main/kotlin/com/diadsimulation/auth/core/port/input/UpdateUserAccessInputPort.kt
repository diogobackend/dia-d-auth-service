package com.diadsimulation.auth.core.port.input

import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserStatus
import com.diadsimulation.auth.core.domain.model.User
import java.util.UUID

interface UpdateUserAccessInputPort {
    fun execute(
        userId: UUID,
        role: UserRole?,
        status: UserStatus?,
    ): User
}
