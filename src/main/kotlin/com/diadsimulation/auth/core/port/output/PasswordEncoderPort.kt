package com.diadsimulation.auth.core.port.output

interface PasswordEncoderPort {
    fun encode(rawPassword: String): String
}
