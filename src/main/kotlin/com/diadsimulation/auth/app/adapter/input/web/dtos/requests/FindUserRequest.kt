package com.diadsimulation.auth.app.adapter.input.web.dtos.requests

import com.diadsimulation.auth.core.common.Messages.INVALID_USER_SEARCH_PARAMS
import jakarta.validation.constraints.AssertTrue
import java.util.UUID

data class FindUserRequest(
    val userId: UUID? = null,
    val email: String? = null,
    val login: String? = null,
    val cpf: String? = null,
    val rg: String? = null,
) {
    @get:AssertTrue(message = INVALID_USER_SEARCH_PARAMS)
    val isValid: Boolean
        get() =
            listOf(
                userId != null,
                !email.isNullOrBlank(),
                !login.isNullOrBlank(),
                !cpf.isNullOrBlank(),
                !rg.isNullOrBlank(),
            ).count { it } == 1
}
