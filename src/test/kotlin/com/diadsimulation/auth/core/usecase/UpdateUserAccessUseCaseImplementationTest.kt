package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.builders.buildUser
import com.diadsimulation.auth.core.common.Messages.PERSISTENCE_FAILED
import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserRole.PLATFORM_ADMIN
import com.diadsimulation.auth.core.domain.enums.UserRole.SCHOOL_ADMIN
import com.diadsimulation.auth.core.domain.enums.UserStatus
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import com.diadsimulation.auth.core.domain.enums.UserStatus.BLOCKED
import com.diadsimulation.auth.core.domain.enums.UserStatus.INACTIVE
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
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
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.OffsetDateTime
import java.util.NoSuchElementException
import java.util.UUID
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class UpdateUserAccessUseCaseImplementationTest {
    @MockK
    private lateinit var userRepositoryOutputPort: UserRepositoryOutputPort

    @InjectMockKs
    private lateinit var updateUserAccessUseCaseImplementation: UpdateUserAccessUseCaseImplementation

    private lateinit var user: User

    private lateinit var userId: UUID
    private lateinit var role: UserRole
    private lateinit var status: UserStatus
    private lateinit var updatedAt: OffsetDateTime

    @BeforeEach
    fun setUp() {
        user = buildUser()
        userId = user.userId
        role = user.role
        status = user.status
        updatedAt = user.updatedAt
    }

    @ParameterizedTest
    @MethodSource("accessUpdates")
    fun `should update only user role or status`(
        role: UserRole?,
        status: UserStatus?,
    ) {
        every { userRepositoryOutputPort.findById(userId) } returns user
        every { userRepositoryOutputPort.updateAccess(any()) } answers { firstArg() }

        val expectedRole = role ?: this.role
        val expectedStatus = status ?: this.status

        val result =
            updateUserAccessUseCaseImplementation.execute(
                userId = userId,
                role = role,
                status = status,
            )

        assertEquals(expectedRole, result.role)
        assertEquals(expectedStatus, result.status)
        assertEquals(userId, result.userId)

        verify(exactly = 1) { userRepositoryOutputPort.findById(userId) }
        verify(exactly = 1) {
            userRepositoryOutputPort.updateAccess(
                match {
                    it.role == expectedRole &&
                        it.status == expectedStatus
                },
            )
        }
    }

    @Test
    fun `should update user role and status`() {
        every { userRepositoryOutputPort.findById(userId) } returns user
        every { userRepositoryOutputPort.updateAccess(any()) } answers { firstArg() }

        val result =
            updateUserAccessUseCaseImplementation.execute(
                userId = userId,
                role = PLATFORM_ADMIN,
                status = INACTIVE,
            )

        assertEquals(PLATFORM_ADMIN, result.role)
        assertEquals(INACTIVE, result.status)
        assertTrue(result.updatedAt >= updatedAt)

        verify(exactly = 1) {
            userRepositoryOutputPort.updateAccess(
                match {
                    it.role == PLATFORM_ADMIN &&
                        it.status == INACTIVE
                },
            )
        }
    }

    @Test
    fun `should preserve user data when updating access`() {
        every { userRepositoryOutputPort.findById(userId) } returns user
        every { userRepositoryOutputPort.updateAccess(any()) } answers { firstArg() }

        val result =
            updateUserAccessUseCaseImplementation.execute(
                userId = userId,
                role = SCHOOL_ADMIN,
                status = BLOCKED,
            )

        assertEquals(user.name, result.name)
        assertEquals(user.email, result.email)
        assertEquals(user.passwordHash, result.passwordHash)
        assertEquals(user.phone, result.phone)
        assertEquals(user.cpf, result.cpf)
        assertEquals(user.rg, result.rg)
        assertEquals(user.address, result.address)
        assertEquals(user.birthDate, result.birthDate)
        assertEquals(user.createdAt, result.createdAt)
    }

    @Test
    fun `should throw exception when user does not exist`() {
        every { userRepositoryOutputPort.findById(userId) } returns null

        val exception =
            assertThrows(NoSuchElementException::class.java) {
                updateUserAccessUseCaseImplementation.execute(
                    userId = userId,
                    role = SCHOOL_ADMIN,
                    status = null,
                )
            }

        assertEquals(USER_NOT_FOUND, exception.message)

        verify(exactly = 1) { userRepositoryOutputPort.findById(userId) }
        verify(exactly = 0) { userRepositoryOutputPort.updateAccess(any()) }
    }

    @Test
    fun `should propagate exception when updating access fails`() {
        every { userRepositoryOutputPort.findById(userId) } returns user
        every {
            userRepositoryOutputPort.updateAccess(any())
        } throws IllegalStateException(PERSISTENCE_FAILED)

        val exception =
            assertThrows(IllegalStateException::class.java) {
                updateUserAccessUseCaseImplementation.execute(
                    userId = userId,
                    role = SCHOOL_ADMIN,
                    status = null,
                )
            }

        assertEquals(PERSISTENCE_FAILED, exception.message)

        verify(exactly = 1) { userRepositoryOutputPort.updateAccess(any()) }
    }

    companion object {
        @JvmStatic
        fun accessUpdates(): Stream<Arguments> =
            Stream.of(
                Arguments.of(CANDIDATE, null),
                Arguments.of(SCHOOL_ADMIN, null),
                Arguments.of(PLATFORM_ADMIN, null),
                Arguments.of(null, ACTIVE),
                Arguments.of(null, INACTIVE),
                Arguments.of(null, BLOCKED),
            )
    }
}
