package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.builders.buildUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class UpdateUserMapperTest {
    @Test
    fun `should map user to update user response successfully`() {
        val user = buildUser()

        val result = UpdateUserMapper.toResponse(user)

        assertAll(
            { assertEquals(user.userId, result.userId) },
            { assertEquals(user.name, result.name) },
            { assertEquals(user.email.value, result.email) },
            { assertEquals(user.phone?.value, result.phone) },
            { assertEquals(user.address, result.address) },
            { assertEquals(user.cpf?.value, result.cpf) },
            { assertEquals(user.rg?.value, result.rg) },
            { assertEquals(user.updatedAt, result.updatedAt) },
        )
    }

    @Test
    fun `should map user with null optional fields`() {
        val user =
            buildUser(
                phone = null,
                cpf = null,
                rg = null,
                address = null,
            )

        val result = UpdateUserMapper.toResponse(user)

        assertAll(
            { assertNull(result.phone) },
            { assertNull(result.cpf) },
            { assertNull(result.rg) },
            { assertNull(result.address) },
        )
    }
}
