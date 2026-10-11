
package com.diadsimulation.auth.app.adapter.input.web.dtos.requests

import com.diadsimulation.auth.core.common.Messages.INVALID_FILTER_FIELD
import com.diadsimulation.auth.core.common.Messages.INVALID_SORT_FIELD
import com.diadsimulation.auth.core.common.Messages.PAGE_CANNOT_BE_NEGATIVE
import com.diadsimulation.auth.core.common.Messages.PAGE_SIZE_MAX_INVALID
import com.diadsimulation.auth.core.common.Messages.PAGE_SIZE_MIN_INVALID
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class ListUsersRequest(
    @field:Min(value = 0, message = PAGE_CANNOT_BE_NEGATIVE)
    val page: Int = 0,
    @field:Min(value = 1, message = PAGE_SIZE_MIN_INVALID)
    @field:Max(value = 100, message = PAGE_SIZE_MAX_INVALID)
    val size: Int = 10,
    @field:Pattern(
        regexp = "userId|name|email|role|status|phone|cpf|rg|address|createdAt|updatedAt",
        message = INVALID_SORT_FIELD,
    )
    val sortBy: String = "name",
    val direction: Direction = ASC,
    @field:Pattern(
        regexp = "name|email|role|status|phone|cpf|rg|address",
        message = INVALID_FILTER_FIELD,
    )
    val filterBy: String? = null,
    val filterValue: String? = null,
    @field:Pattern(
        regexp = "createdAt|updatedAt",
        message = INVALID_FILTER_FIELD,
    )
    val dateField: String? = null,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val dateFrom: LocalDate? = null,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val dateTo: LocalDate? = null,
)
