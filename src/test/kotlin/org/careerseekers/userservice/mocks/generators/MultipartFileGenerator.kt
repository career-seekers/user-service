package org.careerseekers.userservice.mocks.generators

import org.careerseekers.userservice.mocks.MocksGenerator.randomString
import org.springframework.mock.web.MockMultipartFile

object MultipartFileGenerator {
    fun createMultipartFile() = MockMultipartFile(
        randomString(12),
        randomString(12),
        randomString(12),
        ByteArray(0)
    )
}