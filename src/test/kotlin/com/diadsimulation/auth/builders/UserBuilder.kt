package com.diadsimulation.auth.builders

import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserStatus
import com.diadsimulation.auth.core.domain.model.User
import java.time.OffsetDateTime
import java.util.UUID

fun buildUser(
    userId: UUID = UUID.randomUUID(),
    name: String = "Diogo Ferreira",
    email: String = "candidate@diadsimulation.com",
    passwordHash: String = "encoded-password",
    role: UserRole = UserRole.CANDIDATE,
    status: UserStatus = UserStatus.ACTIVE,
    phone: String? = "98999999999",
    cpf: String? = "123.456.789-00",
    rg: String? = "123456789",
    address: String? = "São Luís - MA",
    createdAt: OffsetDateTime = OffsetDateTime.now(),
    updatedAt: OffsetDateTime = OffsetDateTime.now(),
): User =
    User(
        userId = userId,
        name = name,
        email = email,
        passwordHash = passwordHash,
        role = role,
        status = status,
        phone = phone,
        cpf = cpf,
        rg = rg,
        address = address,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
