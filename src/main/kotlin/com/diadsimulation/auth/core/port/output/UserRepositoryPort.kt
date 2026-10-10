package com.diadsimulation.auth.core.port.output

import com.diadsimulation.auth.core.domain.model.User
import java.util.UUID

interface UserRepositoryPort {
    fun save(user: User): User

    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean

    fun existsByCpf(cpf: String): Boolean

    fun existsByRg(rg: String): Boolean

    fun findById(userId: UUID): User?
}
