package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.common.Messages.CPF_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.CPF_ALREADY_REGISTERED
import com.diadsimulation.auth.core.common.Messages.EMAIL_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.RG_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.RG_ALREADY_REGISTERED
import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.domain.exceptions.UserAlreadyExistsException
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.valueobject.Cpf
import com.diadsimulation.auth.core.domain.valueobject.Email
import com.diadsimulation.auth.core.domain.valueobject.Phone
import com.diadsimulation.auth.core.domain.valueobject.Rg
import com.diadsimulation.auth.core.port.input.UpdateUserUseCase
import com.diadsimulation.auth.core.port.output.PasswordEncoderPort
import com.diadsimulation.auth.core.port.output.UserRepositoryPort
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.NoSuchElementException
import java.util.UUID

@Service
class UpdateUserUseCaseImpl(
    private val userRepositoryPort: UserRepositoryPort,
    private val passwordEncoderPort: PasswordEncoderPort,
) : UpdateUserUseCase {
    override fun execute(
        userId: UUID,
        name: String?,
        email: String?,
        password: String?,
        phone: String?,
        address: String?,
        cpf: String?,
        rg: String?,
    ): User {
        val user =
            userRepositoryPort.findById(userId)
                ?: throw NoSuchElementException(USER_NOT_FOUND)

        val newEmail = email?.takeIf { it.isNotBlank() }

        if (newEmail != null && !newEmail.equals(user.email.value, ignoreCase = true)) {
            if (userRepositoryPort.findByEmail(newEmail.toString()) != null) {
                throw UserAlreadyExistsException(EMAIL_ALREADY_EXISTS)
            }
        }

        val updatedUser =
            User(
                userId = user.userId,
                name = name?.takeIf { it.isNotBlank() } ?: user.name,
                email = newEmail?.let(::Email) ?: user.email,
                passwordHash =
                    password
                        ?.takeIf { it.isNotBlank() }
                        ?.let(passwordEncoderPort::encode)
                        ?: user.passwordHash,
                role = user.role,
                status = user.status,
                phone = phone?.takeIf { it.isNotBlank() }?.let(::Phone) ?: user.phone,
                cpf = updateCpf(user.cpf, cpf),
                rg = updateRg(user.rg, rg),
                address = address?.takeIf { it.isNotBlank() } ?: user.address,
                birthDate = user.birthDate,
                createdAt = user.createdAt,
                updatedAt = OffsetDateTime.now(),
            )

        return userRepositoryPort.update(updatedUser)
    }

    private fun updateCpf(
        currentCpf: Cpf?,
        newCpf: String?,
    ): Cpf? {
        val value = newCpf?.takeIf { it.isNotBlank() } ?: return currentCpf

        val cpf = Cpf(value)

        if (currentCpf != null) {
            throw UserAlreadyExistsException(CPF_ALREADY_REGISTERED)
        }

        if (userRepositoryPort.existsByCpf(cpf.value)) {
            throw UserAlreadyExistsException(CPF_ALREADY_EXISTS)
        }

        return cpf
    }

    private fun updateRg(
        currentRg: Rg?,
        newRg: String?,
    ): Rg? {
        val value = newRg?.takeIf { it.isNotBlank() } ?: return currentRg

        val rg = Rg(value)

        if (currentRg != null) {
            throw UserAlreadyExistsException(RG_ALREADY_REGISTERED)
        }

        if (userRepositoryPort.existsByRg(rg.value)) {
            throw UserAlreadyExistsException(RG_ALREADY_EXISTS)
        }

        return rg
    }
}
