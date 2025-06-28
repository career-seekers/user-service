package org.careerseekers.userservice.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.careerseekers.userservice.enums.FileTypes
import java.util.UUID

@Entity
@Table(name = "files_storage")
data class FilesStorage(
    @Id
    @GeneratedValue
    val id: Long,

    @Column(nullable = false)
    var originalFilename: String,

    @Column(nullable = false, unique = true)
    var storedFilename: UUID,

    @Column(nullable = false)
    var contentType: String,

    @Column(nullable = false)
    var size: Long,

    @Column(nullable = false)
    var fileType: FileTypes,

    @Column(nullable = false, columnDefinition = "TEXT")
    var filePath: String,
)
