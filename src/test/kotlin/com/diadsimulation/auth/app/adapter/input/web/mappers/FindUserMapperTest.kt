package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.builders.buildUser
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class FindUserMapperTest {
    @Test
    fun `should map user domain to find user response`() {
        val user = buildUser()

        val result = FindUserMapper.toResponse(user)

        assertAll(
            { assertEquals(user.userId, result.userId) },
            { assertEquals(user.name, result.name) },
            { assertEquals(user.email.value, result.email) },
            { assertEquals(user.role.name, result.role) },
            { assertEquals(user.status.name, result.status) },
            { assertEquals(user.phone?.value, result.phone) },
            { assertEquals(user.cpf?.value, result.cpf) },
            { assertEquals(user.rg?.value, result.rg) },
            { assertEquals(user.address, result.address) },
            { assertEquals(user.birthDate, result.birthDate) },
            { assertEquals(user.createdAt, result.createdAt) },
            { assertEquals(user.updatedAt, result.updatedAt) },
        )
    }

    @Test
    fun `should map user domain to response with null phone`() {
        val user = buildUser(phone = null)

        val result = FindUserMapper.toResponse(user)

        assertNull(result.phone)
    }

    @Test
    fun `should map user domain to response with null cpf and rg`() {
        val user = buildUser(cpf = null, rg = null)

        val result = FindUserMapper.toResponse(user)

        assertAll(
            { assertNull(result.cpf) },
            { assertNull(result.rg) },
        )
    }
}
