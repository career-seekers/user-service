package org.careerseekers.userservice.dto.filters

import org.careerseekers.userservice.dto.FilterDtoClass

data class ChildrenFilterDto(
    val name: String? = null,
) : FilterDtoClass