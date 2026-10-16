package com.diadsimulation.auth.app.adapter.input.web.security

import com.diadsimulation.auth.core.common.Messages.AUTHORIZATION
import com.diadsimulation.auth.core.common.Messages.BASIC_CREDENTIALS
import com.diadsimulation.auth.core.common.Messages.INVALID_CLAIMS_TYPE
import com.diadsimulation.auth.core.common.Messages.INVALID_JWT
import com.diadsimulation.auth.core.common.Messages.INVALID_TOKEN
import com.diadsimulation.auth.core.common.Messages.INVALID_TOKEN_MESSAGE
import com.diadsimulation.auth.core.common.Messages.LOWERCASE_BEARER_TOKEN
import com.diadsimulation.auth.core.common.Messages.VALID_TOKEN
import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserRole.PLATFORM_ADMIN
import com.diadsimulation.auth.core.domain.enums.UserStatus
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import com.diadsimulation.auth.core.domain.model.AuthenticatedUser
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.output.TokenValidatorOutputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import io.jsonwebtoken.JwtException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import java.util.UUID
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class JwtAuthenticationFilterTest {
    @MockK
    private lateinit var tokenValidatorOutputPort: TokenValidatorOutputPort

    @MockK
    private lateinit var userRepositoryOutputPort: UserRepositoryOutputPort

    @InjectMockKs
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse
    private lateinit var filterChain: FilterChain

    private lateinit var userId: UUID

    @BeforeEach
    fun setUp() {
        SecurityContextHolder.clearContext()

        userId = UUID.randomUUID()
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
        filterChain = mockk(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    private fun bearer(token: String = VALID_TOKEN) {
        request.addHeader(
            AUTHORIZATION,
            "Bearer $token",
        )
    }

    private fun authenticatedUser(role: String = CANDIDATE.name) =
        AuthenticatedUser(
            userId = userId,
            role = role,
        )

    private fun user(
        status: UserStatus = ACTIVE,
        role: UserRole = CANDIDATE,
    ): User {
        val existingUser = mockk<User>()

        every { existingUser.userId } returns userId
        every { existingUser.status } returns status
        every { existingUser.role } returns role

        return existingUser
    }

    private fun mockValidToken(
        status: UserStatus = ACTIVE,
        userRole: UserRole = CANDIDATE,
        tokenRole: String = CANDIDATE.name,
    ) {
        every {
            tokenValidatorOutputPort.validate(VALID_TOKEN)
        } returns authenticatedUser(role = tokenRole)

        every {
            userRepositoryOutputPort.findById(userId)
        } returns
            user(
                status = status,
                role = userRole,
            )
    }

    private fun executeFilter() {
        jwtAuthenticationFilter.doFilter(
            request,
            response,
            filterChain,
        )

        verify(exactly = 1) {
            filterChain.doFilter(request, response)
        }
    }

    private fun assertUnauthenticated() {
        assertNull(
            SecurityContextHolder.getContext().authentication,
        )
    }

    @ParameterizedTest
    @MethodSource("invalidAuthorizationHeaders")
    fun `should continue filter chain without valid bearer header`(authorization: String?) {
        authorization?.let {
            request.addHeader(AUTHORIZATION, it)
        }

        executeFilter()

        assertUnauthenticated()

        verify(exactly = 0) {
            tokenValidatorOutputPort.validate(any())
        }

        verify(exactly = 0) {
            userRepositoryOutputPort.findById(any())
        }
    }

    @Test
    fun `should authenticate active user with matching role`() {
        bearer()
        mockValidToken()

        executeFilter()

        val authentication =
            SecurityContextHolder.getContext().authentication

        assertNotNull(authentication)
        assertTrue(authentication?.isAuthenticated == true)
        assertEquals(userId, authentication?.principal)

        assertEquals(
            "ROLE_CANDIDATE",
            authentication?.authorities?.single()?.authority,
        )

        verify(exactly = 1) {
            tokenValidatorOutputPort.validate(VALID_TOKEN)
        }

        verify(exactly = 1) {
            userRepositoryOutputPort.findById(userId)
        }
    }

    @Test
    fun `should remove bearer prefix and trim token`() {
        request.addHeader(
            AUTHORIZATION,
            "Bearer   $VALID_TOKEN   ",
        )

        mockValidToken()

        executeFilter()

        val authentication =
            SecurityContextHolder.getContext().authentication

        assertNotNull(authentication)
        assertEquals(userId, authentication?.principal)

        verify(exactly = 1) {
            tokenValidatorOutputPort.validate(VALID_TOKEN)
        }
    }

    @Test
    fun `should clear authentication when user does not exist`() {
        bearer()

        every {
            tokenValidatorOutputPort.validate(VALID_TOKEN)
        } returns authenticatedUser()

        every {
            userRepositoryOutputPort.findById(userId)
        } returns null

        executeFilter()

        assertUnauthenticated()

        verify(exactly = 1) {
            userRepositoryOutputPort.findById(userId)
        }
    }

    @ParameterizedTest
    @EnumSource(
        value = UserStatus::class,
        names = ["INACTIVE", "BLOCKED"],
    )
    fun `should clear authentication when user is not active`(status: UserStatus) {
        bearer()
        mockValidToken(status = status)

        executeFilter()

        assertUnauthenticated()

        verify(exactly = 1) {
            userRepositoryOutputPort.findById(userId)
        }
    }

    @Test
    fun `should clear authentication when token role differs from user role`() {
        bearer()

        mockValidToken(
            userRole = CANDIDATE,
            tokenRole = UserRole.PLATFORM_ADMIN.name,
        )

        executeFilter()

        assertUnauthenticated()
    }

    @Test
    fun `should authenticate platform admin with matching role`() {
        bearer()

        mockValidToken(
            userRole = PLATFORM_ADMIN,
            tokenRole = PLATFORM_ADMIN.name,
        )

        executeFilter()

        val authentication =
            SecurityContextHolder.getContext().authentication

        assertNotNull(authentication)
        assertTrue(authentication?.isAuthenticated == true)
        assertEquals(userId, authentication?.principal)

        assertEquals(
            "ROLE_PLATFORM_ADMIN",
            authentication?.authorities?.single()?.authority,
        )
    }

    @ParameterizedTest
    @MethodSource("tokenValidationExceptions")
    fun `should clear authentication when token validation throws exception`(exception: RuntimeException) {
        bearer(INVALID_TOKEN)

        every {
            tokenValidatorOutputPort.validate(INVALID_TOKEN)
        } throws exception

        executeFilter()

        assertUnauthenticated()

        verify(exactly = 1) {
            tokenValidatorOutputPort.validate(INVALID_TOKEN)
        }

        verify(exactly = 0) {
            userRepositoryOutputPort.findById(any())
        }
    }

    companion object {
        @JvmStatic
        fun invalidAuthorizationHeaders(): Stream<Arguments> =
            Stream.of(
                Arguments.of(null as String?),
                Arguments.of(""),
                Arguments.of(BASIC_CREDENTIALS),
                Arguments.of(LOWERCASE_BEARER_TOKEN),
            )

        @JvmStatic
        fun tokenValidationExceptions(): Stream<Arguments> =
            Stream.of(
                Arguments.of(JwtException(INVALID_JWT)),
                Arguments.of(IllegalArgumentException(INVALID_TOKEN_MESSAGE)),
                Arguments.of(ClassCastException(INVALID_CLAIMS_TYPE)),
            )
    }
}
