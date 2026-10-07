package com.diadsimulation.auth.app.adapter.input.web.dtos

import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserStatus
import java.time.OffsetDateTime
import java.util.UUID

data class RegisterUserResponse(
    val userId: UUID,
    val email: String,
    val role: UserRole,
    val status: UserStatus,
    val phone: String?,
    val address: String?,
    val cpf: String?,
    val rg: String?,
    val createdAt: OffsetDateTime
)