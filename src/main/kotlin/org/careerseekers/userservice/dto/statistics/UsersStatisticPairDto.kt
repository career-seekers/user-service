package org.careerseekers.userservice.dto.statistics

import org.careerseekers.userservice.dto.DtoClass

data class UsersStatisticPairDto(
    val count: Int,
    val verified: Int,
) : DtoClass