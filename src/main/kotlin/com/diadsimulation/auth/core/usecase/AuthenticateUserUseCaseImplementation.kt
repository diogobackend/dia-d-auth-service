package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.common.Messages.INVALID_CREDENTIALS
import com.diadsimulation.auth.core.common.Messages.USER_NOT_ACTIVE
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import com.diadsimulation.auth.core.port.input.AuthenticateUserInputPort
import com.diadsimulation.auth.core.port.output.PasswordEncoderOutputPort
import com.diadsimulation.auth.core.port.output.TokenGeneratorOutputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import org.springframework.security.authentication.BadCredentialsException

class AuthenticateUserUseCaseImplementation(
    private val userRepositoryOutputPort: UserRepositoryOutputPort,
    private val passwordEncoderOutputPort: PasswordEncoderOutputPort,
    private val tokenGeneratorOutputPort: TokenGeneratorOutputPort,
) : AuthenticateUserInputPort {
    override fun execute(
        email: String?,
        login: String?,
        password: String,
    ): String {
        val user =
            when {
                !email.isNullOrBlank() -> userRepositoryOutputPort.findByEmail(email.toString())
                !login.isNullOrBlank() -> userRepositoryOutputPort.findByLogin(login.toString())
                else -> null
            } ?: throw BadCredentialsException(INVALID_CREDENTIALS)

        if (!passwordEncoderOutputPort.matches(password, user.passwordHash)) {
            throw BadCredentialsException(INVALID_CREDENTIALS)
        }

        if (user.status != ACTIVE) {
            throw IllegalStateException(USER_NOT_ACTIVE)
        }

        return tokenGeneratorOutputPort.generate(
            userId = user.userId,
            role = user.role.name,
        )
    }
}
