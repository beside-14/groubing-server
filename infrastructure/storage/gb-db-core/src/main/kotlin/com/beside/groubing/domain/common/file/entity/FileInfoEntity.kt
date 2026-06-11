package com.beside.groubing.domain.common.file.entity

import com.beside.groubing.domain.common.file.domain.FileInfo
import com.beside.groubing.global.domain.jpa.BaseEntity
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
) : BaseEntity() {
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
