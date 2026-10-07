package com.diadsimulation.auth.core.domain.exceptions

class RgAlreadyExistsException(
    rg: String
) : RuntimeException("Já existe um usuário cadastrado com o RG: $rg")