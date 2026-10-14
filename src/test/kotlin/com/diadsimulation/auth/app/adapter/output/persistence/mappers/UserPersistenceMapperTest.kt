package com.diadsimulation.auth.app.adapter.output.persistence.mappers

import com.diadsimulation.auth.builders.buildUser
import com.diadsimulation.auth.core.domain.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserPersistenceMapperTest {
    @Test
    fun `should map domain to entity with all fields`() {
        val user = buildUser()
        val entity = UserPersistenceMapper.toEntity(user)

        assertUserFields(
            expected = user,
            actual = UserPersistenceMapper.toDomain(entity),
        )
        assertEquals(user.birthDate, entity.birthDate)
    }

    @Test
    fun `should map entity to domain with all fields`() {
        val entity = UserPersistenceMapper.toEntity(buildUser())

        val user = UserPersistenceMapper.toDomain(entity)

        assertEquals(entity.userId, user.userId)
        assertEquals(entity.name, user.name)
        assertEquals(entity.email, user.email.value)
        assertEquals(entity.passwordHash, user.passwordHash)
        assertEquals(entity.role, user.role)
        assertEquals(entity.status, user.status)
        assertEquals(entity.phone, user.phone?.value)
        assertEquals(entity.cpf, user.cpf?.value)
        assertEquals(entity.rg, user.rg?.value)
        assertEquals(entity.address, user.address)
        assertEquals(entity.birthDate, user.birthDate)
        assertEquals(entity.createdAt, user.createdAt)
        assertEquals(entity.updatedAt, user.updatedAt)
    }

    @Test
    fun `should preserve null optional fields`() {
        val user =
            buildUser(
                phone = null,
                cpf = null,
                rg = null,
                address = null,
                birthDate = null,
            )

        val entity = UserPersistenceMapper.toEntity(user)
        val result = UserPersistenceMapper.toDomain(entity)

        assertUserFields(user, result)
    }

    private fun assertUserFields(
        expected: User,
        actual: User,
    ) {
        assertEquals(expected.userId, actual.userId)
        assertEquals(expected.name, actual.name)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.passwordHash, actual.passwordHash)
        assertEquals(expected.role, actual.role)
        assertEquals(expected.status, actual.status)
        assertEquals(expected.phone, actual.phone)
        assertEquals(expected.cpf, actual.cpf)
        assertEquals(expected.rg, actual.rg)
        assertEquals(expected.address, actual.address)
        assertEquals(expected.birthDate, actual.birthDate)
        assertEquals(expected.createdAt, actual.createdAt)
        assertEquals(expected.updatedAt, actual.updatedAt)
    }
}
