package org.careerseekers.userservice.mocks.generators

import org.careerseekers.userservice.mocks.MocksGenerator.randomString
import org.careerseekers.userservice.dto.files.FileStructure
import org.careerseekers.userservice.enums.FileTypes
import java.util.UUID
import kotlin.random.Random

object FileStructureGenerator {
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