package com.diadsimulation.auth.app.adapter.input.web.dtos.responses

data class PagedResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int,
)
