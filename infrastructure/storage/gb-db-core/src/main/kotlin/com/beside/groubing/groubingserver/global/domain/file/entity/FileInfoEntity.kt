package com.beside.groubing.groubingserver.global.domain.file.entity

import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfo
import com.beside.groubing.groubingserver.global.domain.jpa.BaseCreatedTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "FILE_INFOS")
class FileInfoEntity private constructor(
    val directory: String,
    val fileName: String,
    val originalName: String
) : BaseCreatedTimeEntity() {
    @Id
    @Column(name = "FILE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    fun toDomain(): FileInfo = FileInfo.of(
        id = id,
        directory = directory,
        fileName = fileName,
        originalName = originalName
    )

    companion object {
        fun from(fileInfo: FileInfo): FileInfoEntity = FileInfoEntity(
            directory = fileInfo.directory,
            fileName = fileInfo.fileName,
            originalName = fileInfo.originalName
        )
    }
}
