package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.builders.buildUser
import com.diadsimulation.auth.core.common.Messages.CPF_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.CPF_ALREADY_REGISTERED
import com.diadsimulation.auth.core.common.Messages.EMAIL_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.PASSWORD_ENCODING_FAILED
import com.diadsimulation.auth.core.common.Messages.PERSISTENCE_FAILED
import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
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
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource
import java.util.NoSuchElementException

@ExtendWith(MockKExtension::class)
class UpdateUserUseCaseImplTest {
    @MockK
    private lateinit var userRepositoryPort: UserRepositoryPort

    @MockK
    private lateinit var passwordEncoderPort: PasswordEncoderPort

    @InjectMockKs
    private lateinit var updateUserUseCaseImpl: UpdateUserUseCaseImpl

    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        user = buildUser()
    }

    @Test
    fun `should update user name successfully`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(name = "Updated Name")

        assertEquals("Updated Name", result.name)
        assertEquals(user.email, result.email)
        assertEquals(user.passwordHash, result.passwordHash)
        assertEquals(user.phone, result.phone)
        assertEquals(user.address, result.address)

        verify(exactly = 1) {
            userRepositoryPort.findById(user.userId)
            userRepositoryPort.update(any())
        }

        verify(exactly = 0) {
            passwordEncoderPort.encode(any())
        }
    }

    @Test
    fun `should update user email successfully`() {
        val newEmail = "updated@diad.com"

        mockExistingUser()
        every { userRepositoryPort.findByEmail(newEmail) } returns null
        mockSuccessfulUpdate()

        val result = execute(email = newEmail)

        assertEquals(newEmail, result.email.value)

        verify(exactly = 1) {
            userRepositoryPort.findByEmail(newEmail)
            userRepositoryPort.update(any())
        }
    }

    @Test
    fun `should update user password successfully`() {
        mockExistingUser()
        every {
            passwordEncoderPort.encode(RAW_PASSWORD)
        } returns ENCODED_PASSWORD
        mockSuccessfulUpdate()

        val result = execute(password = RAW_PASSWORD)

        assertEquals(ENCODED_PASSWORD, result.passwordHash)

        verify(exactly = 1) {
            passwordEncoderPort.encode(RAW_PASSWORD)
            userRepositoryPort.update(any())
        }
    }

    @Test
    fun `should update user phone successfully`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(phone = VALID_PHONE)

        assertEquals(VALID_PHONE, result.phone?.value)
        verify(exactly = 1) { userRepositoryPort.update(any()) }
    }

    @Test
    fun `should update user address successfully`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(address = "Updated Address")

        assertEquals("Updated Address", result.address)
        verify(exactly = 1) { userRepositoryPort.update(any()) }
    }

    @Test
    fun `should update all allowed fields successfully`() {
        val newEmail = "updated@diad.com"

        mockExistingUser()
        every { userRepositoryPort.findByEmail(newEmail) } returns null
        every {
            passwordEncoderPort.encode(RAW_PASSWORD)
        } returns ENCODED_PASSWORD
        mockSuccessfulUpdate()

        val result =
            execute(
                name = "Updated Name",
                email = newEmail,
                password = RAW_PASSWORD,
                phone = VALID_PHONE,
                address = "Updated Address",
            )

        assertEquals("Updated Name", result.name)
        assertEquals(newEmail, result.email.value)
        assertEquals(ENCODED_PASSWORD, result.passwordHash)
        assertEquals(VALID_PHONE, result.phone?.value)
        assertEquals("Updated Address", result.address)

        verify(exactly = 1) {
            passwordEncoderPort.encode(RAW_PASSWORD)
            userRepositoryPort.update(any())
        }
    }

    @Test
    fun `should preserve existing values when fields are null`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute()

        assertEquals(user.name, result.name)
        assertEquals(user.email, result.email)
        assertEquals(user.passwordHash, result.passwordHash)
        assertEquals(user.phone, result.phone)
        assertEquals(user.address, result.address)

        verify(exactly = 0) {
            userRepositoryPort.findByEmail(any())
            passwordEncoderPort.encode(any())
        }
    }

    @Test
    fun `should ignore blank fields`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result =
            execute(
                name = " ",
                email = " ",
                password = " ",
                phone = " ",
                address = " ",
            )

        assertEquals(user.name, result.name)
        assertEquals(user.email, result.email)
        assertEquals(user.passwordHash, result.passwordHash)
        assertEquals(user.phone, result.phone)
        assertEquals(user.address, result.address)

        verify(exactly = 0) {
            userRepositoryPort.findByEmail(any())
            passwordEncoderPort.encode(any())
        }
    }

    @Test
    fun `should allow updating with same email`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(email = user.email.value)

        assertEquals(user.email, result.email)

        verify(exactly = 0) {
            userRepositoryPort.findByEmail(any())
        }
    }

    @Test
    fun `should allow updating with same email ignoring case`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val newEmail = user.email.value.uppercase()

        val result = execute(email = newEmail)

        assertEquals(newEmail, result.email.value)

        verify(exactly = 0) {
            userRepositoryPort.findByEmail(any())
        }
    }

    @Test
    fun `should throw exception when user does not exist`() {
        every {
            userRepositoryPort.findById(user.userId)
        } returns null

        val exception =
            assertThrows(NoSuchElementException::class.java) {
                execute(name = "Updated Name")
            }

        assertEquals(USER_NOT_FOUND, exception.message)

        verify(exactly = 0) {
            userRepositoryPort.update(any())
            passwordEncoderPort.encode(any())
        }
    }

    @Test
    fun `should throw exception when email already exists`() {
        val newEmail = "existing@diad.com"

        mockExistingUser()
        every {
            userRepositoryPort.findByEmail(newEmail)
        } returns buildUser(email = newEmail)

        val exception =
            assertThrows(UserAlreadyExistsException::class.java) {
                execute(email = newEmail)
            }

        assertEquals(EMAIL_ALREADY_EXISTS, exception.message)

        verify(exactly = 0) {
            userRepositoryPort.update(any())
            passwordEncoderPort.encode(any())
        }
    }

    @Test
    fun `should propagate exception when password encoding fails`() {
        mockExistingUser()

        every {
            passwordEncoderPort.encode(RAW_PASSWORD)
        } throws IllegalStateException(PASSWORD_ENCODING_FAILED)

        val exception =
            assertThrows(IllegalStateException::class.java) {
                execute(password = RAW_PASSWORD)
            }

        assertEquals(PASSWORD_ENCODING_FAILED, exception.message)

        verify(exactly = 0) {
            userRepositoryPort.update(any())
        }
    }

    @Test
    fun `should propagate exception when persistence fails`() {
        mockExistingUser()

        every {
            userRepositoryPort.update(any())
        } throws IllegalStateException(PERSISTENCE_FAILED)

        val exception =
            assertThrows(IllegalStateException::class.java) {
                execute(name = "Updated Name")
            }

        assertEquals(PERSISTENCE_FAILED, exception.message)

        verify(exactly = 1) {
            userRepositoryPort.update(any())
        }
    }

    @Test
    fun `should preserve immutable and non editable user fields`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(name = "Updated Name")

        assertEquals(user.userId, result.userId)
        assertEquals(user.role, result.role)
        assertEquals(user.status, result.status)
        assertEquals(user.cpf, result.cpf)
        assertEquals(user.rg, result.rg)
        assertEquals(user.birthDate, result.birthDate)
        assertEquals(user.createdAt, result.createdAt)
        assertTrue(result.updatedAt >= user.updatedAt)
    }

    @Test
    fun `should update cpf when current cpf is null`() {
        user = buildUser(cpf = null)
        val newCpf = "613.098.083-33"

        mockExistingUser()
        every { userRepositoryPort.existsByCpf(newCpf) } returns false
        mockSuccessfulUpdate()

        val result = execute(cpf = newCpf)

        assertEquals(newCpf, result.cpf?.value)

        verify(exactly = 1) {
            userRepositoryPort.existsByCpf(newCpf)
            userRepositoryPort.update(any())
        }
    }

    @Test
    fun `should throw exception when cpf is already registered`() {
        mockExistingUser()

        val exception =
            assertThrows(UserAlreadyExistsException::class.java) {
                execute(cpf = "613.098.083-33")
            }

        assertEquals(CPF_ALREADY_REGISTERED, exception.message)

        verify(exactly = 0) {
            userRepositoryPort.existsByCpf(any())
            userRepositoryPort.update(any())
        }
    }

    @Test
    fun `should throw exception when cpf already exists`() {
        user = buildUser(cpf = null)
        val newCpf = "613.098.083-33"

        mockExistingUser()
        every { userRepositoryPort.existsByCpf(newCpf) } returns true

        val exception =
            assertThrows(UserAlreadyExistsException::class.java) {
                execute(cpf = newCpf)
            }

        assertEquals(CPF_ALREADY_EXISTS, exception.message)

        verify(exactly = 1) {
            userRepositoryPort.existsByCpf(newCpf)
        }

        verify(exactly = 0) {
            userRepositoryPort.update(any())
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = [" ", "   "])
    fun `should preserve current cpf when new cpf is null or blank`(newCpf: String?) {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(cpf = newCpf)

        assertEquals(user.cpf, result.cpf)

        verify(exactly = 0) {
            userRepositoryPort.existsByCpf(any())
        }
    }

    private fun execute(
        name: String? = null,
        email: String? = null,
        password: String? = null,
        phone: String? = null,
        address: String? = null,
        cpf: String? = null,
    ): User =
        updateUserUseCaseImpl.execute(
            userId = user.userId,
            name = name,
            email = email,
            password = password,
            phone = phone,
            address = address,
            cpf = cpf,
        )

    private fun mockExistingUser() {
        every {
            userRepositoryPort.findById(user.userId)
        } returns user
    }

    private fun mockSuccessfulUpdate() {
        every {
            userRepositoryPort.update(any())
        } answers { firstArg() }
    }

    companion object {
        private const val RAW_PASSWORD = "Diad@123456"
        private const val ENCODED_PASSWORD = "encoded-password"
        private const val VALID_PHONE = "98999999999"
    }
}
