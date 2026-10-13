package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.builders.buildUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateUserAccessMapperTest {
    @Test
    fun `should map user to update user access response`() {
        val user = buildUser()

        val result = UpdateUserAccessMapper.toResponse(user)

        assertEquals(user.userId, result.userId)
        assertEquals(user.name, result.name)
        assertEquals(user.email.value, result.email)
        assertEquals(user.role, result.role)
        assertEquals(user.status, result.status)
        assertEquals(user.updatedAt, result.updatedAt)
    }
}
