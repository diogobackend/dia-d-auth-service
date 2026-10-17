package com.diadsimulation.auth.core.domain.valueobject

import com.diadsimulation.auth.core.common.Messages.LOGIN_LENGTH
import com.diadsimulation.auth.core.common.Messages.LOGIN_LOWERCASE_REQUIRED
import com.diadsimulation.auth.core.common.Messages.LOGIN_MUST_START_WITH_LETTER
import com.diadsimulation.auth.core.common.Messages.LOGIN_REQUIRED
import com.diadsimulation.auth.core.common.Messages.LOGIN_WHITESPACE_NOT_ALLOWED
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class LoginTest {
    @Test
    fun `should create valid login`() {
        val login = Login("diogo123")

        assertEquals("diogo123", login.value)
    }

    @ParameterizedTest
    @MethodSource("invalidLogins")
    fun `should reject invalid login`(
        value: String,
        expectedMessage: String,
    ) {
        val exception =
            assertThrows<IllegalArgumentException> {
                Login(value)
            }

        assertEquals(expectedMessage, exception.message)
    }

    companion object {
        @JvmStatic
        fun invalidLogins() =
            listOf(
                Arguments.of("", LOGIN_REQUIRED),
                Arguments.of("   ", LOGIN_REQUIRED),
                Arguments.of("ab", LOGIN_LENGTH),
                Arguments.of("a".repeat(36), LOGIN_LENGTH),
                Arguments.of("123diogo", LOGIN_MUST_START_WITH_LETTER),
                Arguments.of("Diogo123", LOGIN_MUST_START_WITH_LETTER),
                Arguments.of("diogoTeste", LOGIN_LOWERCASE_REQUIRED),
                Arguments.of("diogo 123", LOGIN_WHITESPACE_NOT_ALLOWED),
                Arguments.of("diogo\t123", LOGIN_WHITESPACE_NOT_ALLOWED),
                Arguments.of("{diogo", LOGIN_MUST_START_WITH_LETTER),
            )
    }
}
