package org.careerseekers.userservice.mappers

import org.careerseekers.userservice.dto.docs.CreateMentorDocsTransferDto
import org.careerseekers.userservice.entities.MentorDocuments
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface MentorsDocumentsMapper {
    fun mentorDocsFromDto(o: CreateMentorDocsTransferDto): MentorDocuments
}