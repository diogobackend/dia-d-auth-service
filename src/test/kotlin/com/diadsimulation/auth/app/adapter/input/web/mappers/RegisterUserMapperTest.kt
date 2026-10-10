package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.builders.buildRegisterUserRequest
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class RegisterUserMapperTest {
    @Test
    fun `should map request to domain with all fields`() {
        val request =
            buildRegisterUserRequest(
                phone = "98999999999",
                address = "São Luís - MA",
                cpf = "123.456.789-00",
                rg = "123456789",
            )

        val result = RegisterUserMapper.toDomain(request)

        assertNotNull(result.userId)
        assertEquals(request.email, result.email)
        assertEquals(request.password, result.passwordHash)
        assertEquals(CANDIDATE, result.role)
        assertEquals(ACTIVE, result.status)
        assertEquals(request.phone, result.phone)
        assertEquals(request.address, result.address)
        assertEquals(request.cpf, result.cpf)
        assertEquals(request.rg, result.rg)
    }

    @Test
    fun `should map request to domain with null optional fields`() {
        val request = buildRegisterUserRequest()

        val result = RegisterUserMapper.toDomain(request)

        assertNull(result.phone)
        assertNull(result.address)
        assertNull(result.cpf)
        assertNull(result.rg)
    }

    @Test
    fun `should normalize empty optional fields to null`() {
        val request =
            buildRegisterUserRequest(
                phone = "",
                address = "",
                cpf = "",
                rg = "",
            )

        val result = RegisterUserMapper.toDomain(request)

        assertNull(result.phone)
        assertNull(result.address)
        assertNull(result.cpf)
        assertNull(result.rg)
    }

    @Test
    fun `should preserve whitespace values according to current implementation`() {
        val request =
            buildRegisterUserRequest(
                phone = "   ",
                address = "   ",
                cpf = "   ",
                rg = "   ",
            )

        val result = RegisterUserMapper.toDomain(request)

        assertEquals("   ", result.phone)
        assertEquals("   ", result.address)
        assertEquals("   ", result.cpf)
        assertEquals("   ", result.rg)
    }

    @Test
    fun `should map domain to response`() {
        val user =
            RegisterUserMapper.toDomain(
                buildRegisterUserRequest(
                    phone = "98999999999",
                    address = "São Luís - MA",
                    cpf = "123.456.789-00",
                    rg = "123456789",
                ),
            )

        val response = RegisterUserMapper.toResponse(user)

        assertEquals(user.userId, response.userId)
        assertEquals(user.email, response.email)
        assertEquals(user.role, response.role)
        assertEquals(user.status, response.status)
        assertEquals(user.phone, response.phone)
        assertEquals(user.address, response.address)
        assertEquals(user.cpf, response.cpf)
        assertEquals(user.rg, response.rg)
        assertEquals(user.createdAt, response.createdAt)
    }
}
