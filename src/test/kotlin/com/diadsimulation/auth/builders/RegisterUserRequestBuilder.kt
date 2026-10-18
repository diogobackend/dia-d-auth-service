package com.diadsimulation.auth.builders

import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.RegisterUserRequest
import java.time.LocalDate

fun buildRegisterUserRequest(
    email: String = "candidate@diadsimulation.com",
    name: String = "Diogo Ferreira",
    login: String = "dio123",
    password: String = "Diad@123456",
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
    phone = phone,
    address = address,
    cpf = cpf,
    rg = rg,
    birthDate = birthDate,
)
