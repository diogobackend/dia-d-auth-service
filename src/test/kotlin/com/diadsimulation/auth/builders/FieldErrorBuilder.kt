package com.diadsimulation.auth.builders

import com.diadsimulation.auth.core.common.Messages.EMAIL_REQUIRED
import com.diadsimulation.auth.core.common.Messages.REGISTER_USER_REQUEST
import org.springframework.validation.FieldError

fun buildFieldError(
    field: String = "email",
    message: String? = EMAIL_REQUIRED,
    objectName: String = REGISTER_USER_REQUEST,
): FieldError =
    FieldError(
        objectName,
        field,
        null,
        false,
        null,
        null,
        message,
    )
