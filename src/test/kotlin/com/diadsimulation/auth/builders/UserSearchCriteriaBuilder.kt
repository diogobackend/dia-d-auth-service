package com.diadsimulation.auth.builders

import com.diadsimulation.auth.core.domain.enums.SortDirection
import com.diadsimulation.auth.core.domain.enums.SortDirection.ASC
import com.diadsimulation.auth.core.domain.model.UserSearchCriteria
import java.time.LocalDate

fun buildUserSearchCriteria(
    page: Int = 0,
    size: Int = 10,
    sortBy: String = "name",
    direction: SortDirection = ASC,
    filterBy: String? = null,
    filterValue: String? = null,
    name: String? = null,
    email: String? = null,
    phone: String? = null,
    cpf: String? = null,
    rg: String? = null,
    address: String? = null,
    role: String? = null,
    status: String? = null,
    dateField: String? = null,
    dateFrom: LocalDate? = null,
    dateTo: LocalDate? = null,
    dateExact: LocalDate? = null,
    birthDate: LocalDate? = null,
    createdAt: LocalDate? = null,
    updatedAt: LocalDate? = null,
) = UserSearchCriteria(
    page = page,
    size = size,
    sortBy = sortBy,
    direction = direction,
    filterBy = filterBy,
    filterValue = filterValue,
    name = name,
    email = email,
    phone = phone,
    cpf = cpf,
    rg = rg,
    address = address,
    role = role,
    status = status,
    dateField = dateField,
    dateFrom = dateFrom,
    dateTo = dateTo,
    dateExact = dateExact,
    birthDate = birthDate,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
