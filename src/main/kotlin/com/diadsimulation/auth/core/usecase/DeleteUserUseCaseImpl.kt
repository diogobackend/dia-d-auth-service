package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.port.input.DeleteUserUseCase
import com.diadsimulation.auth.core.port.output.UserRepositoryPort
import org.springframework.stereotype.Service
import java.util.NoSuchElementException
import java.util.UUID

@Service
class DeleteUserUseCaseImpl(
    private val userRepositoryPort: UserRepositoryPort,
) : DeleteUserUseCase {
    override fun execute(userId: UUID) {
        val user =
            userRepositoryPort.findById(userId)
                ?: throw NoSuchElementException(USER_NOT_FOUND)

        userRepositoryPort.deleteById(user.userId)
    }
}
