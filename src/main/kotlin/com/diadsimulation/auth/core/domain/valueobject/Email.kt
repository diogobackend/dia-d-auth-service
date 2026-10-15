package com.diadsimulation.auth.core.domain.valueobject

import com.diadsimulation.auth.core.common.Messages.EMAIL_INVALID
import com.diadsimulation.auth.core.common.Messages.EMAIL_MAX_LENGTH
import com.diadsimulation.auth.core.common.Messages.EMAIL_REQUIRED

@JvmInline
value class Email(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            EMAIL_REQUIRED
        }

        require(value.length <= 255) {
            EMAIL_MAX_LENGTH
        }

        require(EMAIL_REGEX.matches(value)) {
            EMAIL_INVALID
        }
    }

    companion object {
        private val EMAIL_REGEX =
            Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
