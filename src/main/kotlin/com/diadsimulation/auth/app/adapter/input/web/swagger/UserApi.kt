package com.diadsimulation.auth.app.adapter.input.web.swagger

import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.FindUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.ListUsersRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.RegisterUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.UpdateUserAccessRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.UpdateUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.FindUserResponse
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.ListUsersResponse
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.PagedResponse
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.RegisterUserResponse
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.UpdateUserAccessUserResponse
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.UpdateUserResponse
import com.diadsimulation.auth.app.adapter.input.web.exception.ApiErrorResponse
import com.diadsimulation.auth.core.common.Messages.INTERNAL_SERVER_ERROR_MESSAGE
import com.diadsimulation.auth.core.common.Messages.INVALID_REQUEST_DATA
import com.diadsimulation.auth.core.common.Messages.USER_NOT_FOUND
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@Tag(
    name = "Users",
    description = "Operações relacionadas ao gerenciamento de usuários",
)
interface UserApi {
    @Operation(
        summary = "Cadastrar usuário",
        description = "Cria um novo usuário na plataforma com o perfil CANDIDATE.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Usuário cadastrado com sucesso",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = RegisterUserResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = INVALID_REQUEST_DATA,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "409",
                description = "E-mail, login, CPF ou RG já cadastrado",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "500",
                description = INTERNAL_SERVER_ERROR_MESSAGE,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun register(
        @Valid @RequestBody request: RegisterUserRequest,
    ): ResponseEntity<RegisterUserResponse>

    @Operation(
        summary = "Listar usuários",
        description =
            "Lista usuários com paginação, ordenação ASC/DESC e filtros parciais. " +
                "A pesquisa ignora maiúsculas e minúsculas e considera apenas caracteres consecutivos. " +
                "PLATFORM_ADMIN pode listar todos os usuários. " +
                "SCHOOL_ADMIN pode listar candidatos e seu próprio usuário. " +
                "CANDIDATE não possui permissão para listar usuários.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Usuários listados com sucesso",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = PagedResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = INVALID_REQUEST_DATA,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Autenticação necessária ou token inválido",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Usuário sem permissão para listar usuários",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "500",
                description = INTERNAL_SERVER_ERROR_MESSAGE,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun listUsers(
        @Valid @ParameterObject @ModelAttribute request: ListUsersRequest,
    ): ResponseEntity<PagedResponse<ListUsersResponse>>

    @Operation(
        summary = "Buscar usuário",
        description =
            "Busca um único usuário por userId, e-mail, login, CPF ou RG. " +
                "Apenas um parâmetro deve ser informado. " +
                "PLATFORM_ADMIN pode consultar qualquer usuário. " +
                "SCHOOL_ADMIN pode consultar candidatos e seu próprio usuário. " +
                "CANDIDATE pode consultar apenas seu próprio usuário.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Usuário encontrado com sucesso",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = FindUserResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = INVALID_REQUEST_DATA,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Autenticação necessária ou token inválido",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Usuário sem permissão para consultar o usuário solicitado",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = USER_NOT_FOUND,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "500",
                description = INTERNAL_SERVER_ERROR_MESSAGE,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun findUser(
        @Valid @ParameterObject @ModelAttribute request: FindUserRequest,
    ): ResponseEntity<FindUserResponse>

    @Operation(
        summary = "Atualizar usuário",
        description =
            "Atualiza parcialmente os dados de perfil de um usuário. " +
                "PLATFORM_ADMIN pode atualizar qualquer usuário. " +
                "SCHOOL_ADMIN pode atualizar candidatos e seu próprio usuário. " +
                "CANDIDATE pode atualizar apenas seu próprio usuário.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Usuário atualizado com sucesso",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UpdateUserResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = INVALID_REQUEST_DATA,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Autenticação necessária ou token inválido",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Usuário sem permissão para atualizar o usuário solicitado",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = USER_NOT_FOUND,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "409",
                description = "E-mail, login, CPF ou RG já cadastrado ou documento já registrado",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "500",
                description = INTERNAL_SERVER_ERROR_MESSAGE,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun updateUser(
        @PathVariable userId: UUID,
        @Valid @RequestBody request: UpdateUserRequest,
    ): ResponseEntity<UpdateUserResponse>

    @Operation(
        summary = "Excluir usuário",
        description =
            "Exclui permanentemente um usuário pelo ID. " +
                "PLATFORM_ADMIN pode excluir qualquer usuário. " +
                "SCHOOL_ADMIN pode excluir candidatos e seu próprio usuário. " +
                "CANDIDATE não possui permissão para excluir usuários.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Usuário excluído com sucesso",
            ),
            ApiResponse(
                responseCode = "400",
                description = INVALID_REQUEST_DATA,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Autenticação necessária ou token inválido",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Usuário sem permissão para excluir o usuário solicitado",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = USER_NOT_FOUND,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "500",
                description = INTERNAL_SERVER_ERROR_MESSAGE,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun deleteUser(userId: UUID)

    @Operation(
        summary = "Atualizar acesso do usuário",
        description =
            "Atualiza parcialmente o perfil de acesso (role) e/ou o status de um usuário. " +
                "Operação exclusiva do PLATFORM_ADMIN.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Acesso do usuário atualizado com sucesso",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UpdateUserAccessUserResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = INVALID_REQUEST_DATA,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Autenticação necessária ou token inválido",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Operação permitida apenas para PLATFORM_ADMIN",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = USER_NOT_FOUND,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "500",
                description = INTERNAL_SERVER_ERROR_MESSAGE,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun updateUserAccess(
        @PathVariable userId: UUID,
        @Valid @RequestBody request: UpdateUserAccessRequest,
    ): ResponseEntity<UpdateUserAccessUserResponse>
}
