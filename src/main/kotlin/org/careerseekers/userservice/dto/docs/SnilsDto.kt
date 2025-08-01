package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass
import org.springframework.web.multipart.MultipartFile

data class SnilsDto(
    val snilsNumber: String,
    val snilsFile: MultipartFile,
) : DtoClass
