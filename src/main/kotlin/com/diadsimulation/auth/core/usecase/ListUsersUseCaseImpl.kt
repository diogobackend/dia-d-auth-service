package com.diadsimulation.auth.core.usecase

import com.diadsimulation.auth.core.common.Messages.FILTER_PARAMETERS_REQUIRED
import com.diadsimulation.auth.core.domain.model.PagedResult
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.model.UserSearchCriteria
import com.diadsimulation.auth.core.port.input.ListUsersUseCase
import com.diadsimulation.auth.core.port.output.UserRepositoryPort
import org.springframework.stereotype.Service

@Service
class ListUsersUseCaseImpl(
    private val userRepositoryPort: UserRepositoryPort,
) : ListUsersUseCase {
    override fun execute(criteria: UserSearchCriteria): PagedResult<User> {
        require(
            (criteria.filterBy == null) == (criteria.filterValue == null),
        ) {
            FILTER_PARAMETERS_REQUIRED
        }

        return userRepositoryPort.findAll(criteria)
    }
}
