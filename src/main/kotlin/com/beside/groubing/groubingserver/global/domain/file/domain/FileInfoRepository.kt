package com.beside.groubing.groubingserver.global.domain.file.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileInfoRepository : JpaRepository<FileInfo, Long> {
    fun findFirstByFileName(fileName: String): FileInfo?
}
