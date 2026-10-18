package com.diadsimulation.auth.app.adapter.input.web.dtos.requests

import com.diadsimulation.auth.core.common.Messages.INVALID_USER_UPDATE_PARAMS
import jakarta.validation.constraints.AssertTrue
import java.time.LocalDate

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val login: String? = null,
    val password: String? = null,
    val phone: String? = null,
    val cpf: String? = null,
    val rg: String? = null,
    val address: String? = null,
    val birthDate: LocalDate? = null,
) {
    @get:AssertTrue(message = INVALID_USER_UPDATE_PARAMS)
    val isValid: Boolean
        get() =
            listOf(name, email, login, password, phone, cpf, rg, address)
                .any { !it.isNullOrBlank() } || birthDate != null
}
