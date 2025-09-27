package org.careerseekers.userservice.mappers

import org.careerseekers.userservice.dto.docs.CreateChildDocsDto
import org.careerseekers.userservice.entities.ChildDocuments
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ChildDocsMapper {
    fun childDocsFromDto(o: CreateChildDocsDto): ChildDocuments
}