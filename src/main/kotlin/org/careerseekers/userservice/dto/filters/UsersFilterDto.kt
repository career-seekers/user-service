package org.careerseekers.userservice.dto.filters

import org.careerseekers.userservice.dto.FilterDtoClass
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.enums.VerificationStatuses

data class UsersFilterDto(
    val roles: List<UsersRoles>?,
    val verified: VerificationStatuses?,
) : FilterDtoClass
