package com.diadsimulation.auth.app.adapter.input.web.swagger

import com.diadsimulation.auth.app.adapter.input.web.dtos.RegisterUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.RegisterUserResponse
import com.diadsimulation.auth.app.adapter.input.web.exception.ApiErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

@Tag(
    name = "Users",
    description = "Operações relacionadas ao gerenciamento de usuários"
)
interface UserApi {

    @Operation(
        summary = "Cadastrar usuário",
        description = "Cria um novo usuário na plataforma."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Usuário cadastrado com sucesso",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(
                            implementation = RegisterUserResponse::class
                        )
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Dados da requisição inválidos",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(
                            implementation = ApiErrorResponse::class
                        )
                    )
                ]
            ),
            ApiResponse(
                responseCode = "409",
                description = "E-mail, CPF ou RG já cadastrado",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(
                            implementation = ApiErrorResponse::class
                        )
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Erro interno do servidor",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(
                            implementation = ApiErrorResponse::class
                        )
                    )
                ]
            )
        ]
    )
    fun register(
        @Valid @RequestBody request: RegisterUserRequest
    ): ResponseEntity<RegisterUserResponse>
}