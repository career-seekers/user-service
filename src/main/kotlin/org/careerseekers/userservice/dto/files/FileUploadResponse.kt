package org.careerseekers.userservice.dto.files

import org.careerseekers.userservice.dto.DtoClass

data class FileUploadResponse(
    val originalFilename: String,
    val storedFilePath: String,
    val contentType: String,
    val size: Long
) : DtoClass