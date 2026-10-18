package com.diadsimulation.auth.app.adapter.output.persistence.adapters

import com.diadsimulation.auth.app.adapter.output.persistence.entities.UserEntity
import com.diadsimulation.auth.app.adapter.output.persistence.mappers.UserPersistenceMapper
import com.diadsimulation.auth.app.adapter.output.persistence.repositories.UserJpaRepository
import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.model.PagedResult
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.model.UserSearchCriteria
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import jakarta.persistence.criteria.Predicate
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.NoSuchElementException
import java.util.UUID

@Component
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
) : UserRepositoryOutputPort {
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

    override fun findByLogin(login: String): User? = userJpaRepository.findByLogin(login)?.let(UserPersistenceMapper::toDomain)

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
                val predicates = mutableListOf<Predicate>()

                if (criteria.allowedRole != null && criteria.allowedUserId != null) {
                    predicates.add(
                        builder.or(
                            builder.equal(root.get<UserRole>("role"), criteria.allowedRole),
                            builder.equal(root.get<UUID>("userId"), criteria.allowedUserId),
                        ),
                    )
                }

                val filterBy = criteria.filterBy
                val filterValue = criteria.filterValue

                if (filterBy != null && filterValue != null) {
                    val pattern = "%${filterValue.trim().lowercase()}%"

                    when (filterBy) {
                        "name", "email", "login", "phone", "cpf", "rg", "address" -> {
                            predicates.add(
                                builder.like(
                                    builder.lower(root.get<String>(filterBy)),
                                    pattern,
                                ),
                            )
                        }

                        "role", "status" -> {
                            predicates.add(
                                builder.equal(
                                    builder.lower(
                                        root.get<Any>(filterBy).`as`(String::class.java),
                                    ),
                                    filterValue.lowercase(),
                                ),
                            )
                        }
                    }
                }

                mapOf(
                    "name" to criteria.name,
                    "email" to criteria.email,
                    "login" to criteria.login,
                    "phone" to criteria.phone,
                    "cpf" to criteria.cpf,
                    "rg" to criteria.rg,
                    "address" to criteria.address,
                ).forEach { (field, value) ->
                    value?.let {
                        predicates.add(
                            builder.like(
                                builder.lower(root.get<String>(field)),
                                "%${it.trim().lowercase()}%",
                            ),
                        )
                    }
                }

                mapOf(
                    "role" to criteria.role,
                    "status" to criteria.status,
                ).forEach { (field, value) ->
                    value?.let {
                        predicates.add(
                            builder.equal(
                                builder.lower(
                                    root.get<Any>(field).`as`(String::class.java),
                                ),
                                it.lowercase(),
                            ),
                        )
                    }
                }

                val dateField = criteria.dateField

                if (dateField != null) {
                    if (dateField == "birthDate") {
                        val datePath = root.get<LocalDate>(dateField)

                        criteria.dateFrom?.let { dateFrom ->
                            predicates.add(
                                builder.greaterThanOrEqualTo(datePath, dateFrom),
                            )
                        }

                        criteria.dateTo?.let { dateTo ->
                            predicates.add(
                                builder.lessThanOrEqualTo(datePath, dateTo),
                            )
                        }

                        criteria.dateExact?.let { dateExact ->
                            predicates.add(
                                builder.equal(datePath, dateExact),
                            )
                        }
                    } else {
                        val datePath = root.get<OffsetDateTime>(dateField)

                        criteria.dateFrom?.let { dateFrom ->
                            predicates.add(
                                builder.greaterThanOrEqualTo(
                                    datePath,
                                    dateFrom.atStartOfDay().atOffset(ZoneOffset.UTC),
                                ),
                            )
                        }

                        criteria.dateTo?.let { dateTo ->
                            predicates.add(
                                builder.lessThan(
                                    datePath,
                                    dateTo
                                        .plusDays(1)
                                        .atStartOfDay()
                                        .atOffset(ZoneOffset.UTC),
                                ),
                            )
                        }

                        criteria.dateExact?.let { dateExact ->
                            predicates.add(
                                builder.greaterThanOrEqualTo(
                                    datePath,
                                    dateExact.atStartOfDay().atOffset(ZoneOffset.UTC),
                                ),
                            )

                            predicates.add(
                                builder.lessThan(
                                    datePath,
                                    dateExact
                                        .plusDays(1)
                                        .atStartOfDay()
                                        .atOffset(ZoneOffset.UTC),
                                ),
                            )
                        }
                    }
                }

                criteria.birthDate?.let { birthDate ->
                    predicates.add(
                        builder.equal(
                            root.get<LocalDate>("birthDate"),
                            birthDate,
                        ),
                    )
                }

                mapOf(
                    "createdAt" to criteria.createdAt,
                    "updatedAt" to criteria.updatedAt,
                ).forEach { (field, date) ->
                    date?.let {
                        val datePath = root.get<OffsetDateTime>(field)

                        predicates.add(
                            builder.greaterThanOrEqualTo(
                                datePath,
                                it.atStartOfDay().atOffset(ZoneOffset.UTC),
                            ),
                        )

                        predicates.add(
                            builder.lessThan(
                                datePath,
                                it
                                    .plusDays(1)
                                    .atStartOfDay()
                                    .atOffset(ZoneOffset.UTC),
                            ),
                        )
                    }
                }

                builder.and(*predicates.toTypedArray())
            }

        val result = userJpaRepository.findAll(specification, pageable)

        return PagedResult(
            content = result.content.map(UserPersistenceMapper::toDomain),
            page = result.number,
            size = result.size,
            totalPages = result.totalPages,
            totalElements = result.totalElements,
        )
    }

    override fun findByCpf(cpf: String): User? = userJpaRepository.findByCpf(cpf)?.let(UserPersistenceMapper::toDomain)

    override fun findByRg(rg: String): User? = userJpaRepository.findByRg(rg)?.let(UserPersistenceMapper::toDomain)

    @Transactional
    override fun update(user: User): User {
        val entity =
            userJpaRepository
                .findById(user.userId)
                .orElseThrow { NoSuchElementException(USER_NOT_FOUND) }

        entity.name = user.name
        entity.email = user.email.value
        entity.login = user.login.value
        entity.passwordHash = user.passwordHash
        entity.phone = user.phone?.value
        entity.cpf = user.cpf?.value
        entity.rg = user.rg?.value
        entity.address = user.address
        entity.birthDate = user.birthDate
        entity.updatedAt = user.updatedAt

        return UserPersistenceMapper.toDomain(entity)
    }

    @Transactional
    override fun deleteById(userId: UUID) {
        userJpaRepository.deleteById(userId)
    }

    @Transactional
    override fun updateAccess(user: User): User {
        val userEntity =
            userJpaRepository
                .findById(user.userId)
                .orElseThrow { NoSuchElementException(USER_NOT_FOUND) }

        userEntity.role = user.role
        userEntity.status = user.status
        userEntity.updatedAt = user.updatedAt

        return UserPersistenceMapper.toDomain(
            userJpaRepository.save(userEntity),
        )
    }

    override fun existsByLogin(login: String): Boolean = userJpaRepository.existsByLogin(login)
}
