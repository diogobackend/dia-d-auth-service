package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.builders.buildUser
import com.diadsimulation.auth.core.common.Messages.PERSISTENCE_FAILED
import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.port.output.UserRepositoryOutputPort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.NoSuchElementException

@ExtendWith(MockKExtension::class)
class DeleteUserUseCaseImplTest {
    @MockK
    private lateinit var userRepositoryPort: UserRepositoryOutputPort

    @InjectMockKs
    private lateinit var deleteUserUseCaseImpl: DeleteUserUseCaseImplementation

    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        user = buildUser()
    }

    @Test
    fun `should delete user successfully`() {
        every {
            userRepositoryPort.findById(user.userId)
        } returns user

        justRun {
            userRepositoryPort.deleteById(user.userId)
        }

        deleteUserUseCaseImpl.execute(user.userId)

        verifyOrder {
            userRepositoryPort.findById(user.userId)
            userRepositoryPort.deleteById(user.userId)
        }

        verify(exactly = 1) {
            userRepositoryPort.deleteById(user.userId)
        }
    }

    @Test
    fun `should throw exception when user does not exist`() {
        every {
            userRepositoryPort.findById(user.userId)
        } returns null

        val exception =
            assertThrows(NoSuchElementException::class.java) {
                deleteUserUseCaseImpl.execute(user.userId)
            }

        assertEquals(USER_NOT_FOUND, exception.message)

        verify(exactly = 0) {
            userRepositoryPort.deleteById(any())
        }
    }

    @Test
    fun `should propagate exception when deletion fails`() {
        every {
            userRepositoryPort.findById(user.userId)
        } returns user

        every {
            userRepositoryPort.deleteById(user.userId)
        } throws IllegalStateException(PERSISTENCE_FAILED)

        val exception =
            assertThrows(IllegalStateException::class.java) {
                deleteUserUseCaseImpl.execute(user.userId)
            }

        assertEquals(PERSISTENCE_FAILED, exception.message)

        verify(exactly = 1) {
            userRepositoryPort.deleteById(user.userId)
        }
    }
}
