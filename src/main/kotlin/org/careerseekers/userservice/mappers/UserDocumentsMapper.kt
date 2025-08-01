package org.careerseekers.userservice.mappers

import org.careerseekers.userservice.dto.docs.CreateUserDocsTransferDto
import org.careerseekers.userservice.entities.UserDocuments
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface UserDocumentsMapper {
    fun userDocsFromDto(o: CreateUserDocsTransferDto): UserDocuments
}