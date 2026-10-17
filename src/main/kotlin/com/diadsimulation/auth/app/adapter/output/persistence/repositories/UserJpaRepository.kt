package com.diadsimulation.auth.app.adapter.output.persistence.repositories

import com.diadsimulation.auth.app.adapter.output.persistence.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.UUID

interface UserJpaRepository :
    JpaRepository<UserEntity, UUID>,
    JpaSpecificationExecutor<UserEntity> {
    fun findByEmail(email: String): UserEntity?

    fun findByLogin(login: String): UserEntity?

    fun existsByEmail(email: String): Boolean

    fun existsByLogin(login: String): Boolean

    fun existsByCpf(cpf: String): Boolean

    fun existsByRg(rg: String): Boolean

    fun findByCpf(cpf: String): UserEntity?

    fun findByRg(rg: String): UserEntity?
}
