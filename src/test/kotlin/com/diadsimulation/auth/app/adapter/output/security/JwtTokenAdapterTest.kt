package com.diadsimulation.auth.app.adapter.output.security

import com.diadsimulation.auth.core.domain.enums.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.UUID

class JwtTokenAdapterTest {
    private val jwtTokenAdapter =
        JwtTokenAdapter(
            secret = SECRET,
            expirationMs = EXPIRATION_MS,
        )

    private fun parseClaims(token: String): Claims =
        Jwts
            .parser()
            .verifyWith(
                Keys.hmacShaKeyFor(
                    SECRET.toByteArray(StandardCharsets.UTF_8),
                ),
            ).build()
            .parseSignedClaims(token)
            .payload

    @ParameterizedTest
    @EnumSource(UserRole::class)
    fun `should generate valid JWT with user id and role`(role: UserRole) {
        val userId = UUID.randomUUID()

        val token =
            jwtTokenAdapter.generate(
                userId = userId,
                role = role.name,
            )

        val claims = parseClaims(token)

        assertEquals(userId.toString(), claims.subject)
        assertEquals(role.name, claims[ROLE_CLAIM])
        assertNotNull(claims.issuedAt)
        assertNotNull(claims.expiration)
    }

    @Test
    fun `should generate signed JWT with three parts`() {
        val token =
            jwtTokenAdapter.generate(
                userId = UUID.randomUUID(),
                role = UserRole.CANDIDATE.name,
            )

        assertEquals(JWT_PARTS, token.split(".").size)
        assertTrue(token.isNotBlank())

        assertNotNull(parseClaims(token))
    }

    @Test
    fun `should generate JWT with configured expiration`() {
        val token =
            jwtTokenAdapter.generate(
                userId = UUID.randomUUID(),
                role = UserRole.CANDIDATE.name,
            )

        val claims = parseClaims(token)

        val actualExpirationMs =
            claims.expiration.time - claims.issuedAt.time

        assertEquals(EXPIRATION_MS, actualExpirationMs)
    }

    @Test
    fun `should generate JWT with current issued at`() {
        val before = Instant.now().minusSeconds(1)

        val token =
            jwtTokenAdapter.generate(
                userId = UUID.randomUUID(),
                role = UserRole.CANDIDATE.name,
            )

        val after = Instant.now().plusSeconds(1)
        val claims = parseClaims(token)

        val issuedAt = claims.issuedAt.toInstant()

        assertTrue(!issuedAt.isBefore(before))
        assertTrue(!issuedAt.isAfter(after))
    }

    @ParameterizedTest
    @MethodSource("invalidSecrets")
    fun `should reject JWT secret shorter than 32 bytes`(invalidSecret: String) {
        val adapter =
            JwtTokenAdapter(
                secret = invalidSecret,
                expirationMs = EXPIRATION_MS,
            )

        val exception =
            assertThrows(
                IllegalArgumentException::class.java,
            ) {
                adapter.generate(
                    userId = UUID.randomUUID(),
                    role = UserRole.CANDIDATE.name,
                )
            }

        assertEquals(
            "JWT secret must contain at least 32 bytes",
            exception.message,
        )
    }

    @Test
    fun `should accept JWT secret with exactly 32 bytes`() {
        val exactSecret = "12345678901234567890123456789012"

        val adapter =
            JwtTokenAdapter(
                secret = exactSecret,
                expirationMs = EXPIRATION_MS,
            )

        val token =
            adapter.generate(
                userId = UUID.randomUUID(),
                role = UserRole.CANDIDATE.name,
            )

        val claims =
            Jwts
                .parser()
                .verifyWith(
                    Keys.hmacShaKeyFor(
                        exactSecret.toByteArray(StandardCharsets.UTF_8),
                    ),
                ).build()
                .parseSignedClaims(token)
                .payload

        assertEquals(
            UserRole.CANDIDATE.name,
            claims[ROLE_CLAIM],
        )
    }

    @Test
    fun `should reject token verification with different secret`() {
        val token =
            jwtTokenAdapter.generate(
                userId = UUID.randomUUID(),
                role = UserRole.CANDIDATE.name,
            )

        val differentSecret =
            "another-jwt-test-secret-key-123456789012345"

        assertThrows(
            io.jsonwebtoken.security.SignatureException::class.java,
        ) {
            Jwts
                .parser()
                .verifyWith(
                    Keys.hmacShaKeyFor(
                        differentSecret.toByteArray(StandardCharsets.UTF_8),
                    ),
                ).build()
                .parseSignedClaims(token)
        }
    }

    companion object {
        private const val SECRET =
            "diad-simulation-jwt-test-secret-key-123456789"

        private const val EXPIRATION_MS = 900_000L
        private const val ROLE_CLAIM = "role"
        private const val JWT_PARTS = 3

        @JvmStatic
        fun invalidSecrets(): List<String> =
            listOf(
                "",
                "short-secret",
                "1234567890123456789012345678901",
            )
    }
}
