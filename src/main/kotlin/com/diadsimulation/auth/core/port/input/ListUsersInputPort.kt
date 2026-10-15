package com.diadsimulation.auth.core.port.input

import com.diadsimulation.auth.core.domain.model.PagedResult
import com.diadsimulation.auth.core.domain.model.User
import com.diadsimulation.auth.core.domain.model.UserSearchCriteria

interface ListUsersInputPort {
    fun execute(criteria: UserSearchCriteria): PagedResult<User>
}
