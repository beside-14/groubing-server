package com.beside.groubing.groubingserver.global.domain.file.repository

import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfo
import com.beside.groubing.groubingserver.global.domain.file.domain.port.FileInfoQueryRepository
import com.beside.groubing.groubingserver.global.domain.file.exception.FileInfoInputException
import org.springframework.stereotype.Repository

@Repository
class FileInfoRepositoryAdapter(
    private val fileInfoJpaRepository: FileInfoJpaRepository
) : FileInfoQueryRepository {
    override fun findByFileName(fileName: String): FileInfo {
        return fileInfoJpaRepository.findFirstByFileName(fileName)?.toDomain()
            ?: throw FileInfoInputException()
    }
}
