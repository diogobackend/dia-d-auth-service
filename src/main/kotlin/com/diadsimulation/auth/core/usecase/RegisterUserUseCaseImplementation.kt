package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.common.Messages.USER_ALREADY_EXISTS
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.exceptions.UserAlreadyExistsException
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.input.RegisterUserInputPort
import com.diadsimulation.auth.core.port.output.PasswordEncoderOutputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort

class RegisterUserUseCaseImplementation(
    private val userRepositoryOutputPort: UserRepositoryOutputPort,
    private val passwordEncoderOutputPort: PasswordEncoderOutputPort,
) : RegisterUserInputPort {
    override fun execute(user: User): User {
        user.role = CANDIDATE
        val email = user.email.value
        val login = user.login.value
        val cpf = user.cpf?.value
        val rg = user.rg?.value

        if (userRepositoryOutputPort.existsByEmail(email)) {
            throw UserAlreadyExistsException(
                "${USER_ALREADY_EXISTS}E-mail: $email",
            )
        }

        if (userRepositoryOutputPort.existsByLogin(login)) {
            throw UserAlreadyExistsException(
                "${USER_ALREADY_EXISTS}Login: $login",
            )
        }

        if (cpf != null && userRepositoryOutputPort.existsByCpf(cpf)) {
            throw UserAlreadyExistsException(
                "${USER_ALREADY_EXISTS}CPF: $cpf",
            )
        }

        if (rg != null && userRepositoryOutputPort.existsByRg(rg)) {
            throw UserAlreadyExistsException(
                "${USER_ALREADY_EXISTS}RG: $rg",
            )
        }

        user.passwordHash = passwordEncoderOutputPort.encode(user.passwordHash)

        return userRepositoryOutputPort.save(user)
    }
}
