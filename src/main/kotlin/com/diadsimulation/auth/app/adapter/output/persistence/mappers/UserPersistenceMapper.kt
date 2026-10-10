package com.diadsimulation.auth.app.adapter.output.persistence.mappers

import com.diadsimulation.auth.app.adapter.output.persistence.entities.UserEntity
import com.diadsimulation.auth.core.domain.model.User

object UserPersistenceMapper {
    fun toEntity(user: User): UserEntity =
        UserEntity(
            userId = user.userId,
            email = user.email,
            passwordHash = user.passwordHash,
            role = user.role,
            status = user.status,
            phone = user.phone,
            cpf = user.cpf,
            rg = user.rg,
            address = user.address,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
        )

    fun toDomain(entity: UserEntity): User =
        User(
            userId = entity.userId,
            email = entity.email,
            passwordHash = entity.passwordHash,
            role = entity.role,
            status = entity.status,
            phone = entity.phone,
            cpf = entity.cpf,
            rg = entity.rg,
            address = entity.address,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
