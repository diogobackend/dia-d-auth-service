package com.diadsimulation.auth.core.domain.model

import com.diadsimulation.auth.builders.buildUser
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserRole.SCHOOL_ADMIN
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import com.diadsimulation.auth.core.domain.valueobject.Cpf
import com.diadsimulation.auth.core.domain.valueobject.Email
import com.diadsimulation.auth.core.domain.valueobject.Login
import com.diadsimulation.auth.core.domain.valueobject.Phone
import com.diadsimulation.auth.core.domain.valueobject.Rg
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

class UserTest {
    @Test
    fun `should create user with default values`() {
        val user =
            User(
                name = "Diogo Ferreira",
                email = Email("candidate@diadsimulation.com"),
                login = Login("diogo123"),
                passwordHash = "encoded-password",
                role = CANDIDATE,
                status = ACTIVE,
            )

        assertAll(
            { assertNotNull(user.userId) },
            { assertNotNull(user.createdAt) },
            { assertNotNull(user.updatedAt) },
            { assertNull(user.phone) },
            { assertNull(user.cpf) },
            { assertNull(user.rg) },
            { assertNull(user.address) },
            { assertNull(user.birthDate) },
        )
    }

    @Test
    fun `should create user with all fields`() {
        val userId = UUID.randomUUID()
        val createdAt = OffsetDateTime.now().minusDays(1)
        val updatedAt = OffsetDateTime.now()
        val birthDate = LocalDate.of(1997, 1, 11)

        val user =
            buildUser(
                userId = userId,
                createdAt = createdAt,
                updatedAt = updatedAt,
                birthDate = birthDate,
            )

        assertAll(
            { assertEquals(userId, user.userId) },
            { assertEquals(createdAt, user.createdAt) },
            { assertEquals(updatedAt, user.updatedAt) },
            { assertEquals(birthDate, user.birthDate) },
        )
    }

    @Test
    fun `should update mutable fields`() {
        val user =
            buildUser(
                email = "old@diadsimulation.com",
                passwordHash = "old-password",
                phone = null,
                cpf = null,
                rg = null,
                address = null,
            )

        val updatedAt = OffsetDateTime.now().plusMinutes(1)
        val birthDate = LocalDate.of(1997, 1, 11)

        val expected =
            buildUser(
                userId = user.userId,
                name = "Novo Nome",
                email = "new@diadsimulation.com",
                passwordHash = "new-password",
                role = SCHOOL_ADMIN,
                status = ACTIVE,
                phone = "98988888888",
                cpf = "987.654.321-00",
                rg = "04.717.957/2013-4",
                address = "São Paulo - SP",
                birthDate = birthDate,
                createdAt = user.createdAt,
                updatedAt = updatedAt,
            )

        user.apply {
            email = Email("new@diadsimulation.com")
            name = "Novo Nome"
            passwordHash = "new-password"
            role = SCHOOL_ADMIN
            status = ACTIVE
            phone = Phone("98988888888")
            cpf = Cpf("987.654.321-00")
            rg = Rg("04.717.957/2013-4")
            address = "São Paulo - SP"
            this.birthDate = birthDate
            this.updatedAt = updatedAt
        }

        assertUserFields(expected, user)
    }

    @Test
    fun `should update user login`() {
        val user = buildUser()

        user.login = Login("novologin123")

        assertEquals("novologin123", user.login.value)
    }

    private fun assertUserFields(
        expected: User,
        actual: User,
    ) {
        assertAll(
            { assertEquals(expected.userId, actual.userId) },
            { assertEquals(expected.name, actual.name) },
            { assertEquals(expected.email, actual.email) },
            { assertEquals(expected.passwordHash, actual.passwordHash) },
            { assertEquals(expected.role, actual.role) },
            { assertEquals(expected.status, actual.status) },
            { assertEquals(expected.phone, actual.phone) },
            { assertEquals(expected.cpf, actual.cpf) },
            { assertEquals(expected.rg, actual.rg) },
            { assertEquals(expected.address, actual.address) },
            { assertEquals(expected.birthDate, actual.birthDate) },
            { assertEquals(expected.createdAt, actual.createdAt) },
            { assertEquals(expected.updatedAt, actual.updatedAt) },
        )
    }
}
