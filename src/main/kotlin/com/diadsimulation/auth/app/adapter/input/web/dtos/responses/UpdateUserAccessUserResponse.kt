package com.diadsimulation.auth.app.adapter.input.web.dtos.responses

import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserStatus
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class UpdateUserAccessUserResponse(
    val userId: UUID,
    val name: String,
    val email: String,
    val role: UserRole,
    val status: UserStatus,
    val birthDate: LocalDate?,
    val updatedAt: OffsetDateTime,
)
