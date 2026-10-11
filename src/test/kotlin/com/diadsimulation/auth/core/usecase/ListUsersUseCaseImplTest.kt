package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.builders.buildPagedResult
import com.diadsimulation.auth.builders.buildUserSearchCriteria
import com.diadsimulation.auth.core.common.Messages.FILTER_PARAMETERS_REQUIRED
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.output.UserRepositoryPort
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
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class ListUsersUseCaseImplTest {
    @MockK
    private lateinit var userRepositoryPort: UserRepositoryPort

    @InjectMockKs
    private lateinit var listUsersUseCaseImpl: ListUsersUseCaseImpl

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

        every { userRepositoryPort.findAll(criteria) } returns expected

        val result = listUsersUseCaseImpl.execute(criteria)

        assertEquals(expected, result)

        verify(exactly = 1) {
            userRepositoryPort.findAll(criteria)
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
                listUsersUseCaseImpl.execute(criteria)
            }

        assertEquals(FILTER_PARAMETERS_REQUIRED, exception.message)

        verify(exactly = 0) {
            userRepositoryPort.findAll(any())
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
    }
}
