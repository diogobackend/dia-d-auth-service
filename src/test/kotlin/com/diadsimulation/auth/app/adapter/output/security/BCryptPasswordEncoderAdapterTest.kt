package com.diadsimulation.auth.app.adapter.output.security

import com.diadsimulation.auth.core.common.Messages.PASSWORD_ENCODING_FAILED
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

class BCryptPasswordEncoderAdapterTest {
    companion object {
        private const val PASSWORD = "Diad@123456"
    }

    private val bCryptPasswordEncoder = BCryptPasswordEncoder()
    private val bCryptPasswordEncoderAdapter = BCryptPasswordEncoderAdapter(bCryptPasswordEncoder)

    @Test
    fun `should encode password successfully`() {
        val hash = bCryptPasswordEncoderAdapter.encode(PASSWORD)

        assertNotEquals(PASSWORD, hash)
        assertTrue(bCryptPasswordEncoder.matches(PASSWORD, hash))
    }

    @Test
    fun `should generate different hashes for same password`() {
        val firstHash = bCryptPasswordEncoderAdapter.encode(PASSWORD)
        val secondHash = bCryptPasswordEncoderAdapter.encode(PASSWORD)

        assertNotEquals(firstHash, secondHash)
        assertTrue(bCryptPasswordEncoder.matches(PASSWORD, firstHash))
        assertTrue(bCryptPasswordEncoder.matches(PASSWORD, secondHash))
    }

    @Test
    fun `should reject incorrect password`() {
        val hash = bCryptPasswordEncoderAdapter.encode(PASSWORD)

        assertFalse(bCryptPasswordEncoder.matches("WrongPassword123", hash))
    }

    @Test
    fun `should throw exception when encoder returns null`() {
        val passwordEncoder = mockk<PasswordEncoder>()

        every {
            passwordEncoder.encode(any())
        } returns null

        val bCryptPasswordEncoderAdapter = BCryptPasswordEncoderAdapter(passwordEncoder)

        val exception =
            assertThrows<IllegalStateException> {
                bCryptPasswordEncoderAdapter.encode(PASSWORD)
            }

        assertEquals(PASSWORD_ENCODING_FAILED, exception.message)
    }

    @Test
    fun `should encode password using default constructor`() {
        val bCryptPasswordEncoderAdapter = BCryptPasswordEncoderAdapter()
        val hash = bCryptPasswordEncoderAdapter.encode(PASSWORD)
        assertTrue(BCryptPasswordEncoder().matches(PASSWORD, hash))
    }

    @Test
    fun `should return true when password matches`() {
        val hash = bCryptPasswordEncoderAdapter.encode(PASSWORD)

        assertTrue(bCryptPasswordEncoderAdapter.matches(PASSWORD, hash))
    }

    @Test
    fun `should return false when password does not match`() {
        val hash = bCryptPasswordEncoderAdapter.encode(PASSWORD)

        assertFalse(bCryptPasswordEncoderAdapter.matches("WrongPassword123", hash))
    }
}
