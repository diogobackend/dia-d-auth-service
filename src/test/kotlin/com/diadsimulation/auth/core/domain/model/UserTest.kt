import com.diadsimulation.auth.builders.buildUser
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserRole.SCHOOL_ADMIN
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import com.diadsimulation.auth.core.domain.model.User
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.util.UUID

class UserTest {
    @Test
    fun `should create user with default values`() {
        val user =
            User(
                email = "candidate@diadsimulation.com",
                passwordHash = "encoded-password",
                role = CANDIDATE,
                status = ACTIVE,
            )

        assertNotNull(user.userId)
        assertNotNull(user.createdAt)
        assertNotNull(user.updatedAt)

        assertNull(user.phone)
        assertNull(user.cpf)
        assertNull(user.rg)
        assertNull(user.address)
    }

    @Test
    fun `should create user with all fields`() {
        val userId = UUID.randomUUID()
        val createdAt = OffsetDateTime.now().minusDays(1)
        val updatedAt = OffsetDateTime.now()

        val user =
            buildUser(
                userId = userId,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )

        assertAll(
            { assertEquals(userId, user.userId) },
            { assertEquals("candidate@diadsimulation.com", user.email) },
            { assertEquals("encoded-password", user.passwordHash) },
            { assertEquals(CANDIDATE, user.role) },
            { assertEquals(ACTIVE, user.status) },
            { assertEquals("98999999999", user.phone) },
            { assertEquals("123.456.789-00", user.cpf) },
            { assertEquals("123456789", user.rg) },
            { assertEquals("São Luís - MA", user.address) },
            { assertEquals(createdAt, user.createdAt) },
            { assertEquals(updatedAt, user.updatedAt) },
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

        user.email = "new@diadsimulation.com"
        user.passwordHash = "new-password"
        user.role = SCHOOL_ADMIN
        user.status = ACTIVE
        user.phone = "98988888888"
        user.cpf = "987.654.321-00"
        user.rg = "987654321"
        user.address = "São Paulo - SP"
        user.updatedAt = updatedAt

        assertAll(
            { assertEquals("new@diadsimulation.com", user.email) },
            { assertEquals("new-password", user.passwordHash) },
            { assertEquals(SCHOOL_ADMIN, user.role) },
            { assertEquals(ACTIVE, user.status) },
            { assertEquals("98988888888", user.phone) },
            { assertEquals("987.654.321-00", user.cpf) },
            { assertEquals("987654321", user.rg) },
            { assertEquals("São Paulo - SP", user.address) },
            { assertEquals(updatedAt, user.updatedAt) },
        )
    }
}
