package com.diadsimulation.auth.app.adapter.input.web.dtos.requests

import com.diadsimulation.auth.core.common.Messages.ADDRESS_MAX_LENGTH
import com.diadsimulation.auth.core.common.Messages.BIRTH_DATE_FUTURE
import com.diadsimulation.auth.core.common.Messages.CPF_MAX_LENGTH
import com.diadsimulation.auth.core.common.Messages.EMAIL_INVALID
import com.diadsimulation.auth.core.common.Messages.EMAIL_MAX_LENGTH
import com.diadsimulation.auth.core.common.Messages.EMAIL_REQUIRED
import com.diadsimulation.auth.core.common.Messages.LOGIN_LENGTH
import com.diadsimulation.auth.core.common.Messages.LOGIN_LOWERCASE_REQUIRED
import com.diadsimulation.auth.core.common.Messages.LOGIN_MUST_START_WITH_LETTER
import com.diadsimulation.auth.core.common.Messages.LOGIN_REQUIRED
import com.diadsimulation.auth.core.common.Messages.LOGIN_WHITESPACE_NOT_ALLOWED
import com.diadsimulation.auth.core.common.Messages.NAME_MAX_LENGTH
import com.diadsimulation.auth.core.common.Messages.NAME_REQUIRED
import com.diadsimulation.auth.core.common.Messages.PASSWORD_LENGTH
import com.diadsimulation.auth.core.common.Messages.PASSWORD_REQUIRED
import com.diadsimulation.auth.core.common.Messages.PHONE_MAX_LENGTH
import com.diadsimulation.auth.core.common.Messages.RG_MAX_LENGTH
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class RegisterUserRequest(
    @field:NotBlank(message = NAME_REQUIRED)
    @field:Size(max = 150, message = NAME_MAX_LENGTH)
    val name: String,
    @field:NotBlank(message = EMAIL_REQUIRED)
    @field:Email(message = EMAIL_INVALID)
    @field:Size(max = 255, message = EMAIL_MAX_LENGTH)
    val email: String,
    @field:NotBlank(message = LOGIN_REQUIRED)
    @field:Size(min = 3, max = 35, message = LOGIN_LENGTH)
    @field:Pattern(
        regexp = "^[a-z].*$",
        message = LOGIN_MUST_START_WITH_LETTER,
    )
    @field:Pattern(
        regexp = "^[^A-Z]*$",
        message = LOGIN_LOWERCASE_REQUIRED,
    )
    @field:Pattern(
        regexp = "^\\S+$",
        message = LOGIN_WHITESPACE_NOT_ALLOWED,
    )
    val login: String,
    @field:NotBlank(message = PASSWORD_REQUIRED)
    @field:Size(
        min = 8,
        max = 100,
        message = PASSWORD_LENGTH,
    )
    val password: String,
    @field:Size(max = 30, message = PHONE_MAX_LENGTH)
    val phone: String? = null,
    @field:Size(max = 500, message = ADDRESS_MAX_LENGTH)
    val address: String? = null,
    @field:Size(max = 14, message = CPF_MAX_LENGTH)
    val cpf: String? = null,
    @field:Size(max = 20, message = RG_MAX_LENGTH)
    val rg: String? = null,
    @field:PastOrPresent(message = BIRTH_DATE_FUTURE)
    val birthDate: LocalDate? = null,
)
