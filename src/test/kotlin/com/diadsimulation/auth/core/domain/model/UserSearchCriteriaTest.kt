package com.diadsimulation.auth.core.domain.model

import com.diadsimulation.auth.core.domain.enums.SortDirection.ASC
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class UserSearchCriteriaTest {
    @Test
    fun `should create search criteria with default values`() {
        val userSearchCriteria = UserSearchCriteria()

        assertEquals(0, userSearchCriteria.page)
        assertEquals(10, userSearchCriteria.size)
        assertEquals("name", userSearchCriteria.sortBy)
        assertEquals(ASC, userSearchCriteria.direction)
        assertNull(userSearchCriteria.filterBy)
        assertNull(userSearchCriteria.filterValue)
        assertNull(userSearchCriteria.dateField)
        assertNull(userSearchCriteria.dateFrom)
        assertNull(userSearchCriteria.dateTo)
        assertNull(userSearchCriteria.allowedRole)
        assertNull(userSearchCriteria.allowedUserId)
    }
}
