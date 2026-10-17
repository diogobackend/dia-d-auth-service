package com.diadsimulation.auth.core.domain.valueobject

import com.diadsimulation.auth.core.common.Messages.LOGIN_LENGTH
import com.diadsimulation.auth.core.common.Messages.LOGIN_LOWERCASE_REQUIRED
import com.diadsimulation.auth.core.common.Messages.LOGIN_MUST_START_WITH_LETTER
import com.diadsimulation.auth.core.common.Messages.LOGIN_REQUIRED
import com.diadsimulation.auth.core.common.Messages.LOGIN_WHITESPACE_NOT_ALLOWED

@JvmInline
value class Login(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            LOGIN_REQUIRED
        }

        require(value.length in 3..35) {
            LOGIN_LENGTH
        }

        require(value.first() in 'a'..'z') {
            LOGIN_MUST_START_WITH_LETTER
        }

        require(value.none { it.isUpperCase() }) {
            LOGIN_LOWERCASE_REQUIRED
        }

        require(value.none { it.isWhitespace() }) {
            LOGIN_WHITESPACE_NOT_ALLOWED
        }
    }
}
