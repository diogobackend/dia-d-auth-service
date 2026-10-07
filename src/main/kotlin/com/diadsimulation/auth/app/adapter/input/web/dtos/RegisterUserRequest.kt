package com.diadsimulation.auth.app.adapter.input.web.dtos

import com.diadsimulation.auth.core.domain.enums.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class RegisterUserRequest(

    @field:NotBlank(message = "E-mail é obrigatório")
    @field:Email(message = "E-mail inválido")
    @field:Size(max = 255, message = "E-mail deve possuir no máximo 255 caracteres")
    val email: String,

    @field:NotBlank(message = "Senha é obrigatória")
    @field:Size(
        min = 8,
        max = 100,
        message = "Senha deve possuir entre 8 e 100 caracteres"
    )
    val password: String,

    @field:NotNull(message = "Perfil é obrigatório")
    val role: UserRole,

    @field:Size(max = 30, message = "Telefone deve possuir no máximo 30 caracteres")
    val phone: String? = null,

    @field:Size(max = 500, message = "Endereço deve possuir no máximo 500 caracteres")
    val address: String? = null,

    @field:Size(max = 14, message = "CPF deve possuir no máximo 14 caracteres")
    val cpf: String? = null,

    @field:Size(max = 20, message = "RG deve possuir no máximo 20 caracteres")
    val rg: String? = null
)