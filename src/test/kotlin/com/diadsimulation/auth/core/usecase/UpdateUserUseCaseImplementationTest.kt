package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.builders.buildUser
import com.diadsimulation.auth.core.common.Messages.BIRTH_DATE_ALREADY_REGISTERED
import com.diadsimulation.auth.core.common.Messages.BIRTH_DATE_CANNOT_BE_FUTURE
import com.diadsimulation.auth.core.common.Messages.CPF_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.CPF_ALREADY_REGISTERED
import com.diadsimulation.auth.core.common.Messages.EMAIL_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.PASSWORD_ENCODING_FAILED
import com.diadsimulation.auth.core.common.Messages.PERSISTENCE_FAILED
import com.diadsimulation.auth.core.common.Messages.RG_ALREADY_EXISTS
import com.diadsimulation.auth.core.common.Messages.RG_ALREADY_REGISTERED
import com.diadsimulation.auth.core.common.Messages.RG_INVALID
import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate
import java.util.NoSuchElementException

@ExtendWith(MockKExtension::class)
class UpdateUserUseCaseImplementationTest {
    @MockK
    private lateinit var userRepositoryOutputPort: UserRepositoryOutputPort

    @MockK
    private lateinit var passwordEncoderOutputPort: PasswordEncoderOutputPort

    @InjectMockKs
    private lateinit var updateUserUseCaseImpl: UpdateUserUseCaseImplementation

    private lateinit var user: User

    private val userId get() = user.userId
    private val name get() = user.name
    private val email get() = user.email
    private val phone get() = user.phone
    private val address get() = user.address
    private val cpf get() = user.cpf
    private val rg get() = user.rg
    private val passwordHash get() = user.passwordHash
    private val role get() = user.role
    private val status get() = user.status
    private val birthDate get() = user.birthDate
    private val createdAt get() = user.createdAt
    private val updatedAt get() = user.updatedAt

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
        assertEquals(email, result.email)
        assertEquals(passwordHash, result.passwordHash)
        assertEquals(phone, result.phone)
        assertEquals(address, result.address)

        verify(exactly = 1) {
            userRepositoryOutputPort.findById(userId)
            userRepositoryOutputPort.update(any())
        }

