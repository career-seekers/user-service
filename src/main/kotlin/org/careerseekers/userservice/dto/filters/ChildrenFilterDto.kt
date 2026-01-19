package org.careerseekers.userservice.dto.filters

import org.careerseekers.userservice.dto.FilterDtoClass
import java.util.Date

data class ChildrenFilterDto(
    val name: String? = null,
    val dateOfBirth: Date? = null,
) : FilterDtoClass