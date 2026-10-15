package com.diadsimulation.auth.core.port.input

import com.diadsimulation.auth.core.domain.model.User
import java.util.UUID

interface UpdateUserUseCase {
    fun execute(
        userId: UUID,
        name: String?,
        email: String?,
        password: String?,
        phone: String?,
        address: String?,
        cpf: String?,
        rg: String?,
    ): User
}
