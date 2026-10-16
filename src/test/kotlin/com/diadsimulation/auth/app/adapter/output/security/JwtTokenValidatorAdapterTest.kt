package com.diadsimulation.auth.app.adapter.output.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.PrematureJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import io.jsonwebtoken.security.WeakKeyException
import org.hibernate.internal.util.collections.CollectionHelper.listOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import java.util.UUID
import java.util.stream.Stream

class JwtTokenValidatorAdapterTest {
    private val jwtTokenValidatorAdapter =
        JwtTokenValidatorAdapter(SECRET)

    private val jwtTokenAdapter =
        JwtTokenAdapter(
            secret = SECRET,
            expirationMs = EXPIRATION_MS,
        )

    private fun signingKey(secret: String = SECRET) =
        Keys.hmacShaKeyFor(
            secret.toByteArray(StandardCharsets.UTF_8),
        )

    private fun generateToken(
        userId: UUID = UUID.randomUUID(),
        role: String = CANDIDATE_ROLE,
    ): String =
        jwtTokenAdapter.generate(
            userId = userId,
            role = role,
        )

    private fun customToken(
        subject: String = UUID.randomUUID().toString(),
        role: Any? = CANDIDATE_ROLE,
        secret: String = SECRET,
        issuedAt: Instant = Instant.now(),
        expiresAt: Instant = issuedAt.plusMillis(EXPIRATION_MS),
        includeRole: Boolean = true,
    ): String {
        val builder =
            Jwts
                .builder()
                .subject(subject)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))

        if (includeRole) {
            builder.claim(ROLE_CLAIM, role)
        }

        return builder
            .signWith(signingKey(secret))
            .compact()
    }

    @ParameterizedTest
    @MethodSource("validRoles")
    fun `should validate token with valid user id and role`(role: String) {
        val userId = UUID.randomUUID()
        val token = generateToken(userId, role)

        val result = jwtTokenValidatorAdapter.validate(token)

        assertEquals(userId, result.userId)
        assertEquals(role, result.role)
    }

    @Test
    fun `should reject expired token`() {
        val issuedAt = Instant.now().minusSeconds(3600)
        val expiresAt = issuedAt.plusSeconds(900)

        val token =
            customToken(
                issuedAt = issuedAt,
                expiresAt = expiresAt,
            )

        assertThrows(ExpiredJwtException::class.java) {
            jwtTokenValidatorAdapter.validate(token)
        }
    }

    @Test
    fun `should reject token signed with different secret`() {
        val token =
            customToken(
                secret = DIFFERENT_SECRET,
            )

        assertThrows(SignatureException::class.java) {
            jwtTokenValidatorAdapter.validate(token)
        }
    }

    @ParameterizedTest
    @MethodSource("invalidTokens")
    fun `should reject malformed token`(
        token: String,
        expectedException: Class<out Throwable>,
    ) {
        assertThrows(expectedException) {
            jwtTokenValidatorAdapter.validate(token)
        }
    }

    @Test
    fun `should reject token with invalid user id`() {
        val token =
            customToken(
                subject = "invalid-uuid",
            )

        assertThrows(IllegalArgumentException::class.java) {
            jwtTokenValidatorAdapter.validate(token)
        }
    }

    @Test
    fun `should reject token without role claim`() {
        val token =
            customToken(
                includeRole = false,
            )

        assertThrows(NullPointerException::class.java) {
            jwtTokenValidatorAdapter.validate(token)
        }
    }

    @Test
    fun `should reject token with non string role claim`() {
        val token =
            customToken(
                role = 123,
            )

        assertThrows(ClassCastException::class.java) {
            jwtTokenValidatorAdapter.validate(token)
        }
    }

    @Test
    fun `should reject token with future issued at`() {
        val issuedAt = Instant.now().plusSeconds(3600)

        val token =
            customToken(
                issuedAt = issuedAt,
                expiresAt = issuedAt.plusSeconds(900),
            )

        val result = jwtTokenValidatorAdapter.validate(token)

        assertEquals(CANDIDATE_ROLE, result.role)
    }

    @Test
    fun `should reject token with future not before`() {
        val token =
            Jwts
                .builder()
                .subject(UUID.randomUUID().toString())
                .claim(ROLE_CLAIM, CANDIDATE_ROLE)
                .notBefore(
                    Date.from(Instant.now().plusSeconds(3600)),
                ).expiration(
                    Date.from(Instant.now().plusSeconds(4500)),
                ).signWith(signingKey())
                .compact()

        assertThrows(PrematureJwtException::class.java) {
            jwtTokenValidatorAdapter.validate(token)
        }
    }

    @Test
    fun `should reject weak signing secret`() {
        val adapter = JwtTokenValidatorAdapter(SHORT_SECRET)

        val token = generateToken()

        assertThrows(WeakKeyException::class.java) {
            adapter.validate(token)
        }
    }

    companion object {
        private const val SECRET =
            "diad-simulation-jwt-test-secret-key-123456789"

        private const val DIFFERENT_SECRET =
            "another-jwt-test-secret-key-123456789012345"

        private const val SHORT_SECRET = "short-secret"

        private const val EXPIRATION_MS = 900_000L

        private const val ROLE_CLAIM = "role"
        private const val CANDIDATE_ROLE = "CANDIDATE"
        private const val ADMIN_ROLE = "PLATFORM_ADMIN"

        @JvmStatic
        fun validRoles(): Stream<String> =
            Stream.of(
                CANDIDATE_ROLE,
                ADMIN_ROLE,
            )

        @JvmStatic
        fun invalidTokens() =
            listOf(
                Arguments.of("", IllegalArgumentException::class.java),
                Arguments.of("invalid-token", JwtException::class.java),
                Arguments.of("invalid.token", JwtException::class.java),
                Arguments.of("invalid.token.signature", JwtException::class.java),
            )
    }
}
