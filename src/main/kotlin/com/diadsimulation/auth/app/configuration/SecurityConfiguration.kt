package com.diadsimulation.auth.app.configuration

import com.diadsimulation.auth.app.adapter.input.web.exception.JwtAccessDeniedHandler
import com.diadsimulation.auth.app.adapter.input.web.exception.JwtAuthenticationEntryPoint
import com.diadsimulation.auth.app.adapter.input.web.security.JwtAuthenticationFilter
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserRole.PLATFORM_ADMIN
import com.diadsimulation.auth.core.domain.enums.UserRole.SCHOOL_ADMIN
import org.hibernate.internal.util.collections.CollectionHelper.listOf
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfiguration(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { }
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(STATELESS)
            }.exceptionHandling {
                it
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler)
            }.authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                    ).permitAll()
                    .requestMatchers(POST, "/login", "/users")
                    .permitAll()
                    .requestMatchers(PATCH, "/users/*/access")
                    .hasRole(PLATFORM_ADMIN.name)
                    .requestMatchers(GET, "/users/id")
                    .hasAnyRole(
                        CANDIDATE.name,
                        SCHOOL_ADMIN.name,
                        PLATFORM_ADMIN.name,
                    ).requestMatchers(PATCH, "/users/*")
                    .hasAnyRole(
                        CANDIDATE.name,
                        SCHOOL_ADMIN.name,
                        PLATFORM_ADMIN.name,
                    ).requestMatchers("/users/**")
                    .hasAnyRole(
                        SCHOOL_ADMIN.name,
                        PLATFORM_ADMIN.name,
                    ).anyRequest()
                    .authenticated()
            }.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java,
            )

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration =
            CorsConfiguration().apply {
                allowedOrigins = listOf("http://localhost:3000")
                allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                allowedHeaders = listOf("*")
            }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}
