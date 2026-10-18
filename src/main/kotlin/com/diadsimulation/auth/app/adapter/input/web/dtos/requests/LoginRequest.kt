package com.diadsimulation.auth.app.adapter.input.web.dtos.requests

import com.diadsimulation.auth.core.common.Messages.INVALID_LOGIN_PARAMS
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:Email
    val email: String? = null,
    val login: String? = null,
    @field:NotBlank
    val password: String,
) {
    @get:AssertTrue(message = INVALID_LOGIN_PARAMS)
    val isValid: Boolean
        get() =
            (email?.isNotBlank() == true) !=
                (login?.isNotBlank() == true)
}
