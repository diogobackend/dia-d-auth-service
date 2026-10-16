package com.diadsimulation.auth.app.adapter.input.web.security

import com.diadsimulation.auth.core.common.Messages.ACCESS_DENIED
import com.diadsimulation.auth.core.domain.enums.UserRole.PLATFORM_ADMIN
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserAccessAuthorization {
    fun authorize(userId: UUID) {
        val authentication =
            SecurityContextHolder.getContext().authentication
                ?: throw AccessDeniedException(ACCESS_DENIED)

        if (!authentication.isAuthenticated) {
            throw AccessDeniedException(ACCESS_DENIED)
        }

        val isPlatformAdmin =
            authentication.authorities.any {
                it.authority == "ROLE_${PLATFORM_ADMIN.name}"
            }

        if (isPlatformAdmin) return

        val authenticatedUserId =
            authentication.principal as? UUID
                ?: throw AccessDeniedException(ACCESS_DENIED)

        if (authenticatedUserId != userId) {
            throw AccessDeniedException(ACCESS_DENIED)
        }
    }
}
