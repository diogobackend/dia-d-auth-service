package com.diadsimulation.auth.core.port.output

import com.diadsimulation.auth.core.domain.model.AuthenticatedUser

interface TokenValidatorOutputPort {
    fun validate(token: String): AuthenticatedUser
}
