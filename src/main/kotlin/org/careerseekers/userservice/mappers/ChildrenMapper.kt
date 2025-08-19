package org.careerseekers.userservice.mappers

import org.careerseekers.userservice.dto.users.CreateChildDto
import org.careerseekers.userservice.entities.Children
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ChildrenMapper {
    fun childFromDto(o: CreateChildDto): Children
}