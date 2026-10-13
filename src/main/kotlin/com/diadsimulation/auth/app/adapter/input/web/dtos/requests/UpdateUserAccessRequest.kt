package com.diadsimulation.auth.app.adapter.input.web.dtos.requests

import com.diadsimulation.auth.core.common.Messages.INVALID_REQUEST_DATA
import com.diadsimulation.auth.core.domain.enums.UserRole
import com.diadsimulation.auth.core.domain.enums.UserStatus
import jakarta.validation.constraints.AssertTrue

data class UpdateUserAccessRequest(
    val role: UserRole? = null,
    val status: UserStatus? = null,
) {
    @get:AssertTrue(message = INVALID_REQUEST_DATA)
    val isValid: Boolean
        get() = role != null || status != null
}
