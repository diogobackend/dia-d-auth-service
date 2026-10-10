import com.diadsimulation.auth.app.adapter.output.persistence.mappers.UserPersistenceMapper
import com.diadsimulation.auth.builders.buildUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class UserPersistenceMapperTest {
    @Test
    fun `should map domain to entity with all fields`() {
        val user = buildUser()

        val entity = UserPersistenceMapper.toEntity(user)

        assertEquals(user.userId, entity.userId)
        assertEquals(user.name, entity.name)
        assertEquals(user.email, entity.email)
        assertEquals(user.passwordHash, entity.passwordHash)
        assertEquals(user.role, entity.role)
        assertEquals(user.status, entity.status)
        assertEquals(user.phone, entity.phone)
        assertEquals(user.cpf, entity.cpf)
        assertEquals(user.rg, entity.rg)
        assertEquals(user.address, entity.address)
        assertEquals(user.createdAt, entity.createdAt)
        assertEquals(user.updatedAt, entity.updatedAt)
    }

    @Test
    fun `should map entity to domain with all fields`() {
        val entity = UserPersistenceMapper.toEntity(buildUser())

        val user = UserPersistenceMapper.toDomain(entity)

        assertEquals(entity.userId, user.userId)
        assertEquals(entity.name, user.name)
        assertEquals(entity.email, user.email)
        assertEquals(entity.passwordHash, user.passwordHash)
        assertEquals(entity.role, user.role)
        assertEquals(entity.status, user.status)
        assertEquals(entity.phone, user.phone)
        assertEquals(entity.cpf, user.cpf)
        assertEquals(entity.rg, user.rg)
        assertEquals(entity.address, user.address)
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
            )

        val entity = UserPersistenceMapper.toEntity(user)
        val result = UserPersistenceMapper.toDomain(entity)

        assertNull(result.phone)
        assertNull(result.cpf)
        assertNull(result.rg)
        assertNull(result.address)
    }
}
