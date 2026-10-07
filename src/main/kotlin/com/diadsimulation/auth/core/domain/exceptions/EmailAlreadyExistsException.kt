package com.diadsimulation.auth.core.domain.exceptions

class EmailAlreadyExistsException(
    email: String
) : RuntimeException("Já existe um usuário cadastrado com o e-mail: $email")