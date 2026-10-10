package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.domain.exceptions.UserAlreadyExistsException
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.input.RegisterUserUseCase
import com.diadsimulation.auth.core.port.output.PasswordEncoderPort
import com.diadsimulation.auth.core.port.output.UserRepositoryPort

class RegisterUserUseCaseImpl(
    private val userRepositoryPort: UserRepositoryPort,
    private val passwordEncoderPort: PasswordEncoderPort
) : RegisterUserUseCase {

    override fun execute(user: User): User {

        val cpf = user.cpf
        val rg = user.rg
        val email = user.email

        if (userRepositoryPort.existsByEmail(email)) {
            throw UserAlreadyExistsException(
                "Já existe um usuário cadastrado com o e-mail: $email"
            )
        }

        if (cpf != null && userRepositoryPort.existsByCpf(cpf)) {
            throw UserAlreadyExistsException(
                "Já existe um usuário cadastrado com o CPF: $cpf"
            )
        }

        if (rg != null && userRepositoryPort.existsByRg(rg)) {
            throw UserAlreadyExistsException(
                "Já existe um usuário cadastrado com o RG: $rg"
            )
        }

        user.passwordHash = passwordEncoderPort.encode(user.passwordHash)

        return userRepositoryPort.save(user)
    }
}