package com.diadsimulation.auth.builders

import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserStatus
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.valueobject.Email
import com.diadsimulation.auth.core.domain.valueobject.Phone
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

fun buildUser(
    userId: UUID = UUID.randomUUID(),
    name: String = "Diogo Ferreira",
    email: String = "candidate@diadsimulation.com",
    passwordHash: String = "encoded-password",
    role: UserRole = CANDIDATE,
    status: UserStatus = ACTIVE,
    phone: String? = "98999999999",
    cpf: String? = "123.456.789-00",
    rg: String? = "123456789",
    address: String? = "São Luís - MA",
    birthDate: LocalDate? = null,
    createdAt: OffsetDateTime = OffsetDateTime.now(),
    updatedAt: OffsetDateTime = OffsetDateTime.now(),
): User =
    User(
        userId = userId,
        name = name,
        email = Email(email),
        passwordHash = passwordHash,
        role = role,
        status = status,
        phone = phone?.let(::Phone),
        cpf = cpf,
        rg = rg,
        address = address,
        birthDate = birthDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
