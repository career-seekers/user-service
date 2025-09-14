package org.careerseekers.userservice.mappers

import org.careerseekers.userservice.dto.docs.CreateExpertDocsDto
import org.careerseekers.userservice.entities.ExpertDocuments
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ExpertDocumentsMapper {
    fun expertDocsFromDto(o: CreateExpertDocsDto): ExpertDocuments
}