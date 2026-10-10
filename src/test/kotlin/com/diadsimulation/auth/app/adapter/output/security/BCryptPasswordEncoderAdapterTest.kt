package com.diadsimulation.auth.app.adapter.output.security

import com.diadsimulation.auth.core.common.Messages.PASSWORD_ENCODING_FAILED
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

class BCryptPasswordEncoderAdapterTest {

    companion object {
        private const val PASSWORD = "Diad@123456"
    }

    private val encoder = BCryptPasswordEncoder()
    private val adapter = BCryptPasswordEncoderAdapter(encoder)

    @Test
    fun `should encode password successfully`() {

        val hash = adapter.encode(PASSWORD)

        assertNotEquals(PASSWORD, hash)
        assertTrue(encoder.matches(PASSWORD, hash))
    }

    @Test
    fun `should generate different hashes for same password`() {

        val firstHash = adapter.encode(PASSWORD)
        val secondHash = adapter.encode(PASSWORD)

        assertNotEquals(firstHash, secondHash)
        assertTrue(encoder.matches(PASSWORD, firstHash))
        assertTrue(encoder.matches(PASSWORD, secondHash))
    }

    @Test
    fun `should reject incorrect password`() {
        val hash = adapter.encode(PASSWORD)

        assertFalse(encoder.matches("WrongPassword123", hash))
    }

    @Test
    fun `should throw exception when encoder returns null`() {
        val passwordEncoder = mockk<PasswordEncoder>()

        every {
            passwordEncoder.encode(any())
        } returns null

        val adapter = BCryptPasswordEncoderAdapter(passwordEncoder)

        val exception = assertThrows(IllegalStateException::class.java) {
            adapter.encode(PASSWORD)
        }

        assertEquals(PASSWORD_ENCODING_FAILED, exception.message)
    }

    @Test
    fun `should encode password using default constructor`() {
        val adapter = BCryptPasswordEncoderAdapter()
        val hash = adapter.encode(PASSWORD)
        assertTrue(BCryptPasswordEncoder().matches(PASSWORD, hash))
    }
}