package com.diadsimulation.auth.app.adapter.input.web.security

import com.diadsimulation.auth.builders.buildUser
import com.diadsimulation.auth.core.common.Messages.ACCESS_DENIED
import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.util.UUID
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class UserAccessAuthorizationTest {
    @MockK
    private lateinit var userRepositoryOutputPort: UserRepositoryOutputPort

    private val userAccessAuthorization by lazy {
        UserAccessAuthorization(userRepositoryOutputPort)
    }

    private lateinit var userId: UUID

    @BeforeEach
    fun setUp() {
        SecurityContextHolder.clearContext()
        userId = UUID.randomUUID()
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    private fun authenticate(
        principal: Any = userId,
        role: String = ROLE_CANDIDATE,
    ) {
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(
                principal,
                null,
                listOf(SimpleGrantedAuthority(role)),
            )
    }

    private fun assertAccessDenied(requestedUserId: UUID = userId) {
        val exception =
            assertThrows(AccessDeniedException::class.java) {
                userAccessAuthorization.authorize(requestedUserId)
            }

        assertEquals(ACCESS_DENIED, exception.message)
    }

    @Test
    fun `should deny access when authentication is missing`() {
        assertAccessDenied()
    }

    @Test
    fun `should deny access when authentication is not authenticated`() {
        val authentication = mockk<Authentication>()

        every { authentication.isAuthenticated } returns false

        SecurityContextHolder.getContext().authentication = authentication

        assertAccessDenied()
    }

    @Test
    fun `should allow platform admin to access another user`() {
        authenticate(
            role = ROLE_PLATFORM_ADMIN,
        )

        assertDoesNotThrow {
            userAccessAuthorization.authorize(UUID.randomUUID())
        }
    }

    @Test
    fun `should allow candidate to access own user`() {
        authenticate()

        assertDoesNotThrow {
            userAccessAuthorization.authorize(userId)
        }
    }

    @Test
    fun `should deny candidate access to another user`() {
        authenticate()

        assertAccessDenied(UUID.randomUUID())
    }

    @ParameterizedTest
    @MethodSource("invalidPrincipals")
    fun `should deny access when principal is not UUID`(principal: Any) {
        authenticate(principal = principal)

        assertAccessDenied()
    }

    @Test
    fun `should allow school admin to access own user`() {
        authenticate(role = ROLE_SCHOOL_ADMIN)

        assertDoesNotThrow {
            userAccessAuthorization.authorize(userId)
        }
    }

    @Test
    fun `should allow school admin to access candidate`() {
        val candidateId = UUID.randomUUID()
        val candidate =
            buildUser(userId = candidateId).apply {
                role = CANDIDATE
            }

        authenticate(role = ROLE_SCHOOL_ADMIN)
        every { userRepositoryOutputPort.findById(candidateId) } returns candidate

        assertDoesNotThrow {
            userAccessAuthorization.authorize(candidateId)
        }

        verify(exactly = 1) {
            userRepositoryOutputPort.findById(candidateId)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["SCHOOL_ADMIN", "PLATFORM_ADMIN"])
    fun `should deny school admin access to other admins`(targetRole: String) {
        val targetId = UUID.randomUUID()
        val target =
            buildUser(userId = targetId).apply {
                role = UserRole.valueOf(targetRole)
            }

        authenticate(role = ROLE_SCHOOL_ADMIN)
        every { userRepositoryOutputPort.findById(targetId) } returns target

        assertAccessDenied(targetId)
    }

    @Test
    fun `should deny school admin access when target user does not exist`() {
        val targetId = UUID.randomUUID()

        authenticate(role = ROLE_SCHOOL_ADMIN)
        every { userRepositoryOutputPort.findById(targetId) } returns null

        assertAccessDenied(targetId)
    }

    @Test
    fun `should deny list access when authentication is missing`() {
        assertThrows(AccessDeniedException::class.java) {
            userAccessAuthorization.getListAccess()
        }
    }

    @Test
    fun `should deny list access when authentication is not authenticated`() {
        val authentication = mockk<Authentication>()
        every { authentication.isAuthenticated } returns false

        SecurityContextHolder.getContext().authentication = authentication

        assertThrows(AccessDeniedException::class.java) {
            userAccessAuthorization.getListAccess()
        }
    }

    @Test
    fun `should allow platform admin to list all users`() {
        authenticate(role = ROLE_PLATFORM_ADMIN)

        val result = userAccessAuthorization.getListAccess()

        assertNull(result.first)
        assertNull(result.second)
    }

    @Test
    fun `should allow school admin to list candidates and own user`() {
        authenticate(role = ROLE_SCHOOL_ADMIN)

        val result = userAccessAuthorization.getListAccess()

        assertEquals(CANDIDATE, result.first)
        assertEquals(userId, result.second)
    }

    @Test
    fun `should deny candidate access to user list`() {
        authenticate(role = ROLE_CANDIDATE)

        assertThrows(AccessDeniedException::class.java) {
            userAccessAuthorization.getListAccess()
        }
    }

    @ParameterizedTest
    @MethodSource("invalidPrincipals")
    fun `should deny list access when school admin principal is not UUID`(principal: Any) {
        authenticate(principal = principal, role = ROLE_SCHOOL_ADMIN)

        assertThrows(AccessDeniedException::class.java) {
            userAccessAuthorization.getListAccess()
        }
    }

    companion object {
        const val ROLE_SCHOOL_ADMIN = "ROLE_SCHOOL_ADMIN"
        const val ROLE_CANDIDATE = "ROLE_CANDIDATE"
        const val ROLE_PLATFORM_ADMIN = "ROLE_PLATFORM_ADMIN"

        @JvmStatic
        fun invalidPrincipals(): Stream<Any> =
            Stream.of(
                "invalid-user-id",
                123L,
                Any(),
            )
    }
}
