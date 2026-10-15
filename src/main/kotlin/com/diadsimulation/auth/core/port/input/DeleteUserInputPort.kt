package com.diadsimulation.auth.core.port.input

import java.util.UUID

interface DeleteUserInputPort {
    fun execute(userId: UUID)
}
