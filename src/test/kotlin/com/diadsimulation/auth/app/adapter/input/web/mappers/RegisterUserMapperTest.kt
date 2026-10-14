package com.diadsimulation.auth.app.adapter.input.web.mappers

import com.diadsimulation.auth.builders.buildRegisterUserRequest
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import com.diadsimulation.auth.core.domain.enums.UserStatus.ACTIVE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class RegisterUserMapperTest {
    @Test
    fun `should map request to domain with all fields`() {
        val request =
            buildRegisterUserRequest(
                phone = "98999999999",
                address = "São Luís - MA",
                cpf = "529.982.247-25",
                rg = "04.717.957/2013-4",
            )

        val result = RegisterUserMapper.toDomain(request)

        assertNotNull(result.userId)
        assertEquals(request.name, result.name)
        assertEquals(request.email, result.email.value)
        assertEquals(request.password, result.passwordHash)
        assertEquals(CANDIDATE, result.role)
        assertEquals(ACTIVE, result.status)
        assertOptionalFields(
            expectedPhone = request.phone,
            expectedAddress = request.address,
            expectedCpf = request.cpf,
            expectedRg = request.rg,
            actualPhone = result.phone?.value,
            actualAddress = result.address,
            actualCpf = result.cpf?.value,
            actualRg = result.rg?.value,
        )
    }

    @ParameterizedTest
    @MethodSource("optionalFieldsProvider")
    fun `should normalize optional fields according to current implementation`(
        input: String?,
        expected: String?,
    ) {
        val request =
            buildRegisterUserRequest(
                phone = input,
                address = input,
                cpf = input,
                rg = input,
            )

        val result = RegisterUserMapper.toDomain(request)

        assertOptionalFields(
            expectedPhone = expected,
            expectedAddress = expected,
            expectedCpf = expected,
            expectedRg = expected,
            actualPhone = result.phone?.value,
            actualAddress = result.address,
            actualCpf = result.cpf?.value,
            actualRg = result.rg?.value,
        )
    }

    @Test
    fun `should map domain to response`() {
        val user =
            RegisterUserMapper.toDomain(
                buildRegisterUserRequest(
                    phone = "98999999999",
                    address = "São Luís - MA",
                    cpf = "529.982.247-25",
                    rg = "04.717.957/2013-4",
                ),
            )

        val response = RegisterUserMapper.toResponse(user)

        assertEquals(user.userId, response.userId)
        assertEquals(user.name, response.name)
        assertEquals(user.email.value, response.email)
        assertEquals(user.role, response.role)
        assertEquals(user.status, response.status)
        assertEquals(user.createdAt, response.createdAt)

        assertOptionalFields(
            expectedPhone = user.phone?.value,
            expectedAddress = user.address,
            expectedCpf = user.cpf?.value,
            expectedRg = user.rg?.value,
            actualPhone = response.phone,
            actualAddress = response.address,
            actualCpf = response.cpf,
            actualRg = response.rg,
        )
    }

    @Test
    fun `should map domain to response with null phone`() {
        val user =
            RegisterUserMapper.toDomain(
                buildRegisterUserRequest(phone = null),
            )

        val response = RegisterUserMapper.toResponse(user)

        assertNull(response.phone)
    }

    private fun assertOptionalFields(
        expectedPhone: String?,
        expectedAddress: String?,
        expectedCpf: String?,
        expectedRg: String?,
        actualPhone: String?,
        actualAddress: String?,
        actualCpf: String?,
        actualRg: String?,
    ) {
        assertEquals(expectedPhone, actualPhone)
        assertEquals(expectedAddress, actualAddress)
        assertEquals(expectedCpf, actualCpf)
        assertEquals(expectedRg, actualRg)
    }

    companion object {
        @JvmStatic
        fun optionalFieldsProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(null, null),
                Arguments.of("", null),
                Arguments.of("   ", null),
            )
    }
}
