package com.diadsimulation.auth.app.adapter.input.web.security

import com.diadsimulation.auth.core.common.Messages.ACCESS_DENIED
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.util.UUID
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class UserAccessAuthorizationTest {
    private val userAccessAuthorization = UserAccessAuthorization()

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
        role: String = "ROLE_CANDIDATE",
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
            role = "ROLE_PLATFORM_ADMIN",
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

    companion object {
        @JvmStatic
        fun invalidPrincipals(): Stream<Any> =
            Stream.of(
                "invalid-user-id",
                123L,
                Any(),
            )
    }
}
