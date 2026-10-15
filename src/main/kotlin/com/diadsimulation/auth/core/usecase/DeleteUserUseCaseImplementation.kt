package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.port.input.DeleteUserInputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import java.util.NoSuchElementException
import java.util.UUID

class DeleteUserUseCaseImplementation(
    private val userRepositoryPort: UserRepositoryOutputPort,
) : DeleteUserInputPort {
    override fun execute(userId: UUID) {
        val user =
            userRepositoryPort.findById(userId)
                ?: throw NoSuchElementException(USER_NOT_FOUND)

        userRepositoryPort.deleteById(user.userId)
    }
}
