package com.diadsimulation.auth.core.port.input

import com.diadsimulation.auth.core.domain.model.User

interface RegisterUserInputPort {
    fun execute(user: User): User
}
