package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.common.Messages.BIRTH_DATE_ALREADY_REGISTERED
import com.diadsimulation.auth.core.common.Messages.BIRTH_DATE_CANNOT_BE_FUTURE
import com.diadsimulation.auth.core.common.Messages.CPF_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.CPF_ALREADY_REGISTERED
import com.diadsimulation.auth.core.common.Messages.EMAIL_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.LOGIN_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.RG_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.RG_ALREADY_REGISTERED
import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.domain.exceptions.UserAlreadyExistsException
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.valueobject.Cpf
import com.diadsimulation.auth.core.domain.valueobject.Email
import com.diadsimulation.auth.core.domain.valueobject.Login
import com.diadsimulation.auth.core.domain.valueobject.Phone
import com.diadsimulation.auth.core.domain.valueobject.Rg
import com.diadsimulation.auth.core.port.input.UpdateUserInputPort
import com.diadsimulation.auth.core.port.output.PasswordEncoderOutputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.NoSuchElementException
import java.util.UUID

class UpdateUserUseCaseImplementation(
    private val userRepositoryOutputPort: UserRepositoryOutputPort,
    private val passwordEncoderOutputPort: PasswordEncoderOutputPort,
) : UpdateUserInputPort {
    override fun execute(
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
    ): User {
        val user =
            userRepositoryOutputPort.findById(userId)
                ?: throw NoSuchElementException(USER_NOT_FOUND)

        val newEmail = email?.takeIf { it.isNotBlank() }

        if (newEmail != null && !newEmail.equals(user.email.value, ignoreCase = true)) {
            if (userRepositoryOutputPort.findByEmail(newEmail.toString()) != null) {
                throw UserAlreadyExistsException(EMAIL_ALREADY_EXISTS)
            }
        }

        val newLogin = login?.takeIf { it.isNotBlank() }?.let(::Login)

        if (newLogin != null && newLogin.value != user.login.value) {
            if (userRepositoryOutputPort.existsByLogin(newLogin.value)) {
                throw UserAlreadyExistsException(LOGIN_ALREADY_EXISTS)
            }
        }

        val updatedUser =
            User(
                userId = user.userId,
                name = name?.takeIf { it.isNotBlank() } ?: user.name,
                email = newEmail?.let(::Email) ?: user.email,
                login = newLogin ?: user.login,
                passwordHash =
                    password
                        ?.takeIf { it.isNotBlank() }
                        ?.let(passwordEncoderOutputPort::encode)
                        ?: user.passwordHash,
                role = user.role,
                status = user.status,
                phone = phone?.takeIf { it.isNotBlank() }?.let(::Phone) ?: user.phone,
                cpf = updateCpf(user.cpf, cpf),
                rg = updateRg(user.rg, rg),
                address = address?.takeIf { it.isNotBlank() } ?: user.address,
                birthDate = updateBirthDate(user.birthDate, birthDate),
                createdAt = user.createdAt,
                updatedAt = OffsetDateTime.now(),
            )

        return userRepositoryOutputPort.update(updatedUser)
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

        if (userRepositoryOutputPort.existsByCpf(cpf.value)) {
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

        if (userRepositoryOutputPort.existsByRg(rg.value)) {
            throw UserAlreadyExistsException(RG_ALREADY_EXISTS)
        }

        return rg
    }

    private fun updateBirthDate(
        currentBirthDate: LocalDate?,
        newBirthDate: LocalDate?,
    ): LocalDate? {
        if (newBirthDate == null) return currentBirthDate

        require(!newBirthDate.isAfter(LocalDate.now())) {
            BIRTH_DATE_CANNOT_BE_FUTURE
        }

        require(currentBirthDate == null) {
            BIRTH_DATE_ALREADY_REGISTERED
        }

        return newBirthDate
    }
}
