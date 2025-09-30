package org.careerseekers.userservice.mappers

import org.careerseekers.userservice.dto.users.links.CreateMentorLinkDto
import org.careerseekers.userservice.entities.MentorLinkBiscuits
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface MentorLinksBiscuitsMapper {
    fun objectFromDto(dto: CreateMentorLinkDto): MentorLinkBiscuits
}