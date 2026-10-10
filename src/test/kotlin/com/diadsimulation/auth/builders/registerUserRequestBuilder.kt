package com.diadsimulation.auth.builders

import com.diadsimulation.auth.app.adapter.input.web.dtos.RegisterUserRequest
import com.diadsimulation.auth.core.domain.enums.UserRole

fun buildRegisterUserRequest(
    email: String = "candidate@diadsimulation.com",
    password: String = "Diad@123456",
    role: UserRole = UserRole.CANDIDATE,
    phone: String? = null,
    address: String? = null,
    cpf: String? = null,
    rg: String? = null
) = RegisterUserRequest(
    email = email,
    password = password,
    role = role,
    phone = phone,
    address = address,
    cpf = cpf,
    rg = rg
)