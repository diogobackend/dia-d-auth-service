package com.diadsimulation.auth.app.adapter.input.web.dtos.responses

import java.time.OffsetDateTime
import java.util.UUID

data class UpdateUserResponse(
    val userId: UUID,
    val name: String,
    val email: String,
    val phone: String?,
    val cpf: String?,
    val address: String?,
    val updatedAt: OffsetDateTime,
)
