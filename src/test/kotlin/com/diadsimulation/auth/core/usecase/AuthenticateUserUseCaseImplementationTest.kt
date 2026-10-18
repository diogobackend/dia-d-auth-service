package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.common.Messages.INVALID_CREDENTIALS
import com.diadsimulation.auth.core.common.Messages.USER_NOT_ACTIVE
import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserStatus
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import com.diadsimulation.auth.core.domain.enums.UserStatus.BLOCKED
import com.diadsimulation.auth.core.domain.enums.UserStatus.INACTIVE
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.output.PasswordEncoderOutputPort
import com.diadsimulation.auth.core.port.output.TokenGeneratorOutputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.hibernate.internal.util.collections.CollectionHelper.listOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.security.authentication.BadCredentialsException
import java.util.UUID

@ExtendWith(MockKExtension::class)
class AuthenticateUserUseCaseImplementationTest {
    @MockK
    private lateinit var userRepositoryOutputPort: UserRepositoryOutputPort

    @MockK
    private lateinit var passwordEncoderOutputPort: PasswordEncoderOutputPort

    @MockK
    private lateinit var tokenGeneratorOutputPort: TokenGeneratorOutputPort

    @InjectMockKs
    private lateinit var authenticateUserUseCaseImplementation:
        AuthenticateUserUseCaseImplementation

    private fun user(
        status: UserStatus = ACTIVE,
        role: UserRole = CANDIDATE,
    ): User =
        mockk {
            every { userId } returns UUID.randomUUID()
            every { passwordHash } returns PASSWORD_HASH
            every { this@mockk.status } returns status
            every { this@mockk.role } returns role
        }

    private fun mockUserFound(user: User) {
        every {
            userRepositoryOutputPort.findByEmail(EMAIL)
        } returns user
    }

    private fun mockPasswordMatches(matches: Boolean) {
        every {
            passwordEncoderOutputPort.matches(PASSWORD, PASSWORD_HASH)
        } returns matches
    }

    @ParameterizedTest
    @EnumSource(UserRole::class)
    fun `should authenticate active user successfully`(role: UserRole) {
        val user = user(role = role)

        mockUserFound(user)
        mockPasswordMatches(true)

        every {
            tokenGeneratorOutputPort.generate(user.userId, role.name)
        } returns TOKEN

        val result =
            authenticateUserUseCaseImplementation.execute(
                email = EMAIL,
                password = PASSWORD,
                login = LOGIN,
            )

        assertEquals(TOKEN, result)

        verify(exactly = 1) {
            userRepositoryOutputPort.findByEmail(EMAIL)
            passwordEncoderOutputPort.matches(PASSWORD, PASSWORD_HASH)
            tokenGeneratorOutputPort.generate(user.userId, role.name)
        }
    }

    @Test
    fun `should throw bad credentials when user does not exist`() {
        every {
            userRepositoryOutputPort.findByEmail(EMAIL)
        } returns null

        val exception =
            assertThrows(
                BadCredentialsException::class.java,
            ) {
                authenticateUserUseCaseImplementation.execute(
                    email = EMAIL,
                    password = PASSWORD,
                    login = LOGIN,
                )
            }

        assertEquals(INVALID_CREDENTIALS, exception.message)

        verify(exactly = 1) {
            userRepositoryOutputPort.findByEmail(EMAIL)
        }

        verify {
            passwordEncoderOutputPort wasNot Called
            tokenGeneratorOutputPort wasNot Called
        }
    }

    @Test
    fun `should throw bad credentials when password does not match`() {
        val user = user()

        mockUserFound(user)
        mockPasswordMatches(false)

        val exception =
            assertThrows(
                BadCredentialsException::class.java,
            ) {
                authenticateUserUseCaseImplementation.execute(
                    email = EMAIL,
                    password = PASSWORD,
                    login = LOGIN,
                )
            }

        assertEquals(INVALID_CREDENTIALS, exception.message)

        verify(exactly = 1) {
            userRepositoryOutputPort.findByEmail(EMAIL)
            passwordEncoderOutputPort.matches(PASSWORD, PASSWORD_HASH)
        }

        verify {
            tokenGeneratorOutputPort wasNot Called
        }
    }

    @ParameterizedTest
    @MethodSource("inactiveStatuses")
    fun `should throw exception when user is not active`(status: UserStatus) {
        val user = user(status = status)

        mockUserFound(user)
        mockPasswordMatches(true)

        val exception =
            assertThrows(
                IllegalStateException::class.java,
            ) {
                authenticateUserUseCaseImplementation.execute(
                    email = EMAIL,
                    password = PASSWORD,
                    login = LOGIN,
                )
            }

        assertEquals(USER_NOT_ACTIVE, exception.message)

        verify(exactly = 1) {
            userRepositoryOutputPort.findByEmail(EMAIL)
            passwordEncoderOutputPort.matches(PASSWORD, PASSWORD_HASH)
        }

        verify {
            tokenGeneratorOutputPort wasNot Called
        }
    }

