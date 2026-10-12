package com.diadsimulation.auth.core.port.input

import com.diadsimulation.auth.core.domain.model.User
import java.util.UUID

interface FindUserUseCase {
    fun execute(
        userId: UUID?,
        email: String?,
        cpf: String?,
        rg: String?,
    ): User
}
