package org.careerseekers.userservice

import MocksGenerator.randomString
import org.careerseekers.userservice.dto.files.FileStructure
import org.careerseekers.userservice.enums.FileTypes
import java.util.UUID
import kotlin.random.Random

object FileStructureCreator {
    fun createFileStructure(fileType: FileTypes): FileStructure {
        return FileStructure(
            id = Random.nextLong(1, 10000),
            originalFilename = randomString(10),
            storedFilename = UUID.randomUUID().toString(),
            contentType = randomString(10),
            fileType = fileType,
            filePath = randomString(10),
        )
    }
}