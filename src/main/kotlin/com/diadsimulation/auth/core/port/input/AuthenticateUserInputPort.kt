package com.diadsimulation.auth.core.port.input

interface AuthenticateUserInputPort {
    fun execute(
        email: String,
        password: String,
    ): String
}
