package org.careerseekers.userservice.mappers

import org.careerseekers.userservice.dto.users.links.CreateTelegramLinksDto
import org.careerseekers.userservice.entities.TelegramLinks
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface TelegramLinksMapper {
    fun linkFromDto(o: CreateTelegramLinksDto): TelegramLinks
}