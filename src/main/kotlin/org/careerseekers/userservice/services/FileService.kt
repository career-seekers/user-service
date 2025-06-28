package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.files.SaveFileDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@Service
class FileService(
    @Value("\${storage.location}") private val rootLocation: String,
) {
    private val rootPath: Path = Paths.get(rootLocation).toAbsolutePath().normalize()

    init {
        try {
            Files.createDirectories(rootPath)
        } catch (ex: IOException) {
            throw RuntimeException("Не удалось создать директорию для хранения файлов: $rootLocation", ex)
        }
    }

    fun store(file: MultipartFile): String {
        val originalFilename = StringUtils.cleanPath(file.originalFilename ?: "file")
        val extension = originalFilename.substringAfterLast('.', "")
        val uniqueName = UUID.randomUUID().toString() + if (extension.isNotEmpty()) ".$extension" else ""

        val destinationFile = rootPath.resolve(uniqueName).normalize()

        if (!destinationFile.startsWith(rootPath)) {
            throw RuntimeException("Неверный путь для сохранения файла: $uniqueName")
        }

        try {
            file.inputStream.use { input ->
                Files.copy(input, destinationFile, StandardCopyOption.REPLACE_EXISTING)
            }
        } catch (ex: IOException) {
            throw RuntimeException("Ошибка при сохранении файла $uniqueName", ex)
        }

        return destinationFile.toString()
    }
}