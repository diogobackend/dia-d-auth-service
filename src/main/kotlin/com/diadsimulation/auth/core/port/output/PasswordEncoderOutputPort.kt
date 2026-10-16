package com.diadsimulation.auth.core.port.output

interface PasswordEncoderOutputPort {
    fun encode(rawPassword: String): String

    fun matches(
        rawPassword: String,
        encodedPassword: String,
    ): Boolean
}
