package com.diadsimulation.auth.app.adapter.input.web.dtos

import com.diadsimulation.auth.core.domain.enums.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class RegisterUserRequest(

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    @field:Size(min = 8, max = 100)
    val password: String,

    @field:NotNull
    val role: UserRole,
    val phone: String? = null,
    val address: String? = null,
    val cpf: String? = null,
    val rg: String? = null
)
