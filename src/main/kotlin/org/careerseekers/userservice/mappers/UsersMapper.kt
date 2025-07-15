package org.careerseekers.userservice.mappers

import org.careerseekers.userservice.dto.users.CreateUserDto
import org.careerseekers.userservice.entities.Users
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface UsersMapper {
    fun usersFromCreateDto(o: CreateUserDto): Users
}