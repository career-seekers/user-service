package org.careerseekers.userservice.mappers

import org.careerseekers.userservice.dto.jwt.SaveRefreshTokenDto
import org.careerseekers.userservice.entities.JwtTokensStorage
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface JwtTokensStorageMapper {
    fun tokenFromSaveRefreshDto(o: SaveRefreshTokenDto): JwtTokensStorage
}