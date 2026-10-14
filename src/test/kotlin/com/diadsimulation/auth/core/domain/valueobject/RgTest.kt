package com.diadsimulation.auth.core.domain.valueobject

import com.diadsimulation.auth.core.common.Messages.RG_INVALID
import com.diadsimulation.auth.core.common.Messages.RG_REQUIRED
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class RgTest {
    @ParameterizedTest
    @MethodSource("validRgs")
    fun `should create rg when valid`(value: String) {
        val rg = Rg(value)

        assertEquals(value, rg.value)
    }

    @ParameterizedTest
    @MethodSource("invalidRgs")
    fun `should throw exception when rg is invalid`(value: String) {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Rg(value)
            }

        assertEquals(RG_INVALID, exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "   "])
    fun `should throw exception when rg is blank`(value: String) {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Rg(value)
            }

        assertEquals(RG_REQUIRED, exception.message)
    }

    companion object {
        @JvmStatic
        fun validRgs() =
            listOf(
                "04.717.957/2013-4",
                "047.179.572.013-4",
                "0471795720134",
                "1111111111111",
            )

        @JvmStatic
        fun invalidRgs() =
            listOf(
                "047.179.572/013-4",
                "04.717.957.2013-4",
                "04.717.957/20134",
                "047179572013",
                "04717957201344",
                "047179572013A",
                "047 1795720134",
                "123456789",
                "987654321",
                "04.717.957/2013",
                "047.179.572.0134",
            )
    }
}
