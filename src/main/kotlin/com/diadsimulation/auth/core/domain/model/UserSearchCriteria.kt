package com.diadsimulation.auth.core.domain.model

import com.diadsimulation.auth.core.domain.enums.SortDirection
import com.diadsimulation.auth.core.domain.enums.SortDirection.ASC

data class UserSearchCriteria(
    val page: Int = 0,
    val size: Int = 10,
    val sortBy: String = "name",
    val direction: SortDirection = ASC,
    val filterBy: String? = null,
    val filterValue: String? = null,
)
