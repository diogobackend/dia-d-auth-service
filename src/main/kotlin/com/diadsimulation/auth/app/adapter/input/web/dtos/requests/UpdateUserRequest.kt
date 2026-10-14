package com.diadsimulation.auth.app.adapter.input.web.dtos.requests

import com.diadsimulation.auth.core.common.Messages.INVALID_USER_UPDATE_PARAMS
import jakarta.validation.constraints.AssertTrue

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val phone: String? = null,
    val cpf: String? = null,
    val address: String? = null,
) {
    @get:AssertTrue(message = INVALID_USER_UPDATE_PARAMS)
    val isValid: Boolean
        get() =
            listOf(name, email, password, phone, cpf, address)
                .any { !it.isNullOrBlank() }
}
