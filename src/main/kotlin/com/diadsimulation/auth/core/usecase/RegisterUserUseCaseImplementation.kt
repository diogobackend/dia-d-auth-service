package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.common.Messages.USER_ALREADY_EXISTS
import com.diadsimulation.auth.core.domain.exceptions.UserAlreadyExistsException
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.input.RegisterUserInputPort
import com.diadsimulation.auth.core.port.output.PasswordEncoderOutputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort

class RegisterUserUseCaseImplementation(
    private val userRepositoryPort: UserRepositoryOutputPort,
    private val passwordEncoderPort: PasswordEncoderOutputPort,
) : RegisterUserInputPort {
    override fun execute(user: User): User {
        val cpf = user.cpf?.value
        val rg = user.rg?.value
        val email = user.email.value

        if (userRepositoryPort.existsByEmail(email)) {
            throw UserAlreadyExistsException(
                "${USER_ALREADY_EXISTS}E-mail: $email",
            )
        }

        if (cpf != null && userRepositoryPort.existsByCpf(cpf)) {
            throw UserAlreadyExistsException(
                "${USER_ALREADY_EXISTS}CPF: $cpf",
            )
        }

        if (rg != null && userRepositoryPort.existsByRg(rg)) {
            throw UserAlreadyExistsException(
                "${USER_ALREADY_EXISTS}RG: $rg",
            )
        }

        user.passwordHash = passwordEncoderPort.encode(user.passwordHash)

        return userRepositoryPort.save(user)
    }
}
