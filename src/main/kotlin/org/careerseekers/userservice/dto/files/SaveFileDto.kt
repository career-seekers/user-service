package org.careerseekers.userservice.dto.files

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.enums.FileTypes
import org.springframework.web.multipart.MultipartFile

data class SaveFileDto(
    val file: MultipartFile,
    val fileType: FileTypes,
) : DtoClass