package com.diadsimulation.auth.core.domain.model

import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserStatus
import java.time.OffsetDateTime
import java.util.UUID

class User(
    val userId: UUID = UUID.randomUUID(),
    var email: String,
    var passwordHash: String,
    var role: UserRole,
    var status: UserStatus,
    var phone: String? = null,
    var address: String? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)