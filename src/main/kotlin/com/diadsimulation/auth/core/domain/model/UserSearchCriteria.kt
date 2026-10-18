package com.diadsimulation.auth.core.domain.model

import com.diadsimulation.auth.core.domain.enums.SortDirection
import com.diadsimulation.auth.core.domain.enums.SortDirection.ASC
import com.diadsimulation.auth.core.domain.enums.UserRole
import java.time.LocalDate
import java.util.UUID

data class UserSearchCriteria(
    val page: Int = 0,
    val size: Int = 10,
    val sortBy: String = "name",
    val direction: SortDirection = ASC,
    val filterBy: String? = null,
    val filterValue: String? = null,
    val name: String? = null,
    val email: String? = null,
    val login: String? = null,
    val phone: String? = null,
    val cpf: String? = null,
    val rg: String? = null,
    val address: String? = null,
    val role: String? = null,
    val status: String? = null,
    val dateField: String? = null,
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val dateExact: LocalDate? = null,
    val birthDate: LocalDate? = null,
    val createdAt: LocalDate? = null,
    val updatedAt: LocalDate? = null,
    val allowedRole: UserRole? = null,
    val allowedUserId: UUID? = null,
)
