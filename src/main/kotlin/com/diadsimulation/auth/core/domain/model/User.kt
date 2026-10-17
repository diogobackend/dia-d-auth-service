package com.diadsimulation.auth.core.domain.model

import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserStatus
import com.diadsimulation.auth.core.domain.valueobject.Cpf
import com.diadsimulation.auth.core.domain.valueobject.Email
import com.diadsimulation.auth.core.domain.valueobject.Login
import com.diadsimulation.auth.core.domain.valueobject.Phone
import com.diadsimulation.auth.core.domain.valueobject.Rg
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

class User(
    val userId: UUID = UUID.randomUUID(),
    var name: String,
    var email: Email,
    var login: Login,
    var passwordHash: String,
    var role: UserRole,
    var status: UserStatus,
    var phone: Phone? = null,
    var cpf: Cpf? = null,
    var rg: Rg? = null,
    var address: String? = null,
    var birthDate: LocalDate? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
)
