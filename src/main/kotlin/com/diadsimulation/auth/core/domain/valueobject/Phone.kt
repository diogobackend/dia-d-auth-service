package com.diadsimulation.auth.core.domain.valueobject

import com.diadsimulation.auth.core.common.Messages.PHONE_INVALID
import com.diadsimulation.auth.core.common.Messages.PHONE_REQUIRED

@JvmInline
value class Phone(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            PHONE_REQUIRED
        }

        require(value.length <= 30) {
            "Telefone deve possuir no máximo 30 caracteres"
        }

        require(PHONE_REGEX.matches(value)) {
            PHONE_INVALID
        }
    }

    companion object {
        private val PHONE_REGEX =
            Regex("^\\(?[1-9][0-9]\\)?\\s?9?[0-9]{4}-?[0-9]{4}$")
    }
}
