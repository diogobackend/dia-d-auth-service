package com.diadsimulation.auth.core.port.input

import java.util.UUID

interface DeleteUserUseCase {
    fun execute(userId: UUID)
}
