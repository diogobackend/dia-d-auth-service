package com.diadsimulation.auth.app.adapter.input.web.dtos.responses

data class LoginResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
)
