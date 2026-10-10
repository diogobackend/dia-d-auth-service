import com.diadsimulation.auth.app.adapter.output.persistence.adapters.UserPersistenceAdapter
import com.diadsimulation.auth.app.adapter.output.persistence.mappers.UserPersistenceMapper
import com.diadsimulation.auth.app.adapter.output.persistence.repositories.UserJpaRepository
import com.diadsimulation.auth.builders.buildUser
import com.diadsimulation.auth.core.domain.model.User
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional

@ExtendWith(MockKExtension::class)
class UserPersistenceAdapterTest {
    @MockK
    private lateinit var userJpaRepository: UserJpaRepository

    @InjectMockKs
    private lateinit var adapter: UserPersistenceAdapter

    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        user = buildUser()
    }

    @Test
    fun `should save user successfully`() {
        every {
            userJpaRepository.save(
                match {
                    it.userId == user.userId &&
                        it.email == user.email &&
                        it.passwordHash == user.passwordHash
                },
            )
        } answers { firstArg() }

        val result = adapter.save(user)

        assertEquals(user.userId, result.userId)
        assertEquals(user.name, result.name)
        assertEquals(user.email, result.email)
        assertEquals(user.passwordHash, result.passwordHash)

        verify(exactly = 1) {
            userJpaRepository.save(
                match {
                    it.userId == user.userId &&
                        it.name == user.name &&
                        it.email == user.email &&
                        it.passwordHash == user.passwordHash
                },
            )
        }
    }

    @Test
    fun `should find user by id`() {
        val entity = UserPersistenceMapper.toEntity(user)

        every {
            userJpaRepository.findById(user.userId)
        } returns Optional.of(entity)

        val result = adapter.findById(user.userId)

        assertNotNull(result)
        assertEquals(user.userId, result?.userId)
        assertEquals(user.email, result?.email)
    }

    @Test
    fun `should return null when user id does not exist`() {
        every {
            userJpaRepository.findById(user.userId)
        } returns Optional.empty()

        val result = adapter.findById(user.userId)

        assertNull(result)
    }

    @Test
    fun `should find user by email`() {
        val entity = UserPersistenceMapper.toEntity(user)

        every {
            userJpaRepository.findByEmail(user.email)
        } returns entity

        val result = adapter.findByEmail(user.email)

        assertNotNull(result)
        assertEquals(user.userId, result?.userId)
        assertEquals(user.email, result?.email)
    }

    @Test
    fun `should return null when email does not exist`() {
        every {
            userJpaRepository.findByEmail(user.email)
        } returns null

        val result = adapter.findByEmail(user.email)

        assertNull(result)
    }

    @Test
    fun `should return true when email exists`() {
        every {
            userJpaRepository.existsByEmail(user.email)
        } returns true

        assertTrue(adapter.existsByEmail(user.email))
    }

    @Test
    fun `should return false when email does not exist`() {
        every {
            userJpaRepository.existsByEmail(user.email)
        } returns false

        assertFalse(adapter.existsByEmail(user.email))
    }

    @Test
    fun `should return true when cpf exists`() {
        every {
            userJpaRepository.existsByCpf(user.cpf!!)
        } returns true

        assertTrue(adapter.existsByCpf(user.cpf!!))
    }

    @Test
    fun `should return false when cpf does not exist`() {
        every {
            userJpaRepository.existsByCpf(user.cpf!!)
        } returns false

        assertFalse(adapter.existsByCpf(user.cpf!!))
    }

    @Test
    fun `should return true when rg exists`() {
        every {
            userJpaRepository.existsByRg(user.rg!!)
        } returns true

        assertTrue(adapter.existsByRg(user.rg!!))
    }

    @Test
    fun `should return false when rg does not exist`() {
        every {
            userJpaRepository.existsByRg(user.rg!!)
        } returns false

        assertFalse(adapter.existsByRg(user.rg!!))
    }
}
