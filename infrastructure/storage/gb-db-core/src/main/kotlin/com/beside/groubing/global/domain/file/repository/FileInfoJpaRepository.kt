package com.beside.groubing.global.domain.file.repository

import com.beside.groubing.global.domain.file.entity.FileInfoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FileInfoJpaRepository : JpaRepository<FileInfoEntity, Long> {
    fun findFirstByFileName(fileName: String): FileInfoEntity?
}
