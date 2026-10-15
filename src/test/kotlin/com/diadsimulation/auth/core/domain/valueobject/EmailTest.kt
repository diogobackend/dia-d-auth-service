package com.diadsimulation.auth.core.domain.valueobject

import com.diadsimulation.auth.core.common.Messages.EMAIL_INVALID
import com.diadsimulation.auth.core.common.Messages.EMAIL_MAX_LENGTH
import com.diadsimulation.auth.core.common.Messages.EMAIL_REQUIRED
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class EmailTest {
    @ParameterizedTest
    @MethodSource("validEmails")
    fun `should create email when format is valid`(email: String) {
        val result = Email(email)

        assertEquals(email, result.value)
    }

    @ParameterizedTest
    @MethodSource("invalidEmails")
    fun `should throw exception when email format is invalid`(email: String) {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Email(email)
            }

        assertEquals(EMAIL_INVALID, exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "   "])
    fun `should throw exception when email is blank`(email: String) {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Email(email)
            }

        assertEquals(EMAIL_REQUIRED, exception.message)
    }

    @Test
    fun `should throw exception when email exceeds 255 characters`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Email(buildEmail(256))
            }

        assertEquals(
            EMAIL_MAX_LENGTH,
            exception.message,
        )
    }

    @Test
    fun `should accept email with exactly 255 characters`() {
        val email = buildEmail(255)

        val result = Email(email)

        assertEquals(255, result.value.length)
    }

    private fun buildEmail(length: Int): String {
        val domain = "@gmail.com"

        return "${"a".repeat(length - domain.length)}$domain"
    }

    companion object {
        @JvmStatic
        fun validEmails() =
            listOf(
                "usuario@gmail.com",
                "usuario.nome@gmail.com",
                "usuario+teste@gmail.com",
                "usuario_123@empresa.com.br",
            )

        @JvmStatic
        fun invalidEmails() =
            listOf(
                "usuario",
                "usuario@",
                "@gmail.com",
                "usuario@gmail",
                "usuario@gmail.",
                "usuario@gmail.c",
                "usuario gmail@gmail.com",
            )
    }
}
