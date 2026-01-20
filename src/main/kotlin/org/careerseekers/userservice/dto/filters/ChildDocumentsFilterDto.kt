package org.careerseekers.userservice.dto.filters

import org.careerseekers.userservice.dto.FilterDtoClass
import org.careerseekers.userservice.enums.DirectionAgeCategory

data class ChildDocumentsFilterDto(
    val childId: Long?,
    val userId: Long?,
    val ageCategory: DirectionAgeCategory?,
) : FilterDtoClass
