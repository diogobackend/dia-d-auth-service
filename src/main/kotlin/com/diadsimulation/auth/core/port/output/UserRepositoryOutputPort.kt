package com.diadsimulation.auth.core.port.output

import com.diadsimulation.auth.core.domain.model.PagedResult
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.model.UserSearchCriteria
import java.util.UUID

interface UserRepositoryOutputPort {
    fun save(user: User): User

    fun findByEmail(email: String): User?

    fun findByLogin(login: String): User?

    fun existsByEmail(email: String): Boolean

    fun existsByLogin(login: String): Boolean

    fun existsByCpf(cpf: String): Boolean

    fun existsByRg(rg: String): Boolean

    fun findById(userId: UUID): User?

    fun findAll(criteria: UserSearchCriteria): PagedResult<User>

    fun findByCpf(cpf: String): User?

    fun findByRg(rg: String): User?

    fun update(user: User): User

    fun deleteById(userId: UUID)

    fun updateAccess(user: User): User
}
