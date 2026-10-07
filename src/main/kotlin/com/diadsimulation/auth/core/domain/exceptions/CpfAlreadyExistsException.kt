package com.diadsimulation.auth.core.domain.exceptions

class CpfAlreadyExistsException(
    cpf: String
) : RuntimeException("Já existe um usuário cadastrado com o CPF: $cpf")