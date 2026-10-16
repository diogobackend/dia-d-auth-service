package com.diadsimulation.auth.app.configuration

import com.diadsimulation.auth.core.port.input.AuthenticateUserInputPort
import com.diadsimulation.auth.core.port.input.DeleteUserInputPort
import com.diadsimulation.auth.core.port.input.FindUserInputPort
import com.diadsimulation.auth.core.port.input.ListUsersInputPort
import com.diadsimulation.auth.core.port.input.RegisterUserInputPort
import com.diadsimulation.auth.core.port.input.UpdateUserAccessInputPort
import com.diadsimulation.auth.core.port.input.UpdateUserInputPort
import com.diadsimulation.auth.core.port.output.PasswordEncoderOutputPort
import com.diadsimulation.auth.core.port.output.TokenGeneratorOutputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import com.diadsimulation.auth.core.usecase.AuthenticateUserUseCaseImplementation
import com.diadsimulation.auth.core.usecase.DeleteUserUseCaseImplementation
import com.diadsimulation.auth.core.usecase.FindUserUseCaseImplementation
import com.diadsimulation.auth.core.usecase.ListUsersUseCaseImplementation
import com.diadsimulation.auth.core.usecase.RegisterUserUseCaseImplementation
import com.diadsimulation.auth.core.usecase.UpdateUserAccessUseCaseImplementation
import com.diadsimulation.auth.core.usecase.UpdateUserUseCaseImplementation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserUseCaseConfiguration {
    @Bean
    fun registerUserUseCase(
        userRepositoryOutputPort: UserRepositoryOutputPort,
        passwordEncoderOutputPort: PasswordEncoderOutputPort,
    ): RegisterUserInputPort = RegisterUserUseCaseImplementation(userRepositoryOutputPort, passwordEncoderOutputPort)

    @Bean
    fun listUsersUseCase(userRepositoryOutputPort: UserRepositoryOutputPort): ListUsersInputPort =
        ListUsersUseCaseImplementation(userRepositoryOutputPort)

    @Bean
    fun findUserUseCase(userRepositoryOutputPort: UserRepositoryOutputPort): FindUserInputPort =
        FindUserUseCaseImplementation(userRepositoryOutputPort)

    @Bean
    fun updateUserUseCase(
        userRepositoryOutputPort: UserRepositoryOutputPort,
        passwordEncoderOutputPort: PasswordEncoderOutputPort,
    ): UpdateUserInputPort = UpdateUserUseCaseImplementation(userRepositoryOutputPort, passwordEncoderOutputPort)

    @Bean
    fun updateUserAccessUseCase(userRepositoryOutputPort: UserRepositoryOutputPort): UpdateUserAccessInputPort =
        UpdateUserAccessUseCaseImplementation(userRepositoryOutputPort)

    @Bean
    fun deleteUserUseCase(userRepositoryOutputPort: UserRepositoryOutputPort): DeleteUserInputPort =
        DeleteUserUseCaseImplementation(userRepositoryOutputPort)

    @Bean
    fun authenticateUserUseCase(
        userRepositoryOutputPort: UserRepositoryOutputPort,
        passwordEncoderOutputPort: PasswordEncoderOutputPort,
        tokenGeneratorOutputPort: TokenGeneratorOutputPort,
    ): AuthenticateUserInputPort =
        AuthenticateUserUseCaseImplementation(
            userRepositoryOutputPort,
            passwordEncoderOutputPort,
            tokenGeneratorOutputPort,
        )
}
