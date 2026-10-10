package com.diadsimulation.auth.app.adapter.output.persistence.repositories

import com.diadsimulation.auth.app.adapter.output.persistence.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserJpaRepository : JpaRepository<UserEntity, UUID> {
    fun findByEmail(email: String): UserEntity?
    fun existsByEmail(email: String): Boolean
    fun existsByCpf(cpf: String): Boolean
    fun existsByRg(rg: String): Boolean
}