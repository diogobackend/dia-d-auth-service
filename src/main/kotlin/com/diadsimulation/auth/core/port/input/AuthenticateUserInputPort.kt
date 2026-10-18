package com.diadsimulation.auth.core.port.input

interface AuthenticateUserInputPort {
    fun execute(
        email: String?,
        login: String?,
        password: String,
    ): String
}
