package com.diadsimulation.auth.core.domain.exception

class UserAlreadyExistsException(
    email: String
) : RuntimeException("User already exists with email: $email")