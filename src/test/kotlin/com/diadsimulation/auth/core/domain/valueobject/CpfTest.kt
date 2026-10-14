package com.diadsimulation.auth.core.domain.valueobject

import com.diadsimulation.auth.core.common.Messages.CPF_INVALID
import com.diadsimulation.auth.core.common.Messages.CPF_REQUIRED
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class CpfTest {
    @ParameterizedTest
    @MethodSource("validCpfs")
    fun `should create cpf when valid`(value: String) {
        val cpf = Cpf(value)

        assertEquals(value, cpf.value)
    }

    @ParameterizedTest
    @MethodSource("invalidCpfs")
    fun `should throw exception when cpf is invalid`(value: String) {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Cpf(value)
            }

        assertEquals(CPF_INVALID, exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "   "])
    fun `should throw exception when cpf is blank`(value: String) {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Cpf(value)
            }

        assertEquals(CPF_REQUIRED, exception.message)
    }

    companion object {
        @JvmStatic
        fun validCpfs() =
            listOf(
                "529.982.247-25",
                "52998224725",
                "613.098.083-33",
                "61309808333",
                "111.444.777-35",
            )

        @JvmStatic
        fun invalidCpfs() =
            listOf(
                "5299.8224.7-25",
                "529.982.24725",
                "529982247-25",
                "529.982.247.25",
                "5299822472",
                "529982247255",
                "5299822472A",
                "111.111.111-11",
                "00000000000",
                "22222222222",
                "123.456.789-00",
                "613.099.083-33",
            )
    }
}
