package com.diadsimulation.auth.builders

import com.diadsimulation.auth.core.domain.model.PagedResult
import java.util.Collections.emptyList

fun <T> buildPagedResult(
    content: List<T> = emptyList(),
    page: Int = 0,
    size: Int = 10,
    totalPages: Int = 0,
    totalElements: Long = 0L,
): PagedResult<T> =
    PagedResult(
        content = content,
        page = page,
        size = size,
        totalPages = totalPages,
        totalElements = totalElements,
    )
