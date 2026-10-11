package com.diadsimulation.auth.core.domain.model

import com.diadsimulation.auth.core.domain.enums.SortDirection.ASC
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class UserSearchCriteriaTest {
    @Test
    fun `should create search criteria with default values`() {
        val criteria = UserSearchCriteria()

        assertEquals(0, criteria.page)
        assertEquals(10, criteria.size)
        assertEquals("name", criteria.sortBy)
        assertEquals(ASC, criteria.direction)
        assertNull(criteria.filterBy)
        assertNull(criteria.filterValue)
        assertNull(criteria.dateField)
        assertNull(criteria.dateFrom)
        assertNull(criteria.dateTo)
    }
}