        verify(exactly = 0) {
            passwordEncoderOutputPort.encode(any())
        }
    }

    @Test
    fun `should update user email successfully`() {
        val newEmail = "updated@diad.com"

        mockExistingUser()
        every { userRepositoryOutputPort.findByEmail(newEmail) } returns null
        mockSuccessfulUpdate()

        val result = execute(email = newEmail)

        assertEquals(newEmail, result.email.value)

        verify(exactly = 1) {
            userRepositoryOutputPort.findByEmail(newEmail)
            userRepositoryOutputPort.update(any())
        }
    }

    @Test
    fun `should update user password successfully`() {
        mockExistingUser()
        every {
            passwordEncoderOutputPort.encode(RAW_PASSWORD)
        } returns ENCODED_PASSWORD
        mockSuccessfulUpdate()

        val result = execute(password = RAW_PASSWORD)

        assertEquals(ENCODED_PASSWORD, result.passwordHash)

        verify(exactly = 1) {
            passwordEncoderOutputPort.encode(RAW_PASSWORD)
            userRepositoryOutputPort.update(any())
        }
    }

    @Test
    fun `should update user phone successfully`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(phone = VALID_PHONE)

        assertEquals(VALID_PHONE, result.phone?.value)
        verify(exactly = 1) { userRepositoryOutputPort.update(any()) }
    }

    @Test
    fun `should update user address successfully`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(address = "Updated Address")

        assertEquals("Updated Address", result.address)
        verify(exactly = 1) { userRepositoryOutputPort.update(any()) }
    }

    @Test
    fun `should update all allowed fields successfully`() {
        val newEmail = "updated@diad.com"

        mockExistingUser()
        every { userRepositoryOutputPort.findByEmail(newEmail) } returns null
        every {
            passwordEncoderOutputPort.encode(RAW_PASSWORD)
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
            passwordEncoderOutputPort.encode(RAW_PASSWORD)
            userRepositoryOutputPort.update(any())
        }
    }

    @Test
    fun `should preserve existing values when fields are null`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute()

        assertEquals(name, result.name)
        assertEquals(email, result.email)
        assertEquals(passwordHash, result.passwordHash)
        assertEquals(phone, result.phone)
        assertEquals(address, result.address)

        verify(exactly = 0) {
            userRepositoryOutputPort.findByEmail(any())
            passwordEncoderOutputPort.encode(any())
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

        assertEquals(name, result.name)
        assertEquals(email, result.email)
        assertEquals(passwordHash, result.passwordHash)
        assertEquals(phone, result.phone)
        assertEquals(address, result.address)

        verify(exactly = 0) {
            userRepositoryOutputPort.findByEmail(any())
            passwordEncoderOutputPort.encode(any())
        }
    }

    @Test
    fun `should allow updating with same email`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(email = email.value)

        assertEquals(email, result.email)

        verify(exactly = 0) {
            userRepositoryOutputPort.findByEmail(any())
        }
    }

    @Test
    fun `should allow updating with same email ignoring case`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val newEmail = email.value.uppercase()

        val result = execute(email = newEmail)

        assertEquals(newEmail, result.email.value)

        verify(exactly = 0) {
            userRepositoryOutputPort.findByEmail(any())
        }
    }

    @Test
    fun `should throw exception when user does not exist`() {
        every {
            userRepositoryOutputPort.findById(userId)
        } returns null

        val exception =
            assertThrows<NoSuchElementException> {
                execute(name = "Updated Name")
            }

        assertEquals(USER_NOT_FOUND, exception.message)

        verify(exactly = 0) {
            userRepositoryOutputPort.update(any())
            passwordEncoderOutputPort.encode(any())
        }
    }

    @Test
    fun `should throw exception when email already exists`() {
        val newEmail = "existing@diad.com"

        mockExistingUser()
        every {
            userRepositoryOutputPort.findByEmail(newEmail)
        } returns buildUser(email = newEmail)

        val exception =
            assertThrows<UserAlreadyExistsException> {
                execute(email = newEmail)
            }

        assertEquals(EMAIL_ALREADY_EXISTS, exception.message)

        verify(exactly = 0) {
            userRepositoryOutputPort.update(any())
            passwordEncoderOutputPort.encode(any())
        }
    }

    @Test
    fun `should propagate exception when password encoding fails`() {
        mockExistingUser()

        every {
            passwordEncoderOutputPort.encode(RAW_PASSWORD)
        } throws IllegalStateException(PASSWORD_ENCODING_FAILED)

        val exception =
            assertThrows<IllegalStateException> {
                execute(password = RAW_PASSWORD)
            }

        assertEquals(PASSWORD_ENCODING_FAILED, exception.message)

        verify(exactly = 0) {
            userRepositoryOutputPort.update(any())
        }
    }

    @Test
    fun `should propagate exception when persistence fails`() {
        mockExistingUser()

        every {
            userRepositoryOutputPort.update(any())
        } throws IllegalStateException(PERSISTENCE_FAILED)

        val exception =
            assertThrows<IllegalStateException> {
                execute(name = "Updated Name")
            }

        assertEquals(PERSISTENCE_FAILED, exception.message)

        verify(exactly = 1) {
            userRepositoryOutputPort.update(any())
        }
    }

    @Test
    fun `should preserve immutable and non editable user fields`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(name = "Updated Name")

        assertEquals(userId, result.userId)
        assertEquals(role, result.role)
        assertEquals(status, result.status)
        assertEquals(cpf, result.cpf)
        assertEquals(rg, result.rg)
        assertEquals(birthDate, result.birthDate)
        assertEquals(createdAt, result.createdAt)
        assertTrue(result.updatedAt >= updatedAt)
    }

    @Test
    fun `should update cpf when current cpf is null`() {
        user = buildUser(cpf = null)
        val newCpf = "613.098.083-33"

        mockExistingUser()
        every { userRepositoryOutputPort.existsByCpf(newCpf) } returns false
        mockSuccessfulUpdate()

        val result = execute(cpf = newCpf)

        assertEquals(newCpf, result.cpf?.value)

        verify(exactly = 1) {
            userRepositoryOutputPort.existsByCpf(newCpf)
            userRepositoryOutputPort.update(any())
        }
    }

    @Test
    fun `should throw exception when cpf is already registered`() {
        mockExistingUser()

        val exception =
            assertThrows<UserAlreadyExistsException> {
                execute(cpf = "613.098.083-33")
            }

        assertEquals(CPF_ALREADY_REGISTERED, exception.message)

        verify(exactly = 0) {
            userRepositoryOutputPort.existsByCpf(any())
            userRepositoryOutputPort.update(any())
        }
    }

    @Test
    fun `should throw exception when rg is already registered`() {
        mockExistingUser()

        val exception =
            assertThrows<UserAlreadyExistsException> {
                execute(rg = "05.123.456/2018-7")
            }

        assertEquals(RG_ALREADY_REGISTERED, exception.message)

        verify(exactly = 0) {
            userRepositoryOutputPort.existsByRg(any())
            userRepositoryOutputPort.update(any())
        }
    }

    @Test
    fun `should throw exception when rg already exists`() {
        user = buildUser(rg = null)
        val newRg = "05.123.456/2018-7"

        mockExistingUser()
        every { userRepositoryOutputPort.existsByRg(newRg) } returns true

        val exception =
            assertThrows<UserAlreadyExistsException> {
                execute(rg = newRg)
            }

        assertEquals(RG_ALREADY_EXISTS, exception.message)

        verify(exactly = 1) {
            userRepositoryOutputPort.existsByRg(newRg)
        }

        verify(exactly = 0) {
            userRepositoryOutputPort.update(any())
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = [" ", "   "])
    fun `should preserve current rg when new rg is null or blank`(newRg: String?) {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(rg = newRg)

        assertEquals(rg, result.rg)

        verify(exactly = 0) {
            userRepositoryOutputPort.existsByRg(any())
        }
    }

    @Test
    fun `should throw exception when rg format is invalid`() {
        user = buildUser(rg = null)

        mockExistingUser()

        val exception =
            assertThrows<IllegalArgumentException> {
                execute(rg = "12345ABC")
            }

        assertEquals(RG_INVALID, exception.message)

        verify(exactly = 0) {
            userRepositoryOutputPort.existsByRg(any())
            userRepositoryOutputPort.update(any())
        }
    }

    @Test
    fun `should throw exception when cpf already exists`() {
        user = buildUser(cpf = null)
        val newCpf = "613.098.083-33"

        mockExistingUser()
        every { userRepositoryOutputPort.existsByCpf(newCpf) } returns true

        val exception =
            assertThrows<UserAlreadyExistsException> {
                execute(cpf = newCpf)
            }

        assertEquals(CPF_ALREADY_EXISTS, exception.message)

        verify(exactly = 1) {
            userRepositoryOutputPort.existsByCpf(newCpf)
        }

        verify(exactly = 0) {
            userRepositoryOutputPort.update(any())
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = [" ", "   "])
    fun `should preserve current cpf when new cpf is null or blank`(newCpf: String?) {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(cpf = newCpf)

        assertEquals(cpf, result.cpf)

        verify(exactly = 0) {
            userRepositoryOutputPort.existsByCpf(any())
        }
    }

    @Test
    fun `should update rg when it does not exist`() {
        user = buildUser(rg = null)
        val newRg = "05.123.456/2018-7"

        mockExistingUser()
        every { userRepositoryOutputPort.existsByRg(newRg) } returns false
        mockSuccessfulUpdate()

        val result = execute(rg = newRg)

        assertEquals(newRg, result.rg?.value)

        verify(exactly = 1) {
            userRepositoryOutputPort.existsByRg(newRg)
            userRepositoryOutputPort.update(any())
        }
    }

    @Test
    fun `should update login successfully`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        every { userRepositoryOutputPort.existsByLogin("novologin") } returns false

        val result = execute(login = "novologin")

        assertEquals("novologin", result.login.value)
        verify(exactly = 1) { userRepositoryOutputPort.existsByLogin("novologin") }
    }

    @Test
    fun `should throw exception when login already exists`() {
        mockExistingUser()

        every { userRepositoryOutputPort.existsByLogin("loginexistente") } returns true

        assertThrows<UserAlreadyExistsException> {
            execute(login = "loginexistente")
        }

        verify(exactly = 0) { userRepositoryOutputPort.update(any()) }
    }

    @Test
    fun `should keep current login when same login is provided`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(login = user.login.value)

        assertEquals(user.login, result.login)
        verify(exactly = 0) { userRepositoryOutputPort.existsByLogin(any()) }
    }

    @Test
    fun `should keep current login when login is null`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(login = null)

        assertEquals(user.login, result.login)
        verify(exactly = 0) { userRepositoryOutputPort.existsByLogin(any()) }
    }

    @Test
    fun `should keep current login when login is blank`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(login = "")

        assertEquals(user.login, result.login)
        verify(exactly = 0) { userRepositoryOutputPort.existsByLogin(any()) }
    }

    @Test
    fun `should reject invalid login`() {
        mockExistingUser()

        assertThrows<IllegalArgumentException> {
            execute(login = "LoginInvalido")
        }

        verify(exactly = 0) { userRepositoryOutputPort.update(any()) }
    }

    @Test
    fun `should update birth date when current birth date is null`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val birthDate = LocalDate.of(2000, 5, 15)

        val result = execute(birthDate = birthDate)

        assertEquals(birthDate, result.birthDate)
    }

    @Test
    fun `should keep current birth date when new birth date is null`() {
        mockExistingUser()
        mockSuccessfulUpdate()

        val result = execute(birthDate = null)

        assertEquals(user.birthDate, result.birthDate)
    }

    @Test
    fun `should throw exception when birth date is in the future`() {
        mockExistingUser()

        val birthDate = LocalDate.now().plusDays(1)

        val exception =
            assertThrows<IllegalArgumentException> {
                execute(birthDate = birthDate)
            }

        assertEquals(BIRTH_DATE_CANNOT_BE_FUTURE, exception.message)
        verify(exactly = 0) { userRepositoryOutputPort.update(any()) }
    }

    @Test
    fun `should throw exception when birth date is already registered`() {
        val existingUser =
            buildUser(
                userId = userId,
                birthDate = LocalDate.of(2000, 5, 15),
            )

        every { userRepositoryOutputPort.findById(userId) } returns existingUser

        val exception =
            assertThrows<IllegalArgumentException> {
                execute(birthDate = LocalDate.of(1995, 10, 20))
            }

        assertEquals(BIRTH_DATE_ALREADY_REGISTERED, exception.message)

        verify(exactly = 0) {
            userRepositoryOutputPort.update(any())
        }
    }

    private fun execute(
        name: String? = null,
        email: String? = null,
        login: String? = null,
        password: String? = null,
        phone: String? = null,
        address: String? = null,
        cpf: String? = null,
        rg: String? = null,
        birthDate: LocalDate? = null,
    ): User =
        updateUserUseCaseImpl.execute(
            userId = userId,
            name = name,
            email = email,
            login = login,
            password = password,
            phone = phone,
            address = address,
            cpf = cpf,
            rg = rg,
            birthDate = birthDate,
        )

    private fun mockExistingUser() {
        every {
            userRepositoryOutputPort.findById(userId)
        } returns user
    }

    private fun mockSuccessfulUpdate() {
        every {
            userRepositoryOutputPort.update(any())
        } answers { firstArg() }
    }

    companion object {
        private const val RAW_PASSWORD = "Diad@123456"
        private const val ENCODED_PASSWORD = "encoded-password"
        private const val VALID_PHONE = "98999999999"
    }
}
