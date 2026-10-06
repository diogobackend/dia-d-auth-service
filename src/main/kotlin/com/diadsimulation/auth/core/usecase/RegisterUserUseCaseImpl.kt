package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.domain.exception.UserAlreadyExistsException
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.input.RegisterUserUseCase
import com.diadsimulation.auth.core.port.output.PasswordEncoderPort
import com.diadsimulation.auth.core.port.output.UserRepositoryPort

class RegisterUserUseCaseImpl(
    private val userRepositoryPort: UserRepositoryPort,
    private val passwordEncoderPort: PasswordEncoderPort
) : RegisterUserUseCase {

    override fun execute(user: User): User {
        if (userRepositoryPort.existsByEmail(user.email)) {
            throw UserAlreadyExistsException(user.email)
        }

        user.passwordHash = passwordEncoderPort.encode(user.passwordHash)

        return userRepositoryPort.save(user)
    }
}