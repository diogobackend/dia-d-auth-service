package com.diadsimulation.auth.builders

import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.RegisterUserRequest
import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserRole.CANDIDATE
import java.time.LocalDate

fun buildRegisterUserRequest(
    email: String = "candidate@diadsimulation.com",
    name: String = "Diogo Ferreira",
    login: String = "dio123",
    password: String = "Diad@123456",
    role: UserRole = CANDIDATE,
    phone: String? = null,
    address: String? = null,
    cpf: String? = null,
    rg: String? = null,
    birthDate: LocalDate? = null,
) = RegisterUserRequest(
    email = email,
    name = name,
    login = login,
    password = password,
    role = role,
    phone = phone,
    address = address,
    cpf = cpf,
    rg = rg,
    birthDate = birthDate,
)
