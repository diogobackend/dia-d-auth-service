package com.diadsimulation.auth.app.configuration

import com.diadsimulation.auth.core.port.input.DeleteUserInputPort
import com.diadsimulation.auth.core.port.input.FindUserInputPort
import com.diadsimulation.auth.core.port.input.ListUsersInputPort
import com.diadsimulation.auth.core.port.input.RegisterUserInputPort
import com.diadsimulation.auth.core.port.input.UpdateUserAccessInputPort
import com.diadsimulation.auth.core.port.input.UpdateUserInputPort
import com.diadsimulation.auth.core.port.output.PasswordEncoderOutputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
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
        userRepositoryPort: UserRepositoryOutputPort,
        passwordEncoderPort: PasswordEncoderOutputPort,
    ): RegisterUserInputPort = RegisterUserUseCaseImplementation(userRepositoryPort, passwordEncoderPort)

    @Bean
    fun listUsersUseCase(userRepositoryPort: UserRepositoryOutputPort): ListUsersInputPort =
        ListUsersUseCaseImplementation(userRepositoryPort)

    @Bean
    fun findUserUseCase(userRepositoryPort: UserRepositoryOutputPort): FindUserInputPort = FindUserUseCaseImplementation(userRepositoryPort)

    @Bean
    fun updateUserUseCase(
        userRepositoryPort: UserRepositoryOutputPort,
        passwordEncoderPort: PasswordEncoderOutputPort,
    ): UpdateUserInputPort = UpdateUserUseCaseImplementation(userRepositoryPort, passwordEncoderPort)

    @Bean
    fun updateUserAccessUseCase(userRepositoryPort: UserRepositoryOutputPort): UpdateUserAccessInputPort =
        UpdateUserAccessUseCaseImplementation(userRepositoryPort)

    @Bean
    fun deleteUserUseCase(userRepositoryPort: UserRepositoryOutputPort): DeleteUserInputPort =
        DeleteUserUseCaseImplementation(userRepositoryPort)
}
