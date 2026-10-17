package com.diadsimulation.auth.app.adapter.output.persistence.mappers

import com.diadsimulation.auth.app.adapter.output.persistence.entities.UserEntity
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.valueobject.Cpf
import com.diadsimulation.auth.core.domain.valueobject.Email
import com.diadsimulation.auth.core.domain.valueobject.Login
import com.diadsimulation.auth.core.domain.valueobject.Phone
import com.diadsimulation.auth.core.domain.valueobject.Rg

object UserPersistenceMapper {
    fun toEntity(user: User): UserEntity =
        UserEntity(
            userId = user.userId,
            name = user.name,
            email = user.email.value,
            login = user.login.value,
            passwordHash = user.passwordHash,
            role = user.role,
            status = user.status,
            phone = user.phone?.value,
            cpf = user.cpf?.value,
            rg = user.rg?.value,
            address = user.address,
            birthDate = user.birthDate,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
        )

    fun toDomain(entity: UserEntity): User =
        User(
            userId = entity.userId,
            name = entity.name,
            email = Email(entity.email),
            login = Login(entity.login),
            passwordHash = entity.passwordHash,
            role = entity.role,
            status = entity.status,
            phone = entity.phone?.let(::Phone),
            cpf = entity.cpf?.let(::Cpf),
            rg = entity.rg?.let(::Rg),
            address = entity.address,
            birthDate = entity.birthDate,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
