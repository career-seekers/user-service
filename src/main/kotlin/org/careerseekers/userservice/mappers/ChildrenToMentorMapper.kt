package org.careerseekers.userservice.mappers

import org.careerseekers.userservice.dto.users.CreateMentorToChildDto
import org.careerseekers.userservice.entities.ChildToMentor
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ChildrenToMentorMapper {
    fun entityFromDto(o: CreateMentorToChildDto): ChildToMentor
}