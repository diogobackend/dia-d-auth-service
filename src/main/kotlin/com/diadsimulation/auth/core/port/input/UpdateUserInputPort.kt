package com.diadsimulation.auth.core.port.input

import com.diadsimulation.auth.core.domain.model.User
import java.time.LocalDate
import java.util.UUID

interface UpdateUserInputPort {
    fun execute(
        userId: UUID,
        name: String?,
        email: String?,
        login: String?,
        password: String?,
        phone: String?,
        address: String?,
        cpf: String?,
        rg: String?,
        birthDate: LocalDate?,
    ): User
}
