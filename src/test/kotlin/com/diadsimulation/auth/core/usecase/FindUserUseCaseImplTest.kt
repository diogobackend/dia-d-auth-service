import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.domain.enums.SearchType
import com.diadsimulation.auth.core.domain.enums.SearchType.CPF
import com.diadsimulation.auth.core.domain.enums.SearchType.EMAIL
import com.diadsimulation.auth.core.domain.enums.SearchType.RG
import com.diadsimulation.auth.core.domain.enums.SearchType.USER_ID
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.output.UserRepositoryPort
import com.diadsimulation.auth.core.usecase.FindUserUseCaseImpl
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.NoSuchElementException
import java.util.UUID

@ExtendWith(MockKExtension::class)
class FindUserUseCaseImplTest {
    @MockK
    private lateinit var userRepositoryPort: UserRepositoryPort

    @InjectMockKs
    private lateinit var useCase: FindUserUseCaseImpl

    @ParameterizedTest
    @EnumSource(SearchType::class)
    fun `should return user when searching by a valid identifier`(searchType: SearchType) {
        val request = createSearchRequest(searchType)
        val expectedUser = mockk<User>()

        mockRepository(request, expectedUser)

        val result = execute(request)

        assertSame(expectedUser, result)
        verifyRepository(request)
    }

    @ParameterizedTest
    @EnumSource(SearchType::class)
    fun `should throw exception when user is not found`(searchType: SearchType) {
        val request = createSearchRequest(searchType)

        mockRepository(request, null)

        val exception =
            assertThrows<NoSuchElementException> {
                execute(request)
            }

        assertEquals(USER_NOT_FOUND, exception.message)
        verifyRepository(request)
    }

    @Test
    fun `should throw exception when no search parameter is provided`() {
        val exception =
            assertThrows<NoSuchElementException> {
                useCase.execute(
                    userId = null,
                    email = null,
                    cpf = null,
                    rg = null,
                )
            }

        assertEquals(USER_NOT_FOUND, exception.message)

        verify {
            userRepositoryPort wasNot Called
        }
    }

    private fun execute(request: FindUserTestData): User =
        useCase.execute(
            userId = request.userId,
            email = request.email,
            cpf = request.cpf,
            rg = request.rg,
        )

    private fun mockRepository(
        request: FindUserTestData,
        user: User?,
    ) {
        when (request.searchType) {
            USER_ID -> {
                every {
                    userRepositoryPort.findById(requireNotNull(request.userId))
                } returns user
            }

            EMAIL -> {
                every {
                    userRepositoryPort.findByEmail(requireNotNull(request.email))
                } returns user
            }

            CPF -> {
                every {
                    userRepositoryPort.findByCpf(requireNotNull(request.cpf))
                } returns user
            }

            RG -> {
                every {
                    userRepositoryPort.findByRg(requireNotNull(request.rg))
                } returns user
            }
        }
    }

    private fun verifyRepository(request: FindUserTestData) {
        when (request.searchType) {
            USER_ID -> {
                verify(exactly = 1) {
                    userRepositoryPort.findById(requireNotNull(request.userId))
                }
            }

            EMAIL -> {
                verify(exactly = 1) {
                    userRepositoryPort.findByEmail(requireNotNull(request.email))
                }
            }

            CPF -> {
                verify(exactly = 1) {
                    userRepositoryPort.findByCpf(requireNotNull(request.cpf))
                }
            }

            RG -> {
                verify(exactly = 1) {
                    userRepositoryPort.findByRg(requireNotNull(request.rg))
                }
            }
        }
    }

    private fun createSearchRequest(searchType: SearchType): FindUserTestData =
        when (searchType) {
            USER_ID -> {
                FindUserTestData(
                    searchType = searchType,
                    userId = UUID.randomUUID(),
                )
            }

            EMAIL -> {
                FindUserTestData(
                    searchType = searchType,
                    email = "usuario-${UUID.randomUUID()}@email.com",
                )
            }

            CPF -> {
                FindUserTestData(
                    searchType = searchType,
                    cpf = "12345678901",
                )
            }

            RG -> {
                FindUserTestData(
                    searchType = searchType,
                    rg = "123456789",
                )
            }
        }

    private data class FindUserTestData(
        val searchType: SearchType,
        val userId: UUID? = null,
        val email: String? = null,
        val cpf: String? = null,
        val rg: String? = null,
    )
}
