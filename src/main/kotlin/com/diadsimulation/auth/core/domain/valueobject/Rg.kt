package com.diadsimulation.auth.core.domain.valueobject

import com.diadsimulation.auth.core.common.Messages.RG_INVALID
import com.diadsimulation.auth.core.common.Messages.RG_REQUIRED

@JvmInline
value class Rg(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            RG_REQUIRED
        }

        require(RG_REGEX.matches(value)) {
            RG_INVALID
        }
    }

    companion object {
        private val RG_REGEX =
            Regex("""^(?:\d{2}\.\d{3}\.\d{3}/\d{4}-\d|\d{3}\.\d{3}\.\d{3}\.\d{3}-\d|\d{13})$""")
    }
}
