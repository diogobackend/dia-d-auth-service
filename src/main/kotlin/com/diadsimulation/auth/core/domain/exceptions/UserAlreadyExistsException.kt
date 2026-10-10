package com.diadsimulation.auth.core.domain.exceptions

class UserAlreadyExistsException(
    message: String
) : RuntimeException(message)