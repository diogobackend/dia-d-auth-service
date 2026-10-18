package com.diadsimulation.auth.app.adapter.output.persistence.adapters

import com.diadsimulation.auth.app.adapter.output.persistence.entities.UserEntity
import com.diadsimulation.auth.app.adapter.output.persistence.mappers.UserPersistenceMapper
import com.diadsimulation.auth.app.adapter.output.persistence.repositories.UserJpaRepository
import com.diadsimulation.auth.builders.buildUser
import com.diadsimulation.auth.builders.buildUserSearchCriteria
import com.diadsimulation.auth.core.common.Messages.PERSISTENCE_FAILED
import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserRole.PLATFORM_ADMIN
import com.diadsimulation.auth.core.domain.enums.UserRole.SCHOOL_ADMIN
import com.diadsimulation.auth.core.domain.enums.UserStatus.BLOCKED
import com.diadsimulation.auth.core.domain.enums.UserStatus.INACTIVE
import com.diadsimulation.auth.core.domain.model.User
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.util.Collections.emptyList
import java.util.NoSuchElementException
import java.util.Optional
import java.util.UUID
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class UserPersistenceAdapterTest {
    @MockK
    private lateinit var userJpaRepository: UserJpaRepository

    @InjectMockKs
    private lateinit var userPersistenceAdapter: UserPersistenceAdapter

    private lateinit var user: User

    private lateinit var root: Root<UserEntity>
    private lateinit var query: CriteriaQuery<*>
    private lateinit var builder: CriteriaBuilder
    private lateinit var predicate: Predicate

    private val email get() = user.email.value
    private val login get() = user.login.value
    private val userId get() = user.userId
    private val name get() = user.name
    private val cpf get() = user.cpf?.value
    private val rg get() = user.rg?.value
    private val role get() = user.role
    private val status get() = user.status
    private val passwordHash get() = user.passwordHash
    private val phone get() = user.phone
    private val address get() = user.address
    private val birthDate get() = user.birthDate
    private val createdAt get() = user.createdAt

    @BeforeEach
    fun setUp() {
        user = buildUser()
        root = mockk()
        query = mockk()
        builder = mockk()
        predicate = mockk()
    }

    @Test
    fun `should save user successfully`() {
        every {
            userJpaRepository.save(
                match {
                    it.userId == userId &&
                        it.email == email &&
                        it.passwordHash == passwordHash
                },
            )
        } answers { firstArg() }

        val result = userPersistenceAdapter.save(user)

        assertEquals(userId, result.userId)
        assertEquals(name, result.name)
        assertEquals(email, result.email.value)
        assertEquals(passwordHash, result.passwordHash)

        verify(exactly = 1) {
            userJpaRepository.save(
                match {
                    it.userId == userId &&
                        it.name == name &&
                        it.email == email &&
                        it.passwordHash == passwordHash
                },
            )
        }
    }

    @Test
    fun `should find user by id`() {
        every {
            userJpaRepository.findById(userId)
        } returns Optional.of(UserPersistenceMapper.toEntity(user))

        val result = userPersistenceAdapter.findById(userId)

        assertNotNull(result)
        assertEquals(userId, result?.userId)
        assertEquals(email, result?.email?.value)
    }

    @Test
    fun `should return null when user id does not exist`() {
        every {
            userJpaRepository.findById(userId)
        } returns Optional.empty()

        assertNull(userPersistenceAdapter.findById(userId))
    }

    @Test
    fun `should find user by email`() {
        every {
            userJpaRepository.findByEmail(email)
        } returns UserPersistenceMapper.toEntity(user)

        val result = userPersistenceAdapter.findByEmail(email)

        assertNotNull(result)
        assertEquals(userId, result?.userId)
        assertEquals(email, result?.email?.value)
    }

    @Test
    fun `should return null when email does not exist`() {
        every {
            userJpaRepository.findByEmail(email)
        } returns null

        assertNull(userPersistenceAdapter.findByEmail(email))
    }

    @ParameterizedTest
    @MethodSource("booleanProvider")
    fun `should check if email exists`(exists: Boolean) {
        every { userJpaRepository.existsByEmail(email) } returns exists

        assertEquals(exists, userPersistenceAdapter.existsByEmail(email))

        verify(exactly = 1) {
            userJpaRepository.existsByEmail(email)
        }
    }

    @ParameterizedTest
    @MethodSource("booleanProvider")
    fun `should check if cpf exists`(exists: Boolean) {
        val cpf = requireNotNull(cpf)

        every { userJpaRepository.existsByCpf(cpf) } returns exists

        assertEquals(exists, userPersistenceAdapter.existsByCpf(cpf))

        verify(exactly = 1) {
            userJpaRepository.existsByCpf(cpf)
        }
    }

    @ParameterizedTest
    @MethodSource("booleanProvider")
    fun `should check if rg exists`(exists: Boolean) {
        val rg = requireNotNull(rg)

        every { userJpaRepository.existsByRg(rg) } returns exists

        assertEquals(exists, userPersistenceAdapter.existsByRg(rg))

        verify(exactly = 1) {
            userJpaRepository.existsByRg(rg)
        }
    }

    @Test
    fun `should find all users without filters`() {
        val criteria = buildUserSearchCriteria()
        val entity = UserPersistenceMapper.toEntity(user)
        val pageable = PageRequest.of(0, 10, Sort.by(ASC, "name"))
        val page = PageImpl(listOf(entity), pageable, 1)

        every {
            userJpaRepository.findAll(
                any<Specification<UserEntity>>(),
                any<Pageable>(),
            )
        } returns page

        val result = userPersistenceAdapter.findAll(criteria)

        assertEquals(1, result.content.size)
        assertEquals(userId, result.content.first().userId)
        assertEquals(0, result.page)
        assertEquals(10, result.size)
        assertEquals(1, result.totalPages)
        assertEquals(1L, result.totalElements)

        verify(exactly = 1) {
            userJpaRepository.findAll(
                any<Specification<UserEntity>>(),
                eq(pageable),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("textFilterProvider")
    fun `should filter users by text field`(
        filterBy: String,
        filterValue: String,
    ) {
        val criteria =
            buildUserSearchCriteria(
                filterBy = filterBy,
                filterValue = filterValue,
            )

        val path = mockk<Path<String>>()
        val expression = mockk<Expression<String>>()
        val expectedPattern = "%${filterValue.lowercase()}%"

        every { root.get<String>(filterBy) } returns path
        every { builder.lower(path) } returns expression
        every { builder.like(expression, expectedPattern) } returns predicate
        every { builder.and(predicate) } returns predicate

        mockFindAll()

        val result = userPersistenceAdapter.findAll(criteria)

        assertTrue(result.content.isEmpty())

        verify(exactly = 1) {
            builder.like(expression, expectedPattern)
            builder.and(predicate)
        }
    }

    @ParameterizedTest
    @MethodSource("enumFilterProvider")
    fun `should filter users by enum field`(
        filterBy: String,
        filterValue: String,
    ) {
        val criteria =
            buildUserSearchCriteria(
                filterBy = filterBy,
                filterValue = filterValue,
            )

        val path = mockk<Path<Any>>()
        val stringPath = mockk<Expression<String>>()
        val expression = mockk<Expression<String>>()
        val expectedValue = filterValue.lowercase()

        every { root.get<Any>(filterBy) } returns path
        every { path.`as`(String::class.java) } returns stringPath
        every { builder.lower(stringPath) } returns expression
        every { builder.equal(expression, expectedValue) } returns predicate
        every { builder.and(predicate) } returns predicate

        mockFindAll()

        val result = userPersistenceAdapter.findAll(criteria)

        assertTrue(result.content.isEmpty())

        verify(exactly = 1) {
            builder.equal(expression, expectedValue)
            builder.and(predicate)
        }
    }

    @ParameterizedTest
    @MethodSource("dateFilterProvider")
    fun `should filter users by date`(
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
    ) {
        val criteria =
            buildUserSearchCriteria(
                dateField = "createdAt",
                dateFrom = dateFrom,
                dateTo = dateTo,
            )

        val datePath = mockk<Path<OffsetDateTime>>()
        val predicates = mutableListOf<Predicate>()

        every { root.get<OffsetDateTime>("createdAt") } returns datePath

        dateFrom?.let {
            val expectedFrom =
                it.atStartOfDay().atOffset(UTC)

            val fromPredicate = mockk<Predicate>()

            every {
                builder.greaterThanOrEqualTo(datePath, expectedFrom)
            } returns fromPredicate

            predicates.add(fromPredicate)
        }

        dateTo?.let {
            val expectedTo =
                it.plusDays(1).atStartOfDay().atOffset(UTC)

            val toPredicate = mockk<Predicate>()

            every {
                builder.lessThan(datePath, expectedTo)
            } returns toPredicate

            predicates.add(toPredicate)
        }

        every {
            builder.and(*predicates.toTypedArray())
        } returns predicate

        mockFindAll()

        val result = userPersistenceAdapter.findAll(criteria)

        assertTrue(result.content.isEmpty())

        dateFrom?.let {
            verify(exactly = 1) {
                builder.greaterThanOrEqualTo(
                    datePath,
                    it.atStartOfDay().atOffset(UTC),
                )
            }
        }

        dateTo?.let {
            verify(exactly = 1) {
                builder.lessThan(
                    datePath,
                    it.plusDays(1).atStartOfDay().atOffset(UTC),
                )
            }
        }

        verify(exactly = 1) {
            builder.and(*predicates.toTypedArray())
        }
    }

    @ParameterizedTest
    @MethodSource("ignoredFilterProvider")
    fun `should ignore incomplete or unsupported filters`(
        filterBy: String?,
        filterValue: String?,
    ) {
        val criteria =
            buildUserSearchCriteria(
                filterBy = filterBy,
                filterValue = filterValue,
            )

        every { builder.and(*anyVararg()) } returns predicate

        mockFindAll()

        val result = userPersistenceAdapter.findAll(criteria)

        assertTrue(result.content.isEmpty())

        verify(exactly = 1) {
            builder.and(*anyVararg())
        }

        verify(exactly = 0) {
            builder.like(any<Expression<String>>(), any<String>())
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["createdAt", "updatedAt"])
    fun `should filter users by exact date`(dateField: String) {
        val dateExact = LocalDate.of(2026, 10, 11)
        val criteria =
            buildUserSearchCriteria(
                dateField = dateField,
                dateExact = dateExact,
            )

        val datePath = mockk<Path<OffsetDateTime>>()
        val fromPredicate = mockk<Predicate>()
        val toPredicate = mockk<Predicate>()

        val expectedFrom = dateExact.atStartOfDay().atOffset(UTC)
        val expectedTo = dateExact.plusDays(1).atStartOfDay().atOffset(UTC)

        every { root.get<OffsetDateTime>(dateField) } returns datePath
        every { builder.greaterThanOrEqualTo(datePath, expectedFrom) } returns fromPredicate
        every { builder.lessThan(datePath, expectedTo) } returns toPredicate
        every { builder.and(fromPredicate, toPredicate) } returns predicate

        mockFindAll()

        val result = userPersistenceAdapter.findAll(criteria)

        assertTrue(result.content.isEmpty())

        verify(exactly = 1) {
            builder.greaterThanOrEqualTo(datePath, expectedFrom)
            builder.lessThan(datePath, expectedTo)
            builder.and(fromPredicate, toPredicate)
        }
    }

    @Test
    fun `should filter users by role and status`() {
        val criteria =
            buildUserSearchCriteria(
                role = SCHOOL_ADMIN.toString(),
                status = BLOCKED.toString(),
            )

        val rolePath = mockk<Path<Any>>()
        val statusPath = mockk<Path<Any>>()
        val roleString = mockk<Expression<String>>()
        val statusString = mockk<Expression<String>>()
        val roleLower = mockk<Expression<String>>()
        val statusLower = mockk<Expression<String>>()
        val rolePredicate = mockk<Predicate>()
        val statusPredicate = mockk<Predicate>()

        every { root.get<Any>("role") } returns rolePath
        every { root.get<Any>("status") } returns statusPath
        every { rolePath.`as`(String::class.java) } returns roleString
        every { statusPath.`as`(String::class.java) } returns statusString
        every { builder.lower(roleString) } returns roleLower
        every { builder.lower(statusString) } returns statusLower
        every { builder.equal(roleLower, "school_admin") } returns rolePredicate
        every { builder.equal(statusLower, "blocked") } returns statusPredicate
        every { builder.and(rolePredicate, statusPredicate) } returns predicate

        mockFindAll()

        val result = userPersistenceAdapter.findAll(criteria)

        assertTrue(result.content.isEmpty())

        verify(exactly = 1) {
            builder.equal(roleLower, "school_admin")
            builder.equal(statusLower, "blocked")
            builder.and(rolePredicate, statusPredicate)
        }
    }

    @ParameterizedTest
    @MethodSource("independentEnumFilterProvider")
    fun `should filter users by independent enum field`(
        field: String,
        value: String,
    ) {
        val criteria =
            when (field) {
                "role" -> buildUserSearchCriteria(role = value)
                else -> buildUserSearchCriteria(status = value)
            }

        val path = mockk<Path<Any>>()
        val stringPath = mockk<Expression<String>>()
        val expression = mockk<Expression<String>>()

        every { root.get<Any>(field) } returns path
        every { path.`as`(String::class.java) } returns stringPath
        every { builder.lower(stringPath) } returns expression
        every { builder.equal(expression, value.lowercase()) } returns predicate
        every { builder.and(predicate) } returns predicate

        mockFindAll()

        userPersistenceAdapter.findAll(criteria)

        verify(exactly = 1) {
            builder.equal(expression, value.lowercase())
            builder.and(predicate)
        }
    }

    @Test
    fun `should filter users by birth date range and exact date`() {
        val dateFrom = LocalDate.of(1990, 1, 1)
        val dateTo = LocalDate.of(2000, 12, 31)
        val dateExact = LocalDate.of(1995, 5, 5)

        val criteria =
            buildUserSearchCriteria(
                dateField = "birthDate",
                dateFrom = dateFrom,
                dateTo = dateTo,
                dateExact = dateExact,
            )

        val datePath = mockk<Path<LocalDate>>()
        val fromPredicate = mockk<Predicate>()
        val toPredicate = mockk<Predicate>()
        val exactPredicate = mockk<Predicate>()

        every { root.get<LocalDate>("birthDate") } returns datePath
        every {
            builder.greaterThanOrEqualTo(datePath, dateFrom)
        } returns fromPredicate
        every {
            builder.lessThanOrEqualTo(datePath, dateTo)
        } returns toPredicate
        every {
            builder.equal(datePath, dateExact)
        } returns exactPredicate
        every {
            builder.and(fromPredicate, toPredicate, exactPredicate)
        } returns predicate

        mockFindAll()

        userPersistenceAdapter.findAll(criteria)

        verify(exactly = 1) {
            builder.greaterThanOrEqualTo(datePath, dateFrom)
            builder.lessThanOrEqualTo(datePath, dateTo)
            builder.equal(datePath, dateExact)
            builder.and(fromPredicate, toPredicate, exactPredicate)
        }
    }

    @ParameterizedTest
    @MethodSource("birthDateNullFilterProvider")
    fun `should filter birth date with optional parameters`(
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        dateExact: LocalDate?,
    ) {
        val criteria =
            buildUserSearchCriteria(
                dateField = "birthDate",
                dateFrom = dateFrom,
                dateTo = dateTo,
                dateExact = dateExact,
            )

        val datePath = mockk<Path<LocalDate>>()
        val predicates = mutableListOf<Predicate>()

        every { root.get<LocalDate>("birthDate") } returns datePath

        dateFrom?.let {
            val fromPredicate = mockk<Predicate>()
            every {
                builder.greaterThanOrEqualTo(datePath, it)
            } returns fromPredicate
            predicates.add(fromPredicate)
        }

        dateTo?.let {
            val toPredicate = mockk<Predicate>()
            every {
                builder.lessThanOrEqualTo(datePath, it)
            } returns toPredicate
            predicates.add(toPredicate)
        }

        dateExact?.let {
            val exactPredicate = mockk<Predicate>()
            every {
                builder.equal(datePath, it)
            } returns exactPredicate
            predicates.add(exactPredicate)
        }

        every {
            builder.and(*predicates.toTypedArray())
        } returns predicate

        mockFindAll()

        val result = userPersistenceAdapter.findAll(criteria)

        assertTrue(result.content.isEmpty())

        verify(exactly = 1) {
            builder.and(*predicates.toTypedArray())
        }
    }

    @ParameterizedTest
    @MethodSource("independentTextFilterProvider")
    fun `should filter users by independent text field`(
        field: String,
        value: String,
    ) {
        val criteria =
            when (field) {
                "name" -> buildUserSearchCriteria(name = value)
                "email" -> buildUserSearchCriteria(email = value)
                "phone" -> buildUserSearchCriteria(phone = value)
                "cpf" -> buildUserSearchCriteria(cpf = value)
                "rg" -> buildUserSearchCriteria(rg = value)
                else -> buildUserSearchCriteria(address = value)
            }

        val path = mockk<Path<String>>()
        val expression = mockk<Expression<String>>()
        val expectedPattern = "%${value.trim().lowercase()}%"

        every { root.get<String>(field) } returns path
        every { builder.lower(path) } returns expression
        every { builder.like(expression, expectedPattern) } returns predicate
        every { builder.and(predicate) } returns predicate

        mockFindAll()

        val result = userPersistenceAdapter.findAll(criteria)

        assertTrue(result.content.isEmpty())

        verify(exactly = 1) {
            builder.like(expression, expectedPattern)
            builder.and(predicate)
        }
    }

    @Test
    fun `should filter users by independent birth date`() {
        val birthDate = LocalDate.of(2000, 3, 3)
        val criteria = buildUserSearchCriteria(birthDate = birthDate)

        val datePath = mockk<Path<LocalDate>>()

        every { root.get<LocalDate>("birthDate") } returns datePath
        every { builder.equal(datePath, birthDate) } returns predicate
        every { builder.and(predicate) } returns predicate

        mockFindAll()

        val result = userPersistenceAdapter.findAll(criteria)

        assertTrue(result.content.isEmpty())

        verify(exactly = 1) {
            builder.equal(datePath, birthDate)
            builder.and(predicate)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["createdAt", "updatedAt"])
    fun `should filter users by independent timestamp date`(field: String) {
        val date = LocalDate.of(2026, 10, 11)

        val criteria =
            when (field) {
                "createdAt" -> buildUserSearchCriteria(createdAt = date)
                else -> buildUserSearchCriteria(updatedAt = date)
            }

        val datePath = mockk<Path<OffsetDateTime>>()
        val fromPredicate = mockk<Predicate>()
        val toPredicate = mockk<Predicate>()

        val expectedFrom = date.atStartOfDay().atOffset(UTC)
        val expectedTo = date.plusDays(1).atStartOfDay().atOffset(UTC)

        every { root.get<OffsetDateTime>(field) } returns datePath
        every {
            builder.greaterThanOrEqualTo(datePath, expectedFrom)
        } returns fromPredicate
        every {
            builder.lessThan(datePath, expectedTo)
        } returns toPredicate
        every {
            builder.and(fromPredicate, toPredicate)
        } returns predicate

        mockFindAll()

        val result = userPersistenceAdapter.findAll(criteria)

        assertTrue(result.content.isEmpty())

        verify(exactly = 1) {
            builder.greaterThanOrEqualTo(datePath, expectedFrom)
            builder.lessThan(datePath, expectedTo)
            builder.and(fromPredicate, toPredicate)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["cpf", "rg"])
    fun `should find user by document`(document: String) {
        val entity = UserPersistenceMapper.toEntity(user)

        val result =
            when (document) {
                "cpf" -> {
                    every { userJpaRepository.findByCpf(cpf.toString()) } returns entity
                    userPersistenceAdapter.findByCpf(cpf.toString())
                }

                else -> {
                    every { userJpaRepository.findByRg(rg!!) } returns entity
                    userPersistenceAdapter.findByRg(rg!!)
                }
            }

        assertNotNull(result)
        assertEquals(userId, result?.userId)
    }

    @ParameterizedTest
    @ValueSource(strings = ["cpf", "rg"])
    fun `should return null when document does not exist`(document: String) {
        val result =
            when (document) {
                "cpf" -> {
                    every { userJpaRepository.findByCpf(cpf.toString()) } returns null
                    userPersistenceAdapter.findByCpf(cpf.toString())
                }

                else -> {
                    every { userJpaRepository.findByRg(rg!!) } returns null
                    userPersistenceAdapter.findByRg(rg!!)
                }
            }

        assertNull(result)
    }

    @Test
    fun `should update user successfully`() {
        val entity = UserPersistenceMapper.toEntity(user)
        val updatedAt = OffsetDateTime.now()

        every {
            userJpaRepository.findById(userId)
        } returns Optional.of(entity)

        val updatedUser =
            buildUser(
                userId = userId,
                name = "Updated Name",
                email = "updated@diad.com",
                passwordHash = "updated-password-hash",
                phone = "98999999999",
                address = "Updated Address",
                updatedAt = updatedAt,
            )

        val result = userPersistenceAdapter.update(updatedUser)

        assertEquals(updatedUser.userId, result.userId)
        assertEquals(updatedUser.name, result.name)
        assertEquals(updatedUser.email, result.email)
        assertEquals(updatedUser.passwordHash, result.passwordHash)
        assertEquals(updatedUser.phone, result.phone)
        assertEquals(updatedUser.address, result.address)
        assertEquals(updatedAt, result.updatedAt)

        verify(exactly = 1) {
            userJpaRepository.findById(userId)
        }

        verify(exactly = 0) {
            userJpaRepository.save(any())
        }
    }

    @Test
    fun `should preserve non editable fields when updating user`() {
        val entity = UserPersistenceMapper.toEntity(user)

        every {
            userJpaRepository.findById(userId)
        } returns Optional.of(entity)

        val updatedUser =
            buildUser(
                userId = userId,
                name = "Updated Name",
            )

        val result = userPersistenceAdapter.update(updatedUser)

        assertEquals(userId, result.userId)
        assertEquals(role, result.role)
        assertEquals(status, result.status)
        assertEquals(cpf, result.cpf?.value)
        assertEquals(rg, result.rg?.value)
        assertEquals(birthDate, result.birthDate)
        assertEquals(createdAt, result.createdAt)

        verify(exactly = 0) {
            userJpaRepository.save(any())
        }
    }

    @Test
    fun `should throw exception when updating nonexistent user`() {
        every {
            userJpaRepository.findById(userId)
        } returns Optional.empty()

        val exception =
            assertThrows(NoSuchElementException::class.java) {
                userPersistenceAdapter.update(user)
            }

        assertEquals(USER_NOT_FOUND, exception.message)

        verify(exactly = 1) {
            userJpaRepository.findById(userId)
        }

        verify(exactly = 0) {
            userJpaRepository.save(any())
        }
    }

    @Test
    fun `should delete user successfully`() {
        justRun {
            userJpaRepository.deleteById(userId)
        }

        userPersistenceAdapter.deleteById(userId)

        verify(exactly = 1) {
            userJpaRepository.deleteById(userId)
        }
    }

    @Test
    fun `should propagate exception when deletion fails`() {
        every {
            userJpaRepository.deleteById(userId)
        } throws IllegalStateException(PERSISTENCE_FAILED)

        val exception =
            assertThrows(IllegalStateException::class.java) {
                userPersistenceAdapter.deleteById(userId)
            }

        assertEquals(PERSISTENCE_FAILED, exception.message)

        verify(exactly = 1) {
            userJpaRepository.deleteById(userId)
        }
    }

    @Test
    fun `should update user access successfully`() {
        val userEntity = UserPersistenceMapper.toEntity(user)
        val updatedAt = OffsetDateTime.now()

        val updatedUser =
            buildUser(
                userId = userId,
                updatedAt = updatedAt,
            ).apply {
                role = SCHOOL_ADMIN
                status = BLOCKED
            }

        every {
            userJpaRepository.findById(userId)
        } returns Optional.of(userEntity)

        every {
            userJpaRepository.save(userEntity)
        } answers { firstArg() }

        val result = userPersistenceAdapter.updateAccess(updatedUser)

        assertEquals(SCHOOL_ADMIN, result.role)
        assertEquals(BLOCKED, result.status)
        assertEquals(updatedAt, result.updatedAt)
        assertEquals(userId, result.userId)

        verify(exactly = 1) {
            userJpaRepository.findById(userId)
            userJpaRepository.save(userEntity)
        }
    }

    @Test
    fun `should preserve user data when updating access`() {
        val userEntity = UserPersistenceMapper.toEntity(user)

        val updatedUser =
            buildUser(
                userId = userId,
            ).apply {
                role = PLATFORM_ADMIN
                status = INACTIVE
            }

        every {
            userJpaRepository.findById(userId)
        } returns Optional.of(userEntity)

        every {
            userJpaRepository.save(userEntity)
        } answers { firstArg() }

        val result = userPersistenceAdapter.updateAccess(updatedUser)

        assertEquals(name, result.name)
        assertEquals(email, result.email.value)
        assertEquals(passwordHash, result.passwordHash)
        assertEquals(phone, result.phone)
        assertEquals(cpf, result.cpf?.value)
        assertEquals(rg, result.rg?.value)
        assertEquals(address, result.address)
        assertEquals(birthDate, result.birthDate)
        assertEquals(createdAt, result.createdAt)
    }

    @Test
    fun `should throw exception when updating access of nonexistent user`() {
        every {
            userJpaRepository.findById(userId)
        } returns Optional.empty()

        val exception =
            assertThrows(NoSuchElementException::class.java) {
                userPersistenceAdapter.updateAccess(user)
            }

        assertEquals(USER_NOT_FOUND, exception.message)

        verify(exactly = 1) {
            userJpaRepository.findById(userId)
        }

        verify(exactly = 0) {
            userJpaRepository.save(any())
        }
    }

    @Test
    fun `should update user with null phone`() {
        val user = buildUser(phone = null)
        val entity = UserPersistenceMapper.toEntity(user)

        every { userJpaRepository.findById(user.userId) } returns Optional.of(entity)
        every { userJpaRepository.save(any()) } answers { firstArg() }

        val result = userPersistenceAdapter.update(user)

        assertNull(result.phone)
        assertNull(entity.phone)
    }

    @Test
    fun `should update user with null cpf`() {
        val user = buildUser(cpf = null)
        val entity = UserPersistenceMapper.toEntity(user)

        every { userJpaRepository.findById(user.userId) } returns Optional.of(entity)

        val result = userPersistenceAdapter.update(user)

        assertNull(result.cpf)
        assertNull(entity.cpf)
    }

    @Test
    fun `should update user with null rg`() {
        val user = buildUser(rg = null)
        val entity = UserPersistenceMapper.toEntity(user)

        every { userJpaRepository.findById(user.userId) } returns Optional.of(entity)

        val result = userPersistenceAdapter.update(user)

        assertNull(result.rg)
        assertNull(entity.rg)
    }

    @Test
    fun `should check if login exists`() {
        val login = "diogo123"

        every { userJpaRepository.existsByLogin(login) } returns true

        val result = userPersistenceAdapter.existsByLogin(login)

        assertTrue(result)
        verify(exactly = 1) { userJpaRepository.existsByLogin(login) }
    }

    @Test
    fun `should find user by login`() {
        every {
            userJpaRepository.findByLogin(login)
        } returns UserPersistenceMapper.toEntity(user)

        val result = userPersistenceAdapter.findByLogin(login)

        assertNotNull(result)
        assertEquals(userId, result?.userId)
        assertEquals(login, result?.login?.value)

        verify(exactly = 1) {
            userJpaRepository.findByLogin(login)
        }
    }

    @Test
    fun `should return null when login does not exist`() {
        every {
            userJpaRepository.findByLogin(login)
        } returns null

        assertNull(userPersistenceAdapter.findByLogin(login))

        verify(exactly = 1) {
            userJpaRepository.findByLogin(login)
        }
    }

    @Test
    fun `should restrict users to candidates and authenticated school admin`() {
        val criteria =
            buildUserSearchCriteria().copy(
                allowedRole = CANDIDATE,
                allowedUserId = userId,
            )

        val rolePath = mockk<Path<UserRole>>()
        val userIdPath = mockk<Path<UUID>>()
        val rolePredicate = mockk<Predicate>()
        val userIdPredicate = mockk<Predicate>()
        val accessPredicate = mockk<Predicate>()

        every { root.get<UserRole>("role") } returns rolePath
        every { root.get<UUID>("userId") } returns userIdPath

        every { builder.equal(rolePath, CANDIDATE) } returns rolePredicate
        every { builder.equal(userIdPath, userId) } returns userIdPredicate
        every { builder.or(rolePredicate, userIdPredicate) } returns accessPredicate
        every { builder.and(accessPredicate) } returns predicate

        mockFindAll()

        val result = userPersistenceAdapter.findAll(criteria)

        assertTrue(result.content.isEmpty())

        verify(exactly = 1) {
            builder.equal(rolePath, CANDIDATE)
            builder.equal(userIdPath, userId)
            builder.or(rolePredicate, userIdPredicate)
            builder.and(accessPredicate)
        }
    }

    private fun mockFindAll() {
        every {
            userJpaRepository.findAll(
                any<Specification<UserEntity>>(),
                any<Pageable>(),
            )
        } answers {
            firstArg<Specification<UserEntity>>()
                .toPredicate(root, query, builder)

            PageImpl(emptyList<UserEntity>())
        }
    }

    companion object {
        @JvmStatic
        fun booleanProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(true),
                Arguments.of(false),
            )

        @JvmStatic
        fun textFilterProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of("name", "Diogo"),
                Arguments.of("email", "DIOGO@EMAIL.COM"),
                Arguments.of("phone", "11999999999"),
                Arguments.of("cpf", "12345678900"),
                Arguments.of("rg", "123456789"),
                Arguments.of("address", "São Paulo"),
            )

        @JvmStatic
        fun enumFilterProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of("role", "CANDIDATE"),
                Arguments.of("status", "ACTIVE"),
            )

        @JvmStatic
        fun dateFilterProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 12, 31),
                ),
                Arguments.of(
                    null,
                    LocalDate.of(2026, 12, 31),
                ),
                Arguments.of(
                    LocalDate.of(2026, 1, 1),
                    null,
                ),
            )

        @JvmStatic
        fun ignoredFilterProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of("name", null),
                Arguments.of(null, "Diogo"),
                Arguments.of("invalidField", "test"),
            )

        @JvmStatic
        fun independentEnumFilterProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of("role", "SCHOOL_ADMIN"),
                Arguments.of("status", "BLOCKED"),
            )

        @JvmStatic
        fun birthDateNullFilterProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(null, null, null),
                Arguments.of(LocalDate.of(1990, 1, 1), null, null),
                Arguments.of(null, LocalDate.of(2000, 12, 31), null),
                Arguments.of(null, null, LocalDate.of(1995, 5, 5)),
            )

        @JvmStatic
        fun independentTextFilterProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of("name", " Diogo "),
                Arguments.of("email", " DIOGO@EMAIL.COM "),
                Arguments.of("phone", "11999999999"),
                Arguments.of("cpf", "12345678900"),
                Arguments.of("rg", "123456789"),
                Arguments.of("address", " São Paulo "),
            )
    }
}
