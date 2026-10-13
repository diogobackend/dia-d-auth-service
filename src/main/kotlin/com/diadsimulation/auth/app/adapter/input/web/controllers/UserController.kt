package com.diadsimulation.auth.app.adapter.input.web.controllers

import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.FindUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.ListUsersRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.RegisterUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.UpdateUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.FindUserResponse
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.ListUsersResponse
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.PagedResponse
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.RegisterUserResponse
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.UpdateUserResponse
import com.diadsimulation.auth.app.adapter.input.web.mappers.FindUserMapper
import com.diadsimulation.auth.app.adapter.input.web.mappers.ListUsersMapper
import com.diadsimulation.auth.app.adapter.input.web.mappers.RegisterUserMapper
import com.diadsimulation.auth.app.adapter.input.web.mappers.UpdateUserMapper
import com.diadsimulation.auth.app.adapter.input.web.swagger.UserApi
import com.diadsimulation.auth.core.domain.enums.SortDirection
import com.diadsimulation.auth.core.domain.model.UserSearchCriteria
import com.diadsimulation.auth.core.port.input.DeleteUserUseCase
import com.diadsimulation.auth.core.port.input.FindUserUseCase
import com.diadsimulation.auth.core.port.input.ListUsersUseCase
import com.diadsimulation.auth.core.port.input.RegisterUserUseCase
import com.diadsimulation.auth.core.port.input.UpdateUserUseCase
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val listUsersUseCase: ListUsersUseCase,
    private val findUserUseCase: FindUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
) : UserApi {
    @PostMapping
    override fun register(request: RegisterUserRequest): ResponseEntity<RegisterUserResponse> {
        val user = RegisterUserMapper.toDomain(request)
        val registeredUser = registerUserUseCase.execute(user)

        return ResponseEntity
            .status(CREATED)
            .body(RegisterUserMapper.toResponse(registeredUser))
    }

    @GetMapping
    override fun listUsers(request: ListUsersRequest): ResponseEntity<PagedResponse<ListUsersResponse>> {
        val criteria =
            UserSearchCriteria(
                page = request.page,
                size = request.size,
                sortBy = request.sortBy,
                direction = SortDirection.valueOf(request.direction.name),
                filterBy = request.filterBy,
                filterValue = request.filterValue,
                name = request.name,
                email = request.email,
                phone = request.phone,
                cpf = request.cpf,
                rg = request.rg,
                address = request.address,
                role = request.role,
                status = request.status,
                dateField = request.dateField,
                dateFrom = request.dateFrom,
                dateTo = request.dateTo,
                dateExact = request.dateExact,
                birthDate = request.birthDate,
                createdAt = request.createdAt,
                updatedAt = request.updatedAt,
            )

        val result = listUsersUseCase.execute(criteria)

        return ResponseEntity.ok(
            PagedResponse(
                content = result.content.map(ListUsersMapper::toResponse),
                page = result.page,
                size = result.size,
                totalElementsThisPage = result.content.size,
                totalElements = result.totalElements,
                totalPages = result.totalPages,
            ),
        )
    }

    @GetMapping("/id")
    override fun findUser(request: FindUserRequest): ResponseEntity<FindUserResponse> {
        val user =
            findUserUseCase.execute(
                userId = request.userId,
                email = request.email,
                cpf = request.cpf,
                rg = request.rg,
            )

        return ResponseEntity.ok(
            FindUserMapper.toResponse(user),
        )
    }

    @PatchMapping("/{userId}")
    override fun updateUser(
        userId: UUID,
        request: UpdateUserRequest,
    ): ResponseEntity<UpdateUserResponse> {
        val user =
            updateUserUseCase.execute(
                userId = userId,
                name = request.name,
                email = request.email,
                password = request.password,
                phone = request.phone,
                address = request.address,
            )

        return ResponseEntity.ok(UpdateUserMapper.toResponse(user))
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(NO_CONTENT)
    override fun deleteUser(
        @PathVariable userId: UUID,
    ) {
        deleteUserUseCase.execute(userId)
    }
}
