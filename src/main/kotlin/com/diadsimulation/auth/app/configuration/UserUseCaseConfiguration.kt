package com.diadsimulation.auth.app.configuration

import com.diadsimulation.auth.core.port.input.RegisterUserUseCase
import com.diadsimulation.auth.core.port.output.PasswordEncoderPort
import com.diadsimulation.auth.core.port.output.UserRepositoryPort
import com.diadsimulation.auth.core.usecase.RegisterUserUseCaseImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserUseCaseConfiguration {
    @Bean
    fun registerUserUseCase(
        userRepositoryPort: UserRepositoryPort,
        passwordEncoderPort: PasswordEncoderPort,
    ): RegisterUserUseCase =
        RegisterUserUseCaseImpl(
            userRepositoryPort = userRepositoryPort,
            passwordEncoderPort = passwordEncoderPort,
        )
}
