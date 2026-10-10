package com.diadsimulation.auth.app.adapter.output.persistence.adapters

import com.diadsimulation.auth.app.adapter.output.persistence.mappers.UserPersistenceMapper
import com.diadsimulation.auth.app.adapter.output.persistence.repositories.UserJpaRepository
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.output.UserRepositoryPort
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
) : UserRepositoryPort {
    override fun save(user: User): User {
        val userEntity = UserPersistenceMapper.toEntity(user)
        val savedEntity = userJpaRepository.save(userEntity)

        return UserPersistenceMapper.toDomain(savedEntity)
    }

    override fun findById(userId: UUID): User? =
        userJpaRepository
            .findById(userId)
            .map(UserPersistenceMapper::toDomain)
            .orElse(null)

    override fun findByEmail(email: String): User? {
        val userEntity = userJpaRepository.findByEmail(email) ?: return null

        return UserPersistenceMapper.toDomain(userEntity)
    }

    override fun existsByEmail(email: String): Boolean = userJpaRepository.existsByEmail(email)

    override fun existsByCpf(cpf: String): Boolean = userJpaRepository.existsByCpf(cpf)

    override fun existsByRg(rg: String): Boolean = userJpaRepository.existsByRg(rg)
}
