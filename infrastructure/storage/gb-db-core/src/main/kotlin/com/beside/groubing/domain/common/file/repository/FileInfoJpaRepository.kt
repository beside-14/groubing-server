package com.beside.groubing.domain.common.file.repository

import com.beside.groubing.domain.common.file.entity.FileInfoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FileInfoJpaRepository : JpaRepository<FileInfoEntity, Long> {
    fun findByFileName(fileName: String): FileInfoEntity?
}
