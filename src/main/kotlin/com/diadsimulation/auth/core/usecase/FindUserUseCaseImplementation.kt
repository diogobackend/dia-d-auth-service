package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.input.FindUserInputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import java.util.NoSuchElementException
import java.util.UUID

class FindUserUseCaseImplementation(
    private val userRepositoryPort: UserRepositoryOutputPort,
) : FindUserInputPort {
    override fun execute(
        userId: UUID?,
        email: String?,
        cpf: String?,
        rg: String?,
    ): User =
        when {
            userId != null -> userRepositoryPort.findById(userId)
            email != null -> userRepositoryPort.findByEmail(email)
            cpf != null -> userRepositoryPort.findByCpf(cpf)
            rg != null -> userRepositoryPort.findByRg(rg)
            else -> null
        } ?: throw NoSuchElementException(USER_NOT_FOUND)
}
