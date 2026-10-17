package com.diadsimulation.auth.core.port.input

import com.diadsimulation.auth.core.domain.model.User
import java.util.UUID

interface FindUserInputPort {
    fun execute(
        userId: UUID?,
        email: String?,
        login: String?,
        cpf: String?,
        rg: String?,
    ): User
}
