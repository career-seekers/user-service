package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.dto.files.FileUploadResponse
import org.careerseekers.userservice.services.FileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController()
@RequestMapping("/api/v1/files")
class TestFilesController(private val storageService: FileService) {

    @PostMapping("/upload")
    fun uploadFile(@RequestPart("file") file: MultipartFile): ResponseEntity<FileUploadResponse> {
        val storedFilePath = storageService.store(file)

        val response = FileUploadResponse(
            originalFilename = file.originalFilename ?: "file",
            storedFilePath = storedFilePath,
            contentType = file.contentType ?: "application/octet-stream",
            size = file.size
        )

        return ResponseEntity.ok(response)
    }
}