package com.diadsimulation.auth.app.adapter.input.web.dtos.responses

import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class UpdateUserResponse(
    val userId: UUID,
    val name: String,
    val email: String,
    val login: String,
    val phone: String?,
    val cpf: String?,
    val rg: String?,
    val address: String?,
    val birthDate: LocalDate?,
    val updatedAt: OffsetDateTime,
)
