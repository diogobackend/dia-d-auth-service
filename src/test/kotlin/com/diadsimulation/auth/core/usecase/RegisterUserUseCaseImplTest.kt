package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.builders.buildUser
import com.diadsimulation.auth.core.common.Messages.PASSWORD_ENCODING_FAILED
import com.diadsimulation.auth.core.common.Messages.PERSISTENCE_FAILED
import com.diadsimulation.auth.core.common.Messages.USER_ALREADY_EXISTS
import com.diadsimulation.auth.core.domain.exceptions.UserAlreadyExistsException
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.output.PasswordEncoderOutputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class RegisterUserUseCaseImplTest {
    @MockK
    private lateinit var userRepositoryPort: UserRepositoryOutputPort

    @MockK
    private lateinit var passwordEncoderPort: PasswordEncoderOutputPort

    @InjectMockKs
    private lateinit var useCase: RegisterUserUseCaseImplementation

    private lateinit var user: User

    private val email get() = user.email.value
    private val cpf get() = user.cpf?.value
    private val rg get() = user.rg?.value
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
        assertEquals(ENCODED_PASSWORD, result.passwordHash)

        verify(exactly = 1) {
            passwordEncoderPort.encode(RAW_PASSWORD)
            userRepositoryPort.save(user)
        }
    }

    @Test
    fun `should register user without cpf and rg`() {
        user =
            buildUser(
                passwordHash = RAW_PASSWORD,
                cpf = null,
                rg = null,
            )

        mockAvailableUser()
        mockSuccessfulRegistration()

        val result = useCase.execute(user)

        assertEquals(ENCODED_PASSWORD, result.passwordHash)

        verify(exactly = 0) {
            userRepositoryPort.existsByCpf(any())
            userRepositoryPort.existsByRg(any())
        }

        verify(exactly = 1) {
            userRepositoryPort.save(user)
        }
    }

    @ParameterizedTest
    @MethodSource("duplicateFieldsProvider")
    fun `should throw exception when user field already exists`(field: String) {
        mockDuplicateField(field)

        val expectedMessage =
            when (field) {
                "email" -> "${USER_ALREADY_EXISTS}E-mail: $email"
                "cpf" -> "${USER_ALREADY_EXISTS}CPF: $cpf"
                "rg" -> "${USER_ALREADY_EXISTS}RG: $rg"
                else -> error("Unsupported field: $field")
            }

        assertUserAlreadyExists(expectedMessage)
    }

    @Test
    fun `should propagate exception when password encoding fails`() {
        mockAvailableUser()

        every {
            passwordEncoderPort.encode(passwordHash)
        } throws IllegalStateException(PASSWORD_ENCODING_FAILED)

        assertIllegalStateException(PASSWORD_ENCODING_FAILED)

        verify(exactly = 0) {
            userRepositoryPort.save(any())
        }
    }

    @Test
    fun `should propagate exception when persistence fails`() {
        mockAvailableUser()
        mockPasswordEncoding()

        every {
            userRepositoryPort.save(user)
        } throws IllegalStateException(PERSISTENCE_FAILED)

        assertIllegalStateException(PERSISTENCE_FAILED)

        verify(exactly = 1) {
            userRepositoryPort.save(user)
        }
    }

    private fun mockAvailableUser() {
        every {
            userRepositoryPort.existsByEmail(email)
        } returns false

        cpf?.let {
            every {
                userRepositoryPort.existsByCpf(it)
            } returns false
        }

        rg?.let {
            every {
                userRepositoryPort.existsByRg(it)
            } returns false
        }
    }

    private fun mockDuplicateField(field: String) {
        when (field) {
            "email" -> {
                every {
                    userRepositoryPort.existsByEmail(email)
                } returns true
            }

            "cpf" -> {
                every {
                    userRepositoryPort.existsByEmail(email)
                } returns false

                every {
                    userRepositoryPort.existsByCpf(requireNotNull(cpf))
                } returns true
            }

            "rg" -> {
                every {
                    userRepositoryPort.existsByEmail(email)
                } returns false

                every {
                    userRepositoryPort.existsByCpf(requireNotNull(cpf))
                } returns false

                every {
                    userRepositoryPort.existsByRg(requireNotNull(rg))
                } returns true
            }
        }
    }

    private fun mockPasswordEncoding() {
        every {
            passwordEncoderPort.encode(passwordHash)
        } returns ENCODED_PASSWORD
    }

    private fun mockSuccessfulRegistration() {
        mockPasswordEncoding()

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
            passwordEncoderPort.encode(any())
            userRepositoryPort.save(any())
        }
    }

    private fun assertIllegalStateException(expectedMessage: String) {
        val exception =
            assertThrows(IllegalStateException::class.java) {
                useCase.execute(user)
            }

        assertEquals(expectedMessage, exception.message)
    }

    companion object {
        private const val RAW_PASSWORD = "Diad@123456"
        private const val ENCODED_PASSWORD = "encoded-password"

        @JvmStatic
        fun duplicateFieldsProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of("email"),
                Arguments.of("cpf"),
                Arguments.of("rg"),
            )
    }
}
