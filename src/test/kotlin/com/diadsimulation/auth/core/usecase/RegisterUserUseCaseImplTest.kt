package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.builders.buildUser
import com.diadsimulation.auth.core.common.Messages.PASSWORD_ENCODING_FAILED
import com.diadsimulation.auth.core.common.Messages.PERSISTENCE_FAILED
import com.diadsimulation.auth.core.common.Messages.USER_ALREADY_EXISTS
import com.diadsimulation.auth.core.domain.exceptions.UserAlreadyExistsException
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.output.PasswordEncoderPort
import com.diadsimulation.auth.core.port.output.UserRepositoryPort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class RegisterUserUseCaseImplTest {
    @MockK
    private lateinit var userRepositoryPort: UserRepositoryPort

    @MockK
    private lateinit var passwordEncoderPort: PasswordEncoderPort

    @InjectMockKs
    private lateinit var useCase: RegisterUserUseCaseImpl

    private lateinit var user: User

    private val email get() = user.email
    private val cpf get() = user.cpf
    private val rg get() = user.rg
    private val passwordHash get() = user.passwordHash

    @BeforeEach
    fun setUp() {
        user = buildUser(passwordHash = "Diad@123456")
    }

    @Test
    fun `should register user successfully`() {
        mockAvailableUser()
        mockSuccessfulRegistration()

        val result = useCase.execute(user)

        assertSame(user, result)
        assertEquals("encoded-password", result.passwordHash)

        verify(exactly = 1) {
            passwordEncoderPort.encode("Diad@123456")
        }

        verify(exactly = 1) {
            userRepositoryPort.save(user)
        }
    }

    @Test
    fun `should register user without cpf and rg`() {
        user =
            buildUser(
                passwordHash = "Diad@123456",
                cpf = null,
                rg = null,
            )

        mockAvailableUser()
        mockSuccessfulRegistration()

        val result = useCase.execute(user)

        assertEquals("encoded-password", result.passwordHash)

        verify(exactly = 0) {
            userRepositoryPort.existsByCpf(any())
            userRepositoryPort.existsByRg(any())
        }

        verify(exactly = 1) {
            userRepositoryPort.save(user)
        }
    }

    @Test
    fun `should throw exception when email already exists`() {
        every {
            userRepositoryPort.existsByEmail(email)
        } returns true

        assertUserAlreadyExists(
            "${USER_ALREADY_EXISTS}E-mail: $email",
        )
    }

    @Test
    fun `should throw exception when cpf already exists`() {
        every {
            userRepositoryPort.existsByEmail(email)
        } returns false

        every {
            userRepositoryPort.existsByCpf(cpf!!)
        } returns true

        assertUserAlreadyExists(
            "${USER_ALREADY_EXISTS}CPF: $cpf",
        )
    }

    @Test
    fun `should throw exception when rg already exists`() {
        every {
            userRepositoryPort.existsByEmail(email)
        } returns false

        every {
            userRepositoryPort.existsByCpf(cpf!!)
        } returns false

        every {
            userRepositoryPort.existsByRg(rg!!)
        } returns true

        assertUserAlreadyExists(
            "${USER_ALREADY_EXISTS}RG: $rg",
        )
    }

    @Test
    fun `should propagate exception when password encoding fails`() {
        mockAvailableUser()

        every {
            passwordEncoderPort.encode(passwordHash)
        } throws IllegalStateException(PASSWORD_ENCODING_FAILED)

        val exception =
            assertThrows(IllegalStateException::class.java) {
                useCase.execute(user)
            }

        assertEquals(PASSWORD_ENCODING_FAILED, exception.message)

        verify(exactly = 0) {
            userRepositoryPort.save(any())
        }
    }

    @Test
    fun `should propagate exception when persistence fails`() {
        mockAvailableUser()

        every {
            passwordEncoderPort.encode(passwordHash)
        } returns "encoded-password"

        every {
            userRepositoryPort.save(user)
        } throws IllegalStateException(PERSISTENCE_FAILED)

        val exception =
            assertThrows(IllegalStateException::class.java) {
                useCase.execute(user)
            }

        assertEquals(PERSISTENCE_FAILED, exception.message)

        verify(exactly = 1) {
            userRepositoryPort.save(user)
        }
    }

    private fun mockAvailableUser() {
        every { userRepositoryPort.existsByEmail(email) } returns false

        cpf?.let {
            every { userRepositoryPort.existsByCpf(it) } returns false
        }

        rg?.let {
            every { userRepositoryPort.existsByRg(it) } returns false
        }
    }

    private fun mockSuccessfulRegistration() {
        every {
            passwordEncoderPort.encode(passwordHash)
        } returns "encoded-password"

        every {
            userRepositoryPort.save(user)
        } answers { firstArg() }
    }

    private fun assertUserAlreadyExists(expectedMessage: String) {
        val exception =
            assertThrows(UserAlreadyExistsException::class.java) {
                useCase.execute(user)
            }

        assertEquals(expectedMessage, exception.message)

        verify(exactly = 0) {
            passwordEncoderPort.encode(passwordHash)
        }

        verify(exactly = 0) {
            userRepositoryPort.save(any())
        }
    }
}
