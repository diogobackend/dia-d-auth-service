package com.diadsimulation.auth.app.adapter.input.web.controllers

import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.ListUsersRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.RegisterUserRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.ListUsersResponse
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.PagedResponse
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.RegisterUserResponse
import com.diadsimulation.auth.app.adapter.input.web.mappers.ListUsersMapper
import com.diadsimulation.auth.app.adapter.input.web.mappers.RegisterUserMapper
import com.diadsimulation.auth.app.adapter.input.web.swagger.UserApi
import com.diadsimulation.auth.core.domain.enums.SortDirection
import com.diadsimulation.auth.core.domain.model.UserSearchCriteria
import com.diadsimulation.auth.core.port.input.ListUsersUseCase
import com.diadsimulation.auth.core.port.input.RegisterUserUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val listUsersUseCase: ListUsersUseCase,
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
    override fun listUsers(
        @Valid @ModelAttribute request: ListUsersRequest,
    ): ResponseEntity<PagedResponse<ListUsersResponse>> {
        val criteria =
            UserSearchCriteria(
                page = request.page,
                size = request.size,
                sortBy = request.sortBy,
                direction = SortDirection.valueOf(request.direction.name),
                filterBy = request.filterBy,
                filterValue = request.filterValue,
            )

        val result = listUsersUseCase.execute(criteria)

        return ResponseEntity.ok(
            PagedResponse(
                content = result.content.map(ListUsersMapper::toResponse),
                page = result.page,
                size = result.size,
                totalElements = result.content.size,
                totalPages = result.totalPages,
            ),
        )
    }
}
