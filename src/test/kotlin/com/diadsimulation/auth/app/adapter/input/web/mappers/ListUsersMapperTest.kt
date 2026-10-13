package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.builders.buildUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ListUsersMapperTest {
    @Test
    fun `should map user to list users response`() {
        val user = buildUser()

        val response = ListUsersMapper.toResponse(user)

        assertEquals(user.userId, response.userId)
        assertEquals(user.name, response.name)
        assertEquals(user.email.value, response.email)
        assertEquals(user.role, response.role)
        assertEquals(user.status, response.status)
        assertEquals(user.phone?.value, response.phone)
        assertEquals(user.cpf, response.cpf)
        assertEquals(user.rg, response.rg)
        assertEquals(user.address, response.address)
        assertEquals(user.birthDate, response.birthDate)
        assertEquals(user.createdAt, response.createdAt)
        assertEquals(user.updatedAt, response.updatedAt)
    }

    @Test
    fun `should map user to list users response with null phone`() {
        val user = buildUser(phone = null)

        val response = ListUsersMapper.toResponse(user)

        assertNull(response.phone)
    }
}
