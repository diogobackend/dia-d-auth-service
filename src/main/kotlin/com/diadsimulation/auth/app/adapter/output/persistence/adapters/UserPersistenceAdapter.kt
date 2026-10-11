package com.diadsimulation.auth.app.adapter.output.persistence.adapters

import com.diadsimulation.auth.app.adapter.output.persistence.entities.UserEntity
import com.diadsimulation.auth.app.adapter.output.persistence.mappers.UserPersistenceMapper
import com.diadsimulation.auth.app.adapter.output.persistence.repositories.UserJpaRepository
import com.diadsimulation.auth.core.domain.model.PagedResult
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.model.UserSearchCriteria
import com.diadsimulation.auth.core.port.output.UserRepositoryPort
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
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

    override fun findAll(criteria: UserSearchCriteria): PagedResult<User> {
        val direction = Sort.Direction.valueOf(criteria.direction.name)

        val pageable =
            PageRequest.of(
                criteria.page,
                criteria.size,
                Sort.by(direction, criteria.sortBy),
            )

        val specification =
            Specification<UserEntity> { root, _, builder ->
                val filterBy = criteria.filterBy
                val filterValue = criteria.filterValue

                if (filterBy == null || filterValue == null) {
                    builder.conjunction()
                } else {
                    val pattern = "%${filterValue.lowercase()}%"

                    when (filterBy) {
                        "name", "email", "phone", "cpf", "rg", "address" -> {
                            builder.like(
                                builder.lower(root.get<String>(filterBy)),
                                pattern,
                            )
                        }

                        "role", "status" -> {
                            builder.like(
                                builder.lower(root.get<Any>(filterBy).`as`(String::class.java)),
                                pattern,
                            )
                        }

                        "userId", "createdAt", "updatedAt" -> {
                            builder.like(
                                builder.lower(root.get<Any>(filterBy).`as`(String::class.java)),
                                pattern,
                            )
                        }

                        else -> {
                            builder.conjunction()
                        }
                    }
                }
            }

        val result = userJpaRepository.findAll(specification, pageable)

        return PagedResult(
            content = result.content.map(UserPersistenceMapper::toDomain),
            page = result.number,
            size = result.size,
            totalPages = result.totalPages,
        )
    }
}
