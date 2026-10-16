package com.diadsimulation.auth.app.adapter.output.security

import com.diadsimulation.auth.core.common.Messages.PASSWORD_ENCODING_FAILED
import com.diadsimulation.auth.core.port.output.PasswordEncoderOutputPort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordEncoderAdapter(
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder(),
) : PasswordEncoderOutputPort {
    override fun encode(rawPassword: String): String =
        passwordEncoder.encode(rawPassword)
            ?: throw IllegalStateException(PASSWORD_ENCODING_FAILED)

    override fun matches(
        rawPassword: String,
        encodedPassword: String,
    ): Boolean = passwordEncoder.matches(rawPassword, encodedPassword)
}
