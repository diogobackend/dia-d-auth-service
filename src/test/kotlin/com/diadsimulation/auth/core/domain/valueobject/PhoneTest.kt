package com.diadsimulation.auth.core.domain.valueobject

import com.diadsimulation.auth.core.common.Messages.PHONE_INVALID
import com.diadsimulation.auth.core.common.Messages.PHONE_MAX_LENGTH
import com.diadsimulation.auth.core.common.Messages.PHONE_REQUIRED
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class PhoneTest {
    @ParameterizedTest
    @MethodSource("validPhones")
    fun `should create phone when format is valid`(phone: String) {
        val result = Phone(phone)

        assertEquals(phone, result.value)
    }

    @ParameterizedTest
    @MethodSource("invalidPhones")
    fun `should throw exception when phone format is invalid`(phone: String) {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Phone(phone)
            }

        assertEquals(PHONE_INVALID, exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "   "])
    fun `should throw exception when phone is blank`(phone: String) {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Phone(phone)
            }

        assertEquals(PHONE_REQUIRED, exception.message)
    }

    @Test
    fun `should throw exception when phone exceeds 30 characters`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Phone("9".repeat(31))
            }

        assertEquals(
            PHONE_MAX_LENGTH,
            exception.message,
        )
    }

    @Test
    fun `should reject phone with 30 characters and invalid format`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Phone("9".repeat(30))
            }

        assertEquals(PHONE_INVALID, exception.message)
    }

    companion object {
        @JvmStatic
        fun validPhones() =
            listOf(
                "98999999999",
                "(98) 99999-9999",
                "9833334444",
                "(98) 3333-4444",
                "11988888888",
            )

        @JvmStatic
        fun invalidPhones() =
            listOf(
                "12345",
                "98999abc999",
                "00999999999",
                "989999999",
                "abcdefghijk",
                "98-99999-9999",
            )
    }
}
