package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.enums.UsersRoles

data class ChangeUserRoleDto(
    val id: Long,
    val role: UsersRoles,
) : DtoClass
