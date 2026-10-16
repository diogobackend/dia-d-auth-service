package com.diadsimulation.auth.app.adapter.input.web.security

import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import com.diadsimulation.auth.core.port.output.TokenValidatorOutputPort
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val tokenValidatorOutputPort: TokenValidatorOutputPort,
    private val userRepositoryOutputPort: UserRepositoryOutputPort,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authorization = request.getHeader("Authorization")

        if (authorization?.startsWith("Bearer ") == true) {
            val token = authorization.removePrefix("Bearer ").trim()

            try {
                val authenticatedUser = tokenValidatorOutputPort.validate(token)

                val user = userRepositoryOutputPort.findById(authenticatedUser.userId)

                if (
                    user != null &&
                    user.status == ACTIVE &&
                    user.role.name == authenticatedUser.role
                ) {
                    val authentication =
                        UsernamePasswordAuthenticationToken(
                            user.userId,
                            null,
                            listOf(SimpleGrantedAuthority("ROLE_${user.role.name}")),
                        )

                    SecurityContextHolder.getContext().authentication = authentication
                } else {
                    SecurityContextHolder.clearContext()
                }
            } catch (_: JwtException) {
                SecurityContextHolder.clearContext()
            } catch (_: IllegalArgumentException) {
                SecurityContextHolder.clearContext()
            } catch (_: ClassCastException) {
                SecurityContextHolder.clearContext()
            }
        }

        filterChain.doFilter(request, response)
    }
}