    @Test
    fun `should authenticate user by login successfully`() {
        val user = user(role = CANDIDATE)

        every { userRepositoryOutputPort.findByLogin(LOGIN) } returns user
        mockPasswordMatches(true)

        every {
            tokenGeneratorOutputPort.generate(user.userId, CANDIDATE.name)
        } returns TOKEN

        val result =
            authenticateUserUseCaseImplementation.execute(
                email = null,
                login = LOGIN,
                password = PASSWORD,
            )

        assertEquals(TOKEN, result)

        verify(exactly = 1) {
            userRepositoryOutputPort.findByLogin(LOGIN)
            passwordEncoderOutputPort.matches(PASSWORD, PASSWORD_HASH)
            tokenGeneratorOutputPort.generate(user.userId, CANDIDATE.name)
        }

        verify(exactly = 0) {
            userRepositoryOutputPort.findByEmail(any())
        }
    }

    @Test
    fun `should throw bad credentials when login does not exist`() {
        every { userRepositoryOutputPort.findByLogin(LOGIN) } returns null

        val exception =
            assertThrows<BadCredentialsException> {
                authenticateUserUseCaseImplementation.execute(
                    email = null,
                    login = LOGIN,
                    password = PASSWORD,
                )
            }

        assertEquals(INVALID_CREDENTIALS, exception.message)

        verify(exactly = 1) {
            userRepositoryOutputPort.findByLogin(LOGIN)
        }

        verify {
            passwordEncoderOutputPort wasNot Called
            tokenGeneratorOutputPort wasNot Called
        }
    }

    @Test
    fun `should throw bad credentials when email and login are null`() {
        val exception =
            assertThrows<BadCredentialsException> {
                authenticateUserUseCaseImplementation.execute(
                    email = null,
                    login = null,
                    password = PASSWORD,
                )
            }

        assertEquals(INVALID_CREDENTIALS, exception.message)

        verify {
            userRepositoryOutputPort wasNot Called
            passwordEncoderOutputPort wasNot Called
            tokenGeneratorOutputPort wasNot Called
        }
    }

    @Test
    fun `should authenticate by login when email is blank`() {
        val user = user(role = CANDIDATE)

        every { userRepositoryOutputPort.findByLogin(LOGIN) } returns user
        mockPasswordMatches(true)

        every {
            tokenGeneratorOutputPort.generate(user.userId, CANDIDATE.name)
        } returns TOKEN

        val result =
            authenticateUserUseCaseImplementation.execute(
                email = "",
                login = LOGIN,
                password = PASSWORD,
            )

        assertEquals(TOKEN, result)

        verify(exactly = 1) {
            userRepositoryOutputPort.findByLogin(LOGIN)
            passwordEncoderOutputPort.matches(PASSWORD, PASSWORD_HASH)
            tokenGeneratorOutputPort.generate(user.userId, CANDIDATE.name)
        }

        verify(exactly = 0) {
            userRepositoryOutputPort.findByEmail(any())
        }
    }

    @Test
    fun `should throw bad credentials when login is blank and email is null`() {
        val exception =
            assertThrows<BadCredentialsException> {
                authenticateUserUseCaseImplementation.execute(
                    email = null,
                    login = "",
                    password = PASSWORD,
                )
            }

        assertEquals(INVALID_CREDENTIALS, exception.message)

        verify {
            userRepositoryOutputPort wasNot Called
            passwordEncoderOutputPort wasNot Called
            tokenGeneratorOutputPort wasNot Called
        }
    }

    @Test
    fun `should throw bad credentials when email and login are blank`() {
        val exception =
            assertThrows<BadCredentialsException> {
                authenticateUserUseCaseImplementation.execute(
                    email = "",
                    login = "",
                    password = PASSWORD,
                )
            }

        assertEquals(INVALID_CREDENTIALS, exception.message)

        verify {
            userRepositoryOutputPort wasNot Called
            passwordEncoderOutputPort wasNot Called
            tokenGeneratorOutputPort wasNot Called
        }
    }

    companion object {
        private const val EMAIL = "teste@email.com"
        private const val PASSWORD = "Diad@123456"
        private const val LOGIN = "dio123"
        private const val PASSWORD_HASH = "encoded-password"
        private const val TOKEN = "generated-jwt-token"

        @JvmStatic
        fun inactiveStatuses() =
            listOf(
                INACTIVE,
                BLOCKED,
            )
    }
}
