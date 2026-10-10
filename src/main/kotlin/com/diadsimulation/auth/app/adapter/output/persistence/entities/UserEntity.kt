package com.diadsimulation.auth.app.adapter.output.persistence.entities

import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: UUID,
    @Column(nullable = false, unique = true, length = 255)
    var email: String,
    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    var role: UserRole,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: UserStatus,
    @Column(length = 30)
    var phone: String?,
    @Column(unique = true, length = 14)
    var cpf: String?,
    @Column(unique = true, length = 20)
    var rg: String?,
    @Column(length = 500)
    var address: String?,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime,
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime,
)
