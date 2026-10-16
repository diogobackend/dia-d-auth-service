package com.diadsimulation.auth.core.port.output

import java.util.UUID

interface TokenGeneratorOutputPort {
    fun generate(
        userId: UUID,
        role: String,
    ): String
}
