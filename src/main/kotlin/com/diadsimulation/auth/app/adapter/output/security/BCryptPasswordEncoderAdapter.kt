package com.diadsimulation.auth.app.adapter.output.security

import com.diadsimulation.auth.core.port.output.PasswordEncoderPort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordEncoderAdapter : PasswordEncoderPort {

    private val passwordEncoder = BCryptPasswordEncoder()

    override fun encode(rawPassword: String): String =
        passwordEncoder.encode(rawPassword)
            ?: throw IllegalStateException("Password encoding returned null")
}