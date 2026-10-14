package com.diadsimulation.auth.core.domain.valueobject

import com.diadsimulation.auth.core.common.Messages.CPF_INVALID
import com.diadsimulation.auth.core.common.Messages.CPF_REQUIRED

@JvmInline
value class Cpf(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            CPF_REQUIRED
        }

        require(isValid(value)) {
            CPF_INVALID
        }
    }

    companion object {
        private fun isValid(value: String): Boolean {
            if (!value.matches(Regex("""\d{11}|\d{3}\.\d{3}\.\d{3}-\d{2}"""))) {
                return false
            }

            val digits = value.filter(Char::isDigit)

            if (digits.toSet().size == 1) {
                return false
            }

            return (9..10).all { index ->
                val weight = index + 1
                val sum =
                    (0 until index).sumOf { position ->
                        digits[position].digitToInt() * (weight - position)
                    }

                val remainder = (sum * 10) % 11
                val expected = if (remainder == 10) 0 else remainder

                digits[index].digitToInt() == expected
            }
        }
    }
}
