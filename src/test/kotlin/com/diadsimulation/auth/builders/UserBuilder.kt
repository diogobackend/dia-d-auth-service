package com.diadsimulation.auth.builders

import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserStatus
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.valueobject.Cpf
import com.diadsimulation.auth.core.domain.valueobject.Email
import com.diadsimulation.auth.core.domain.valueobject.Login
import com.diadsimulation.auth.core.domain.valueobject.Phone
import com.diadsimulation.auth.core.domain.valueobject.Rg
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

fun buildUser(
    userId: UUID = UUID.randomUUID(),
    name: String = "Diogo Ferreira",
    email: String = "candidate@diadsimulation.com",
    login: String = "diogo123",
    passwordHash: String = "encoded-password",
    role: UserRole = CANDIDATE,
    status: UserStatus = ACTIVE,
    phone: String? = "98999999999",
    cpf: String? = "529.982.247-25",
    rg: String? = "04.717.957/2013-4",
    address: String? = "São Luís - MA",
    birthDate: LocalDate? = null,
    createdAt: OffsetDateTime = OffsetDateTime.now(),
    updatedAt: OffsetDateTime = OffsetDateTime.now(),
): User =
    User(
        userId = userId,
        name = name,
        email = Email(email),
        login = Login(login),
        passwordHash = passwordHash,
        role = role,
        status = status,
        phone = phone?.let(::Phone),
        cpf = cpf?.let(::Cpf),
        rg = rg?.let(::Rg),
        address = address,
        birthDate = birthDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
