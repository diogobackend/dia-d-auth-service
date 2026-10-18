package com.diadsimulation.auth.app.adapter.input.web.security

import com.diadsimulation.auth.core.common.Messages.ACCESS_DENIED
import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserRole.PLATFORM_ADMIN
import com.diadsimulation.auth.core.domain.enums.UserRole.SCHOOL_ADMIN
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserAccessAuthorization(
    private val userRepositoryOutputPort: UserRepositoryOutputPort,
) {
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

        val isSchoolAdmin =
            authentication.authorities.any {
                it.authority == "ROLE_${SCHOOL_ADMIN.name}"
            }

        if (isSchoolAdmin) {
            if (authenticatedUserId == userId) return

            val targetUser =
                userRepositoryOutputPort.findById(userId)
                    ?: throw AccessDeniedException(ACCESS_DENIED)

            if (targetUser.role == CANDIDATE) return

            throw AccessDeniedException(ACCESS_DENIED)
        }

        if (authenticatedUserId != userId) {
            throw AccessDeniedException(ACCESS_DENIED)
        }
    }

    fun getListAccess(): Pair<UserRole?, UUID?> {
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

        if (isPlatformAdmin) return null to null

        val isSchoolAdmin =
            authentication.authorities.any {
                it.authority == "ROLE_${SCHOOL_ADMIN.name}"
            }

        if (!isSchoolAdmin) {
            throw AccessDeniedException(ACCESS_DENIED)
        }

        val authenticatedUserId =
            authentication.principal as? UUID
                ?: throw AccessDeniedException(ACCESS_DENIED)

        return CANDIDATE to authenticatedUserId
    }
}
