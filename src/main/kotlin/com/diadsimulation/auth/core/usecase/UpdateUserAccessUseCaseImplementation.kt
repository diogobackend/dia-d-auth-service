package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserStatus
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.input.UpdateUserAccessInputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import java.time.OffsetDateTime
import java.util.NoSuchElementException
import java.util.UUID

class UpdateUserAccessUseCaseImplementation(
    private val userRepositoryOutputPort: UserRepositoryOutputPort,
) : UpdateUserAccessInputPort {
    override fun execute(
        userId: UUID,
        role: UserRole?,
        status: UserStatus?,
    ): User {
        val user =
            userRepositoryOutputPort.findById(userId)
                ?: throw NoSuchElementException(USER_NOT_FOUND)

        val updatedUser =
            User(
                userId = user.userId,
                name = user.name,
                email = user.email,
                login = user.login,
                passwordHash = user.passwordHash,
                role = role ?: user.role,
                status = status ?: user.status,
                phone = user.phone,
                cpf = user.cpf,
                rg = user.rg,
                address = user.address,
                birthDate = user.birthDate,
                createdAt = user.createdAt,
                updatedAt = OffsetDateTime.now(),
            )

        return userRepositoryOutputPort.updateAccess(updatedUser)
    }
}
