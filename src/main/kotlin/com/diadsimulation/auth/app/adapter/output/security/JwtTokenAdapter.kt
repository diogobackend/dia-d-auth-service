package com.diadsimulation.auth.app.adapter.output.security

import com.diadsimulation.auth.core.port.output.TokenGeneratorOutputPort
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import java.util.UUID

@Component
class JwtTokenAdapter(
    @Value("\${security.jwt.secret}")
    private val secret: String,
    @Value("\${security.jwt.expiration-ms}")
    private val expirationMs: Long,
) : TokenGeneratorOutputPort {
    private val signingKey by lazy {
        require(secret.toByteArray(StandardCharsets.UTF_8).size >= 32) {
            "JWT secret must contain at least 32 bytes"
        }

        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    override fun generate(
        userId: UUID,
        role: String,
    ): String {
        val issuedAt = Instant.now()
        val expiresAt = issuedAt.plusMillis(expirationMs)

        return Jwts
            .builder()
            .subject(userId.toString())
            .claim("role", role)
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(expiresAt))
            .signWith(signingKey)
            .compact()
    }
}
