package com.diadsimulation.auth.app.adapter.input.web.controllers

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
import com.diadsimulation.auth.app.adapter.input.web.mappers.FindUserMapper
import com.diadsimulation.auth.app.adapter.input.web.mappers.ListUsersMapper
import com.diadsimulation.auth.app.adapter.input.web.mappers.RegisterUserMapper
import com.diadsimulation.auth.app.adapter.input.web.mappers.UpdateUserAccessMapper
import com.diadsimulation.auth.app.adapter.input.web.mappers.UpdateUserMapper
import com.diadsimulation.auth.app.adapter.input.web.security.UserAccessAuthorization
import com.diadsimulation.auth.app.adapter.input.web.swagger.UserApi
import com.diadsimulation.auth.core.domain.enums.SortDirection
import com.diadsimulation.auth.core.domain.model.UserSearchCriteria
import com.diadsimulation.auth.core.port.input.DeleteUserInputPort
import com.diadsimulation.auth.core.port.input.FindUserInputPort
import com.diadsimulation.auth.core.port.input.ListUsersInputPort
import com.diadsimulation.auth.core.port.input.RegisterUserInputPort
import com.diadsimulation.auth.core.port.input.UpdateUserAccessInputPort
import com.diadsimulation.auth.core.port.input.UpdateUserInputPort
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    private val registerUserInputPort: RegisterUserInputPort,
    private val listUsersInputPort: ListUsersInputPort,
    private val findUserInputPort: FindUserInputPort,
    private val updateUserInputPort: UpdateUserInputPort,
    private val deleteUserInputPort: DeleteUserInputPort,
    private val updateUserAccessInputPort: UpdateUserAccessInputPort,
    private val userAccessAuthorization: UserAccessAuthorization,
) : UserApi {
    @PostMapping
    override fun register(request: RegisterUserRequest): ResponseEntity<RegisterUserResponse> {
        val user = RegisterUserMapper.toDomain(request)
        val registeredUser = registerUserInputPort.execute(user)

        return ResponseEntity
            .status(CREATED)
            .body(RegisterUserMapper.toResponse(registeredUser))
    }

    @GetMapping
    override fun listUsers(request: ListUsersRequest): ResponseEntity<PagedResponse<ListUsersResponse>> {
        val (allowedRole, allowedUserId) = userAccessAuthorization.getListAccess()

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
                login = request.login,
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
                allowedRole = allowedRole,
                allowedUserId = allowedUserId,
            )

        val result = listUsersInputPort.execute(criteria)

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
            findUserInputPort.execute(
                userId = request.userId,
                email = request.email,
                login = request.login,
                cpf = request.cpf,
                rg = request.rg,
            )
        userAccessAuthorization.authorize(user.userId)
        return ResponseEntity.ok(
            FindUserMapper.toResponse(user),
        )
    }

    @PatchMapping("/{userId}")
    override fun updateUser(
        @PathVariable userId: UUID,
        @Valid @RequestBody request: UpdateUserRequest,
    ): ResponseEntity<UpdateUserResponse> {
        userAccessAuthorization.authorize(userId)
        val user =
            updateUserInputPort.execute(
                userId = userId,
                name = request.name,
                email = request.email,
                login = request.login,
                password = request.password,
                phone = request.phone,
                address = request.address,
                cpf = request.cpf,
                rg = request.rg,
                birthDate = request.birthDate,
            )

        return ResponseEntity.ok(UpdateUserMapper.toResponse(user))
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(NO_CONTENT)
    override fun deleteUser(
        @PathVariable userId: UUID,
    ) {
        userAccessAuthorization.authorize(userId)
        deleteUserInputPort.execute(userId)
    }

    @PatchMapping("/{userId}/access")
    override fun updateUserAccess(
        userId: UUID,
        request: UpdateUserAccessRequest,
    ): ResponseEntity<UpdateUserAccessUserResponse> {
        val user =
            updateUserAccessInputPort.execute(
                userId = userId,
                role = request.role,
                status = request.status,
            )
        return ResponseEntity.ok(UpdateUserAccessMapper.toResponse(user))
    }
}
