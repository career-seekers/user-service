package org.careerseekers.userservice.mappers

import org.careerseekers.userservice.dto.docs.CreateTutorDocsDto
import org.careerseekers.userservice.entities.TutorDocuments
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface TutorDocumentsMapper {
    fun tutorDocsFromDto(o: CreateTutorDocsDto): TutorDocuments
}