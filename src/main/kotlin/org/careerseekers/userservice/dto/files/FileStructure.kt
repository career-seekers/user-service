package org.careerseekers.userservice.dto.files

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.enums.FileTypes

data class FileStructure(
    val id: Long,
    val originalFilename: String,
    val storedFilename: String,
    val contentType: String,
    val fileType: FileTypes,
    val filePath: String,
    val verified: Boolean,
) : DtoClass
