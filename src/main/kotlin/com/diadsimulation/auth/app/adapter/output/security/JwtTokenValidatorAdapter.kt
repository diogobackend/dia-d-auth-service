package com.diadsimulation.auth.app.adapter.output.security

import com.diadsimulation.auth.core.domain.model.AuthenticatedUser
import com.diadsimulation.auth.core.port.output.TokenValidatorOutputPort
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.UUID

@Component
class JwtTokenValidatorAdapter(
    @Value("\${security.jwt.secret}")
    private val secret: String,
) : TokenValidatorOutputPort {
    private val signingKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    override fun validate(token: String): AuthenticatedUser {
        val claims =
            Jwts
                .parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload

        return AuthenticatedUser(
            userId = UUID.fromString(claims.subject),
            role = claims["role"] as String,
        )
    }
}
