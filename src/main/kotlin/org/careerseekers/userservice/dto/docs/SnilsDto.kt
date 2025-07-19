package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass
import org.springframework.http.codec.multipart.FilePart

data class SnilsDto(
    val snilsNumber: String,
    val snilsFile: FilePart,
) : DtoClass
