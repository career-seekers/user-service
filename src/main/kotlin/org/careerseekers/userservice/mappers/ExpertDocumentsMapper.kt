package org.careerseekers.userservice.mappers

import org.careerseekers.userservice.dto.docs.CreateExpertDocsTransferDto
import org.careerseekers.userservice.entities.ExpertDocuments
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ExpertDocumentsMapper {
    fun expertDocsFromDto(o: CreateExpertDocsTransferDto): ExpertDocuments
}