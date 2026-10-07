package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.domain.exceptions.CpfAlreadyExistsException
import com.diadsimulation.auth.core.domain.exceptions.EmailAlreadyExistsException
import com.diadsimulation.auth.core.domain.exceptions.RgAlreadyExistsException
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
            throw EmailAlreadyExistsException(email)
        }

        if (cpf != null && userRepositoryPort.existsByCpf(cpf)) {
            throw CpfAlreadyExistsException(cpf)
        }

        if (rg != null && userRepositoryPort.existsByRg(rg)) {
            throw RgAlreadyExistsException(rg)
        }

        user.passwordHash = passwordEncoderPort.encode(user.passwordHash)

        return userRepositoryPort.save(user)
    }
}