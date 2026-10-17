
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
import org.springframework.format.annotation.DateTimeFormat.ISO.DATE
import java.time.LocalDate

data class ListUsersRequest(
    @field:Min(value = 0, message = PAGE_CANNOT_BE_NEGATIVE)
    val page: Int = 0,
    @field:Min(value = 1, message = PAGE_SIZE_MIN_INVALID)
    @field:Max(value = 100, message = PAGE_SIZE_MAX_INVALID)
    val size: Int = 10,
    @field:Pattern(
        regexp = "userId|name|email|login|role|status|phone|cpf|rg|address|birthDate|createdAt|updatedAt",
        message = INVALID_SORT_FIELD,
    )
    val sortBy: String = "name",
    val direction: Direction = ASC,
    @field:Pattern(
        regexp = "name|email|login|role|status|phone|cpf|rg|address",
        message = INVALID_FILTER_FIELD,
    )
    val filterBy: String? = null,
    val filterValue: String? = null,
    val name: String? = null,
    val email: String? = null,
    val login: String? = null,
    val phone: String? = null,
    val cpf: String? = null,
    val rg: String? = null,
    val address: String? = null,
    @field:Pattern(
        regexp = "PLATFORM_ADMIN|SCHOOL_ADMIN|CANDIDATE",
        message = INVALID_FILTER_FIELD,
    )
    val role: String? = null,
    @field:Pattern(
        regexp = "ACTIVE|INACTIVE|BLOCKED",
        message = INVALID_FILTER_FIELD,
    )
    val status: String? = null,
    @field:Pattern(
        regexp = "createdAt|updatedAt|birthDate",
        message = INVALID_FILTER_FIELD,
    )
    val dateField: String? = null,
    @field:DateTimeFormat(iso = DATE)
    val dateFrom: LocalDate? = null,
    @field:DateTimeFormat(iso = DATE)
    val dateTo: LocalDate? = null,
    @field:DateTimeFormat(iso = DATE)
    val dateExact: LocalDate? = null,
    @field:DateTimeFormat(iso = DATE)
    val birthDate: LocalDate? = null,
    @field:DateTimeFormat(iso = DATE)
    val createdAt: LocalDate? = null,
    @field:DateTimeFormat(iso = DATE)
    val updatedAt: LocalDate? = null,
)
