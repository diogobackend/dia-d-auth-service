package com.diadsimulation.auth.core.domain.model

import java.util.UUID

data class AuthenticatedUser(
    val userId: UUID,
    val role: String,
)
