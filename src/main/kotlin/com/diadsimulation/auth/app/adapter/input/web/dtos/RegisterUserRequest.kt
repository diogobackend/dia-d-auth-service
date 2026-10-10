package com.diadsimulation.auth.app.adapter.input.web.dtos

import com.diadsimulation.auth.core.common.Messages.EMAIL_INVALID
import com.diadsimulation.auth.core.common.Messages.EMAIL_REQUIRED
import com.diadsimulation.auth.core.common.Messages.NAME_REQUIRED
import com.diadsimulation.auth.core.common.Messages.PASSWORD_REQUIRED
import com.diadsimulation.auth.core.domain.enums.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class RegisterUserRequest(
    @field:NotBlank(message = NAME_REQUIRED)
    @field:Size(max = 150, message = "Nome deve possuir no máximo 150 caracteres")
    val name: String,
    @field:NotBlank(message = EMAIL_REQUIRED)
    @field:Email(message = EMAIL_INVALID)
    @field:Size(max = 255, message = "E-mail deve possuir no máximo 255 caracteres")
    val email: String,
    @field:NotBlank(message = PASSWORD_REQUIRED)
    @field:Size(
        min = 8,
        max = 100,
        message = "Senha deve possuir entre 8 e 100 caracteres",
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
    val rg: String? = null,
)
