package com.diadsimulation.auth.builders

import com.diadsimulation.auth.app.adapter.input.web.dtos.RegisterUserRequest
import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE

fun buildRegisterUserRequest(
    email: String = "candidate@diadsimulation.com",
    name: String = "Diogo Ferreira",
    password: String = "Diad@123456",
    role: UserRole = CANDIDATE,
    phone: String? = null,
    address: String? = null,
    cpf: String? = null,
    rg: String? = null,
) = RegisterUserRequest(
    email = email,
    name = name,
    password = password,
    role = role,
    phone = phone,
    address = address,
    cpf = cpf,
    rg = rg,
)
