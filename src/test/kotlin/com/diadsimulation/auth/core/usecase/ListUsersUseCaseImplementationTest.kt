package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.builders.buildPagedResult
import com.diadsimulation.auth.builders.buildUserSearchCriteria
import com.diadsimulation.auth.core.common.Messages.DATE_FIELD_REQUIRED
import com.diadsimulation.auth.core.common.Messages.FILTER_PARAMETERS_REQUIRED
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.model.UserSearchCriteria
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class ListUsersUseCaseImplementationTest {
    @MockK
    private lateinit var userRepositoryOutputPort: UserRepositoryOutputPort

    @InjectMockKs
    private lateinit var listUsersUseCaseImplementation: ListUsersUseCaseImplementation

    @ParameterizedTest
    @MethodSource("validFilterProvider")
    fun `should list users with valid filter parameters`(
        filterBy: String?,
        filterValue: String?,
    ) {
        val criteria =
            buildUserSearchCriteria(
                filterBy = filterBy,
                filterValue = filterValue,
            )

        val expected = buildPagedResult<User>()

        every { userRepositoryOutputPort.findAll(criteria) } returns expected

        val result = listUsersUseCaseImplementation.execute(criteria)

        assertEquals(expected, result)

        verify(exactly = 1) {
            userRepositoryOutputPort.findAll(criteria)
        }
    }

    @ParameterizedTest
    @MethodSource("invalidFilterProvider")
    fun `should reject incomplete filter parameters`(
        filterBy: String?,
        filterValue: String?,
    ) {
        val criteria =
            buildUserSearchCriteria(
                filterBy = filterBy,
                filterValue = filterValue,
            )

        val exception =
            assertThrows<IllegalArgumentException> {
                listUsersUseCaseImplementation.execute(criteria)
            }

        assertEquals(FILTER_PARAMETERS_REQUIRED, exception.message)

        verify(exactly = 0) {
            userRepositoryOutputPort.findAll(any())
        }
    }

    @ParameterizedTest
    @MethodSource("invalidDateFilterProvider")
    fun `should reject date filters without date field`(userSearchCriteria: UserSearchCriteria) {
        val exception =
            assertThrows<IllegalArgumentException> {
                listUsersUseCaseImplementation.execute(userSearchCriteria)
            }

        assertEquals(DATE_FIELD_REQUIRED, exception.message)

        verify(exactly = 0) {
            userRepositoryOutputPort.findAll(any())
        }
    }

    @ParameterizedTest
    @MethodSource("validDateFilterProvider")
    fun `should list users with valid date filters`(userSearchCriteria: UserSearchCriteria) {
        val expected = buildPagedResult<User>()

        every { userRepositoryOutputPort.findAll(userSearchCriteria) } returns expected

        val result = listUsersUseCaseImplementation.execute(userSearchCriteria)

        assertEquals(expected, result)

        verify(exactly = 1) {
            userRepositoryOutputPort.findAll(userSearchCriteria)
        }
    }

    companion object {
        @JvmStatic
        fun validFilterProvider() =
            Stream.of(
                Arguments.of(null, null),
                Arguments.of("name", "Diogo"),
                Arguments.of("email", "diogo@email.com"),
            )

        @JvmStatic
        fun invalidFilterProvider() =
            Stream.of(
                Arguments.of("name", null),
                Arguments.of(null, "Diogo"),
            )

        @JvmStatic
        fun invalidDateFilterProvider() =
            Stream.of(
                Arguments.of(buildUserSearchCriteria(dateFrom = LocalDate.of(2026, 10, 11))),
                Arguments.of(buildUserSearchCriteria(dateTo = LocalDate.of(2026, 10, 11))),
                Arguments.of(buildUserSearchCriteria(dateExact = LocalDate.of(2026, 10, 11))),
            )

        @JvmStatic
        fun validDateFilterProvider() =
            Stream.of(
                Arguments.of(buildUserSearchCriteria(dateField = "createdAt")),
                Arguments.of(
                    buildUserSearchCriteria(
                        dateField = "createdAt",
                        dateFrom = LocalDate.of(2026, 10, 11),
                    ),
                ),
                Arguments.of(
                    buildUserSearchCriteria(
                        dateField = "updatedAt",
                        dateTo = LocalDate.of(2026, 10, 11),
                    ),
                ),
                Arguments.of(
                    buildUserSearchCriteria(
                        dateField = "createdAt",
                        dateExact = LocalDate.of(2026, 10, 11),
                    ),
                ),
            )
    }
}
